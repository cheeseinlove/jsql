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
package io.jsql.sql.response;

import io.jsql.config.Fields;
import io.jsql.mysql.PacketUtil;
import io.jsql.mysql.mysql.*;
import io.jsql.sql.OConnection;
import io.jsql.util.StringUtil;

import java.util.List;

/**
 * @author changhong
 *         这个类用来简化常用的功能，
 *         select 只还回一个列
 */
public class MSelect1Response {

    private static final int FIELD_COUNT = 1;
    private static final ResultSetHeaderPacket header = PacketUtil.getHeader(FIELD_COUNT);
    private static final FieldPacket[] fields = new FieldPacket[FIELD_COUNT];
    private static final EOFPacket eof = new EOFPacket();

    static {
        int i = 0;
        byte packetId = 0;
        header.packetId = ++packetId;
//        fields[i] = PacketUtil.getField("DATABASE", Fields.FIELD_TYPE_VAR_STRING);
//        fields[i++].packetId = ++packetId;
        packetId++;
        eof.packetId = ++packetId;
    }

    public static void response(OConnection c, String columnname, List<String> columnvaluess) {
        fields[0] = PacketUtil.getField(columnname, Fields.FIELD_TYPE_VAR_STRING);
        fields[0].packetId = 2;

        // write rows
        byte packetId = eof.packetId;

        MySQLPacket[] rows = new MySQLPacket[columnvaluess.size()];
        int index = 0;
        for (String name : columnvaluess) {
            RowDataPacket row = new RowDataPacket(FIELD_COUNT);
            row.add(StringUtil.encode(name, c.charset));
            row.packetId = ++packetId;
//                buffer = row.write(buffer, c, true);
            rows[index++] = row;
        }
        // write last eof
        EOFPacket lastEof = new EOFPacket();
        lastEof.packetId = ++packetId;
        c.writeResultSet(header, fields, eof, rows, lastEof);
    }

}