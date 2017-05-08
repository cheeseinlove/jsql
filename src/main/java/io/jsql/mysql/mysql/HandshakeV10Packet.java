/*
 * Copyright (c) 2013, OpenCloudDB/MyCAT and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software;Designed and Developed mainly by many Chinese 
 * opensource volunteers. you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License version 2 only, as published by the
 * Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Any questions about this component can be directed to it's project Web address 
 * https://code.google.com/p/opencloudb/.
 *
 */
package io.jsql.mysql.mysql;

import io.jsql.config.Capabilities;
import io.jsql.mysql.MBufferUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

/**
 * Connection Phase
 The Connection Phase performs these tasks:

 exchange the capabilities of client and server
 setup SSL communication channel if requested
 authenticate the client against the server
 It starts with the client connect()ing to the server which may send a ERR packet and finish the handshake or
 send a Initial Handshake Packet which the client answers with a Handshake Response Packet. At this stage
 client can request SSL connection, in which case an SSL communication channel
 is established before client sends its authentication response.

 Note
 In case the server sent a ERR packet as first packet it will happen before the
 client and server negotiated any capabilities. Therefore the ERR packet will not contain the SQL-state.

 * From jsql server to client during initial handshake.
 * <p>
 * <pre>
 * Bytes                        Name
 * -----                        ----
 * 1                            protocol_version (always 0x0a)
 * n (string[NULL])             server_version
 * 4                            thread_id
 * 8 (string[8])                auth-plugin-data-part-1
 * 1                            (filler) always 0x00
 * 2                            capability flags (lower 2 bytes)
 *   if more data in the packet:
 * 1                            character set
 * 2                            status flags
 * 2                            capability flags (upper 2 bytes)
 *   if capabilities & CLIENT_PLUGIN_AUTH {
 * 1                            length of auth-plugin-data
 *   } else {
 * 1                            0x00
 *   }
 * 10 (string[10])              reserved (all 0x00)
 *   if capabilities & CLIENT_SECURE_CONNECTION {
 * string[$len]   auth-plugin-data-part-2 ($len=MAX(13, length of auth-plugin-data - 8))
 *   }
 *   if capabilities & CLIENT_PLUGIN_AUTH {
 * string[NUL]    auth-plugin name
 * }
 *
 * @see http://dev.mysql.com/doc/internals/en/connection-phase-packets.html#Protocol::HandshakeV10
 * </pre>
 *
 * @author CrazyPig
 * @author changhong
 * @since 2016-11-13
 */
public class HandshakeV10Packet extends MySQLPacket {
    private static final byte[] FILLER_10 = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final byte[] DEFAULT_AUTH_PLUGIN_NAME = "mysql_native_password".getBytes();
    public final byte[] authPluginName = DEFAULT_AUTH_PLUGIN_NAME;
    public byte protocolVersion;
    public byte[] serverVersion;
    public long threadId;
    public byte[] seed; // auth-plugin-data-part-1
    public int serverCapabilities;
    public byte serverCharsetIndex;
    public int serverStatus;
    public byte[] restOfScrambleBuff; // auth-plugin-data-part-2

    public void write(Channel c) {

        ByteBuf buffer = Unpooled.buffer(100);
        MBufferUtil.writeUB3(buffer, calcPacketSize());
        buffer.writeByte(packetId);
        buffer.writeByte(protocolVersion);
        MBufferUtil.writeWithNull(buffer, serverVersion);
        MBufferUtil.writeUB4(buffer, threadId);
        buffer.writeBytes(seed);
        buffer.writeByte((byte) 0); // [00] filler
        MBufferUtil.writeUB2(buffer, serverCapabilities); // capability flags (lower 2 bytes)
        buffer.writeByte(serverCharsetIndex);
        MBufferUtil.writeUB2(buffer, serverStatus);
        MBufferUtil.writeUB2(buffer, (serverCapabilities >> 16)); // capability flags (upper 2 bytes)
        if ((serverCapabilities & Capabilities.CLIENT_PLUGIN_AUTH) != 0) {
            if (restOfScrambleBuff.length <= 13) {
                buffer.writeByte((byte) (seed.length + 13));
            } else {
                buffer.writeByte((byte) (seed.length + restOfScrambleBuff.length));
            }
        } else {
            buffer.writeByte((byte) 0);
        }
        buffer.writeBytes(FILLER_10);
        if ((serverCapabilities & Capabilities.CLIENT_SECURE_CONNECTION) != 0) {
            buffer.writeBytes(restOfScrambleBuff);
            // restOfScrambleBuff.length always to be 12
            if (restOfScrambleBuff.length < 13) {
                for (int i = 13 - restOfScrambleBuff.length; i > 0; i--) {
                    buffer.writeByte((byte) 0);
                }
            }
        }
        if ((serverCapabilities & Capabilities.CLIENT_PLUGIN_AUTH) != 0) {
            MBufferUtil.writeWithNull(buffer, authPluginName);
        }
        c.writeAndFlush(buffer);
    }

    @Override
    public void read(byte[] data) {
        throw new NullPointerException();
    }

    @Override
    public int calcPacketSize() {
        int size = 1; // protocol version
        size += (serverVersion.length + 1); // server version
        size += 4; // connection id
        size += seed.length;
        size += 1; // [00] filler
        size += 2; // capability flags (lower 2 bytes)
        size += 1; // character set
        size += 2; // status flags
        size += 2; // capability flags (upper 2 bytes)
        size += 1;
        size += 10; // reserved (all [00])
        if ((serverCapabilities & Capabilities.CLIENT_SECURE_CONNECTION) != 0) {
            // restOfScrambleBuff.length always to be 12
            if (restOfScrambleBuff.length <= 13) {
                size += 13;
            } else {
                size += restOfScrambleBuff.length;
            }
        }
        if ((serverCapabilities & Capabilities.CLIENT_PLUGIN_AUTH) != 0) {
            size += (authPluginName.length + 1); // auth-plugin name
        }
        return size;
    }

    @Override
    protected String getPacketInfo() {
        return "MySQL HandshakeV10 Packet";
    }

}