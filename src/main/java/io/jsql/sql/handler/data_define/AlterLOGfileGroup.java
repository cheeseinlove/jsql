package io.jsql.sql.handler.data_define;

import io.jsql.sql.OConnection;

/**
 * Created by 长宏 on 2017/3/18 0018.
 * ALTER LOGFILE GROUP lg_3
 * ADD UNDOFILE 'undo_10.dat'
 * INITIAL_SIZE=32M
 * ENGINE=NDBCLUSTER;
 */
public class AlterLOGfileGroup {
    public static boolean isme(String sql) {
        String sqll = sql.toUpperCase().trim();
        String list[] = sqll.split("\\s+");
        return list.length > 2 && list[0].equals("ALTER") && list[1].equals("LOGFILE") && list[2].equals("GROUP");
    }

    public static void handle(String sql, OConnection c) {
        c.writeok();
    }
}
