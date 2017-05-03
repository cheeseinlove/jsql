package io.jsql.mysql.handler;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import io.jsql.config.ErrorCode;
import io.jsql.sql.OConnection;
import io.jsql.sql.handler.AllHanders;
import io.jsql.sql.handler.SqlStatementHander;
import io.jsql.sql.handler.adminstatement.ShowHandler;
import io.jsql.sql.handler.data_define.*;
import io.jsql.sql.handler.data_mannipulation.Mdo;
import io.jsql.sql.handler.data_mannipulation.Mhandler;
import io.jsql.sql.handler.data_mannipulation.Msubquery;
import io.jsql.sql.handler.utilstatement.ExplainStatement;
import io.jsql.sql.parser.MSQLvisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by 长宏 on 2017/4/30 0030.
 */
public class MysqlSQLhander implements SQLHander {
    private static final Logger logger = LoggerFactory
            .getLogger(MysqlSQLhander.class);

    private final OConnection source;
//    private final MySqlASTVisitor mySqlASTVisitor;
    protected Boolean readOnly;
    private Exception exception;
    private AllHanders allHanders;
    public MysqlSQLhander(OConnection source, AllHanders allHanders) {
        this.allHanders = allHanders;
        this.source = source;
//        mySqlASTVisitor = new MSQLvisitor(source);
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public void handle(String sql) {
        logger.info(sql);
        OConnection c = this.source;
        if (logger.isDebugEnabled()) {
            logger.debug(sql);
        }
        SQLStatement sqlStatement;
        try {
            MySqlStatementParser parser = new MySqlStatementParser(sql);
            sqlStatement = parser.parseStatement();
//            if (sqlStatement.toString().startsWith("show")) {
//                return;
////            lists.forEach(statement -> statement.accept(mySqlASTVisitor));
//            }
            SqlStatementHander hander = allHanders.handerMap.get(sqlStatement.getClass());
            if (hander != null) {
                hander.handle(sqlStatement, c);
                return;
            }
        } catch (Exception e) {//如果不是合法的mysql语句，就报错
//            e.printStackTrace();
            exception = e;
//            return;
        }
        //druid支持的语句就用上面的方法语句处理，如果不支持，就会有异常，就自己写代码解析sql语句，处理。
        //下面是drop event语句的例子，这个例子druid不支持，所以自己写
        handleotherStatement(sql, c);
    }

    private void handleotherStatement(String sql, OConnection c) {
        if (AlterEvent.isme(sql)) {
            AlterEvent.handle(sql, c);
            return;
        }

        if (AlterFunction.isme(sql)) {
            AlterFunction.handle(sql, c);
            return;
        }
        if (AlterInstall.isme(sql)) {
            AlterInstall.handle(sql, c);
            return;
        }
        if (AlterLOGfileGroup.isme(sql)) {
            AlterLOGfileGroup.handle(sql, c);
            return;
        }
        if (AlterProcedure.isme(sql)) {
            AlterProcedure.handle(sql, c);
            return;
        }
        if (AlterServer.isme(sql)) {
            AlterServer.handle(sql, c);
            return;
        }
        if (AlterTableSpace.isme(sql)) {
            AlterTableSpace.handle(sql, c);
            return;
        }
        if (CreateEvent.isme(sql)) {
            CreateEvent.handle(sql, c);
            return;
        }
        if (CreateServer.isme(sql)) {
            CreateServer.handle(sql, c);
            return;
        }
        if (CreateFunction.isme(sql)) {
            CreateFunction.handle(sql, c);
            return;
        }
        if (CreateLogfilegroup.isme(sql)) {
            CreateLogfilegroup.handle(sql, c);
            return;
        }
        if (CreateServer.isme(sql)) {
            CreateServer.handle(sql, c);
            return;
        }
        if (CreateTableSpace.isme(sql)) {
            CreateTableSpace.handle(sql, c);
            return;
        }
        if (DropEVENT.isdropevent(sql)) {//判断是不是dropevent语句
            DropEVENT.handle(sql, c);
            return;
        }
        if (DropFunction.isme(sql)) {
            DropFunction.handle(sql, c);
            return;
        }
        if (ExplainStatement.isme(sql, c)) {
            ExplainStatement.handle(sql, c);
            return;
        }
        if (DropLOGFILEGROUP.isme(sql)) {
            DropLOGFILEGROUP.handle(sql, c);
            return;
        }
        if (DropServer.isme(sql)) {
            DropServer.handle(sql, c);
            return;
        }
        if (Mdo.isme(sql)) {
            Mdo.handle(sql, c);
            return;
        }
        if (Mhandler.isme(sql)) {
            Mhandler.handle(sql, c);
            return;
        }
        if (Msubquery.isme(sql)) {
            Msubquery.handle(sql, c);
            return;
        }

        c.writeErrMessage(ErrorCode.ER_SP_BAD_SQLSTATE, exception == null ? "不支持的语句！！！" : exception.getMessage());
    }

}
