package io.jsql.sql.handler.data_define;

import io.jsql.sql.OConnection;

/**
 * Created by 长宏 on 2017/3/18 0018.
 * CREATE EVENT myevent
 * ON SCHEDULE AT CURRENT_TIMESTAMP + INTERVAL 1 HOUR
 * DO
 * UPDATE myschema.mytable SET mycol = mycol + 1;
 */
public class CreateEvent {
    public static boolean isme(String sql) {
        String sqll = sql.toUpperCase().trim();
        String list[] = sqll.split("\\s+");
        return list.length > 2 && list[0].equals("CREATE") && list[1].equals("EVENT");
    }

    public static void handle(String sql, OConnection c) {
        c.writeok();
    }
}
