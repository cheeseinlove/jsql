package io.jsql.sql.handler.data_define;

import io.jsql.sql.OConnection;

/**
 * Created by 长宏 on 2017/3/18 0018.
 */
public class CreateLogfilegroup {
    public static boolean isme(String sql) {
        String sqll = sql.toUpperCase().trim();
        String list[] = sqll.split("\\s+");
        return list.length > 2 && list[0].equals("CREATE") && list[1].equals("LOGFILE");
    }

    public static void handle(String sql, OConnection c) {
        c.writeok();
    }
}
