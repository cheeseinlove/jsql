package io.jsql.sql.handler.adminstatement;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLShowTablesStatement;
import io.jsql.sql.handler.SqlStatementHander;
import io.jsql.sql.response.MShowTables;
import org.springframework.stereotype.Component;

/**
 * Created by 长宏 on 2017/5/3 0003.
 */
@Component
public class MSQLShowTablesStatement extends SqlStatementHander{
    @Override
    public Class<? extends SQLStatement> supportSQLstatement() {
        return SQLShowTablesStatement.class;
    }

    @Override
    protected Object handle(SQLStatement sqlStatement) throws Exception {
        return MShowTables.getdata((SQLShowTablesStatement) sqlStatement, c);
    }
}
