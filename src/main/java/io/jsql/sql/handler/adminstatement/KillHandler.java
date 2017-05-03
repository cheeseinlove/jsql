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
package io.jsql.sql.handler.adminstatement;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlKillStatement;
import io.jsql.config.ErrorCode;
import io.jsql.mysql.mysql.OkPacket;
import io.jsql.sql.OConnection;
import io.jsql.sql.handler.SqlStatementHander;
import io.jsql.util.StringUtil;
import org.springframework.stereotype.Component;

/**
 * @author jsql
 */
@Component
public class KillHandler extends SqlStatementHander{

    public static void handle(String stmt, int offset, OConnection c) {
        String id = stmt.substring(offset).trim();
        if (StringUtil.isEmpty(id)) {
            c.writeErrMessage(ErrorCode.ER_NO_SUCH_THREAD, "NULL connection id");
        } else {
            // get value
            long value = 0;
            try {
                value = Long.parseLong(id);
            } catch (NumberFormatException e) {
                c.writeErrMessage(ErrorCode.ER_NO_SUCH_THREAD, "Invalid connection id:" + id);
            }

//            // kill myself
//            if (value == c.getId()) {
//                getOkPacket().write(c);
//                c.write(c.allocate());
//                return;
//            }
//
//            // get connection and close it
//            FrontendConnection fc = null;
//            NIOProcessor[] processors = MycatServer.getInstance().getProcessors();
//            for (NIOProcessor p : processors) {
//                if ((fc = p.getFrontends().get(value)) != null) {
//                    break;
//                }
//            }
//            if (fc != null) {
//                fc.close("killed");
//                getOkPacket().write(c);
//            } else {
//                c.writeErrMessage(ErrorCode.ER_NO_SUCH_THREAD, "Unknown connection id:" + id);
//            }
        }
    }

    private static OkPacket getOkPacket() {
        OkPacket packet = new OkPacket();
        packet.packetId = 1;
        packet.affectedRows = 0;
        packet.serverStatus = 2;
        return packet;
    }

    public static void handle(MySqlKillStatement x, OConnection c) {
        String id = x.getThreadId().toString();
        if (StringUtil.isEmpty(id)) {
            c.writeErrMessage(ErrorCode.ER_NO_SUCH_THREAD, "NULL connection id");
        } else {
            // get value
            long value = 0;
            try {
                value = Long.parseLong(id);
            } catch (NumberFormatException e) {
                c.writeErrMessage(ErrorCode.ER_NO_SUCH_THREAD, "Invalid connection id:" + id);
            }

//            // kill myself
//            if (value == c.getId()) {
//                getOkPacket().write(c);
//                c.write(c.allocate());
//                return;
//            }
//
//            // get connection and close it
//            FrontendConnection fc = null;
//            NIOProcessor[] processors = MycatServer.getInstance().getProcessors();
//            for (NIOProcessor p : processors) {
//                if ((fc = p.getFrontends().get(value)) != null) {
//                    break;
//                }
//            }
//            if (fc != null) {
//                fc.close("killed");
//                getOkPacket().write(c);
//            } else {
//                c.writeErrMessage(ErrorCode.ER_NO_SUCH_THREAD, "Unknown connection id:" + id);
//            }
//        }
        }
    }

    @Override
    public Class<? extends SQLStatement> supportSQLstatement() {
        return MySqlKillStatement.class;
    }

    @Override
    protected Object handle(SQLStatement sqlStatement) throws Exception {
        return null;
    }
}