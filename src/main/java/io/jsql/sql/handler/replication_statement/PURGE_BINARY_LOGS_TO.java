package io.jsql.sql.handler.replication_statement;

import io.jsql.config.ErrorCode;
import io.jsql.sql.OConnection;

/**
 * Created by zhang on 2017/3/27.
 * PURGE  BINARY  LOGS TO 'log_name';
 * eg:
 * PURGE BINARY LOGS TO 'mysql-bin.010';
 */
public class PURGE_BINARY_LOGS_TO {
    public static boolean isMe(String sql) {
        String[] strings = sql.split("\\s+");
        return strings.length > 4 && strings[0].equalsIgnoreCase("purge") && strings[1].equalsIgnoreCase("binary") && strings[2].equalsIgnoreCase("logs") && strings[3].equalsIgnoreCase("to");
    }

    public static void handle(String sql, OConnection c) {
        c.writeErrMessage(ErrorCode.ER_NOT_SUPPORTED_YET, "暂未支持");
    }
}
