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
package io.jsql.mysql.handler;

import io.jsql.config.ErrorCode;
import io.jsql.mysql.CharsetUtil;
import io.jsql.mysql.mysql.AuthPacket;
import io.jsql.mysql.mysql.MySQLPacket;
import io.jsql.sql.OConnection;
import io.jsql.storage.DB;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 前端认证处理器
 * 处理auth包
 */
@Component
@Scope("prototype")
public class MysqlAuthHander implements MysqlPacketHander {

    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlAuthHander.class);
    private static final byte[] AUTH_OK = new byte[]{7, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0};
    private  OConnection source;
    public MysqlAuthHander( ) {
    }
    public void setSource(OConnection source) {
        this.source = source;
    }
    public void handle(AuthPacket auth) {
        // check quit packet
//        if (data.length == QuitPacket.QUIT.length && data[4] == MySQLPacket.COM_QUIT) {
////            source.close("quit packet");
//            return;
//        }
        source.schema = (auth.database);

        // check user
//        if (!checkUser(auth.user, source.getHost())) {
//            failure(ErrorCode.ER_ACCESS_DENIED_ERROR, "Access denied for user '" + auth.user + "' with host '" + source.getHost()+ "'");
//            return;
//        }

        // check password
        if (!checkPassword(auth.password, auth.user)) {
            failure(ErrorCode.ER_ACCESS_DENIED_ERROR, "Access denied for user '" + auth.user + "', because password is error ");
        } else {
            success(auth);

        }

        // check degrade
//        if ( isDegrade( auth.user ) ) {
//        	 failure(ErrorCode.ER_ACCESS_DENIED_ERROR, "Access denied for user '" + auth.user + "', because service be degraded ");
//             return;
//        }

//            DB.currentDB = auth.database;
//        source.setSchema(auth.database);
        // check schema
//        switch (checkSchema(auth.database, auth.user)) {
//        case ErrorCode.ER_BAD_DB_ERROR:
//            failure(ErrorCode.ER_BAD_DB_ERROR, "Unknown database '" + auth.database + "'");
//            break;
//        case ErrorCode.ER_DBACCESS_DENIED_ERROR:
//            String s = "Access denied for user '" + auth.user + "' to database '" + auth.database + "'";
//            failure(ErrorCode.ER_DBACCESS_DENIED_ERROR, s);
//            break;
//        default:
//            success(auth);
//        }
    }

    //TODO: add by zhuam
    //前端 connection 达到该用户设定的阀值后, 立马降级拒绝连接
    protected boolean isDegrade(String user) {

//    	int benchmark = source.getPrivileges().getBenchmark(user);
//    	if ( benchmark > 0 ) {
//
//	    	int forntedsLength = 0;
//	    	NIOProcessor[] processors = MycatServer.getInstance().getProcessors();
//			for (NIOProcessor p : processors) {
//				forntedsLength += p.getForntedsLength();
//			}
//
//			if ( forntedsLength >= benchmark ) {
//				return true;
//			}
//    	}

        return false;
    }

//    protected boolean checkUser(String user, String host) {
//        return
//                source.getPrivileges().userExists(user, host);
//    }

    protected boolean checkPassword(byte[] password, String user) {
//        String pass = source.getPrivileges().getPassword(user);

        // check null
//        if (pass == null || pass.length() == 0) {
//            if (password == null || password.length == 0) {
//                return true;
//            } else {
//                return false;
//            }
//        }
//        if (password == null || password.length == 0) {
//            return false;
//        }

        // encrypt
//        byte[] encryptPass = null;
//        try {
////            encryptPass = SecurityUtil.scramble411(pass.getBytes(), source.getSeed());
//        } catch (NoSuchAlgorithmException e) {
////            LOGGER.warn(source.toString(), e);
//            return false;
//        }
//        if (encryptPass != null && (encryptPass.length == password.length)) {
//            int i = encryptPass.length;
//            while (i-- != 0) {
//                if (encryptPass[i] != password[i]) {
//                    return false;
//                }
//            }
//        } else {
//            return false;
//        }

        return true;
    }

//    protected int checkSchema(String schema, String user) {
//        if (schema == null) {
//            return 0;
//        }
////        FrontendPrivileges privileges = source.getPrivileges();
////        if (!privileges.schemaExists(schema)) {
////            return ErrorCode.ER_BAD_DB_ERROR;
////        }
////        Set<String> schemas = privileges.getUserSchemas(user);
////        if (schemas == null || schemas.size() == 0 || schemas.contains(schema)) {
////            return 0;
////        } else {
////            return ErrorCode.ER_DBACCESS_DENIED_ERROR;
////        }
//        return 0;
//    }

    protected void success(AuthPacket auth) {
        source.authenticated = true;
        source.user = auth.user;
        source.schema = auth.database;
        source.charsetIndex = (auth.charsetIndex);
        source.charset = CharsetUtil.getCharset(source.charsetIndex);

        if (LOGGER.isInfoEnabled()) {
            StringBuilder s = new StringBuilder();
            s.append(source).append('\'').append(auth.user).append("' login success");
            byte[] extra = auth.extra;
            if (extra != null && extra.length > 0) {
                s.append(",extra:").append(new String(extra));
            }
            LOGGER.info(s.toString());
        }

//        ByteBuffer buffer = source.allocate();
//        source.write(source.writeToBuffer(AUTH_OK, buffer));
        source.write(Unpooled.wrappedBuffer(AUTH_OK));
//        boolean clientCompress = Capabilities.CLIENT_COMPRESS==(Capabilities.CLIENT_COMPRESS & auth.clientFlags);
//        boolean usingCompress = false;
//        if(clientCompress&&usingCompress)
//        {
////            source.setSupportCompress(true);
//        }
    }

    protected void failure(int errno, String info) {
        LOGGER.error(source.toString() + info);
        source.writeErrMessage((byte) 2, errno, info);
    }

    @Override
    public void hander(MySQLPacket mySQLPacket) {
        System.out.println(mySQLPacket.toString());
        handle((AuthPacket) mySQLPacket);
    }

    @Override
    public void setConnection(OConnection connection) {
        source = connection;
    }
}