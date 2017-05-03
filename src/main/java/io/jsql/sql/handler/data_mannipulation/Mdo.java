package io.jsql.sql.handler.data_mannipulation;

import io.jsql.sql.OConnection;

/**
 * Created by 长宏 on 2017/3/18 0018.
 * mysql> SELECT SLEEP(5);
 * +----------+
 * | SLEEP(5) |
 * +----------+
 * |        0 |
 * +----------+
 * 1 row in set (5.02 sec)
 * DO, on the other hand, pauses without producing a result set.:
 * <p>
 * mysql> DO SLEEP(5);
 * Query OK, 0 rows affected (4.99 sec)
 */
//@Component 不支持的解析语句，，，所有不用实现hander接口
public class Mdo  {
    public static boolean isme(String sql) {
        String sqll = sql.toUpperCase().trim();
        String list[] = sqll.split("\\s+");
        return list.length > 1 && list[0].equals("DO");
    }

    public static void handle(String sql, OConnection c) {
        c.writeok();
    }

}
