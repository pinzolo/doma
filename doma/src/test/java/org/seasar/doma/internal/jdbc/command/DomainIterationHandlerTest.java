package org.seasar.doma.internal.jdbc.command;

import org.seasar.doma.domain.StringDomain;
import org.seasar.doma.internal.jdbc.command.DomainIterationHandler;
import org.seasar.doma.internal.jdbc.mock.ColumnMetaData;
import org.seasar.doma.internal.jdbc.mock.MockConfig;
import org.seasar.doma.internal.jdbc.mock.MockResultSet;
import org.seasar.doma.internal.jdbc.mock.MockResultSetMetaData;
import org.seasar.doma.internal.jdbc.mock.RowData;
import org.seasar.doma.internal.jdbc.query.SqlFileSelectQuery;
import org.seasar.doma.internal.jdbc.sql.SqlFiles;
import org.seasar.doma.jdbc.IterationCallback;
import org.seasar.doma.jdbc.IterationContext;

import junit.framework.TestCase;

/**
 * @author taedium
 * 
 */
public class DomainIterationHandlerTest extends TestCase {

    private MockConfig runtimeConfig = new MockConfig();

    public void testHandle() throws Exception {
        MockResultSetMetaData metaData = new MockResultSetMetaData();
        metaData.columns.add(new ColumnMetaData("name"));
        MockResultSet resultSet = new MockResultSet(metaData);
        resultSet.rows.add(new RowData("aaa"));
        resultSet.rows.add(new RowData("bbb"));

        SqlFileSelectQuery query = new SqlFileSelectQuery();
        query.setConfig(runtimeConfig);
        query.setSqlFilePath(SqlFiles
                .buildPath(getClass().getName(), getName()));
        query.setCallerClassName("aaa");
        query.setCallerMethodName("bbb");
        query.compile();

        DomainIterationHandler<String, StringDomain> handler = new DomainIterationHandler<String, StringDomain>(
                StringDomain.class,
                new IterationCallback<String, StringDomain>() {

                    private String result = "";

                    @Override
                    public String iterate(StringDomain target,
                            IterationContext iterationContext) {
                        result += target.get();
                        return result;
                    }
                });
        String result = handler.handle(resultSet, query);
        assertEquals("aaabbb", result);
    }

    public void testHandle_exits() throws Exception {
        MockResultSetMetaData metaData = new MockResultSetMetaData();
        metaData.columns.add(new ColumnMetaData("name"));
        MockResultSet resultSet = new MockResultSet(metaData);
        resultSet.rows.add(new RowData("aaa"));
        resultSet.rows.add(new RowData("bbb"));

        SqlFileSelectQuery query = new SqlFileSelectQuery();
        query.setConfig(runtimeConfig);
        query.setSqlFilePath(SqlFiles
                .buildPath(getClass().getName(), getName()));
        query.setCallerClassName("aaa");
        query.setCallerMethodName("bbb");
        query.compile();

        DomainIterationHandler<String, StringDomain> handler = new DomainIterationHandler<String, StringDomain>(
                StringDomain.class,
                new IterationCallback<String, StringDomain>() {

                    private String result = "";

                    @Override
                    public String iterate(StringDomain target,
                            IterationContext iterationContext) {
                        result += target.get();
                        iterationContext.exits();
                        return result;
                    }
                });
        String result = handler.handle(resultSet, query);
        assertEquals("aaa", result);
    }
}