package org.seasar.doma.internal.jdbc.query;

import java.util.List;

import org.seasar.doma.domain.BigDecimalDomain;
import org.seasar.doma.domain.IntegerDomain;
import org.seasar.doma.domain.StringDomain;
import org.seasar.doma.internal.jdbc.mock.MockConfig;
import org.seasar.doma.internal.jdbc.query.AutoUpdateQuery;
import org.seasar.doma.internal.jdbc.query.UpdateQuery;
import org.seasar.doma.internal.jdbc.sql.PreparedSql;
import org.seasar.doma.internal.jdbc.sql.PreparedSqlParameter;
import org.seasar.doma.jdbc.JdbcException;
import org.seasar.doma.message.MessageCode;

import junit.framework.TestCase;
import example.entity.Emp;
import example.entity.Emp_;

/**
 * @author taedium
 * 
 */
public class AutoUpdateQueryTest extends TestCase {

    private MockConfig runtimeConfig = new MockConfig();

    public void testCompile() throws Exception {
        Emp emp = new Emp_();
        emp.id().set(10);
        emp.name().set("aaa");
        emp.version().set(100);

        AutoUpdateQuery<Emp, Emp_> query = new AutoUpdateQuery<Emp, Emp_>(
                Emp_.class);
        query.setConfig(runtimeConfig);
        query.setEntity(emp);
        query.setCallerClassName("aaa");
        query.setCallerMethodName("bbb");
        query.compile();

        UpdateQuery updateQuery = query;
        assertNotNull(updateQuery.getSql());
    }

    public void testOption_default() throws Exception {
        Emp emp = new Emp_();
        emp.id().set(10);
        emp.name().set("aaa");
        emp.version().set(100);

        AutoUpdateQuery<Emp, Emp_> query = new AutoUpdateQuery<Emp, Emp_>(
                Emp_.class);
        query.setConfig(runtimeConfig);
        query.setEntity(emp);
        query.setCallerClassName("aaa");
        query.setCallerMethodName("bbb");
        query.compile();

        PreparedSql sql = query.getSql();
        assertEquals("update emp set name = ?, version = ? + 1 where id = ? and version = ?", sql
                .getRawSql());

        List<PreparedSqlParameter> parameters = sql.getParameters();
        assertEquals(4, parameters.size());
        assertEquals(new StringDomain("aaa"), parameters.get(0).getDomain());
        assertEquals(new IntegerDomain(100), parameters.get(1).getDomain());
        assertEquals(new IntegerDomain(10), parameters.get(2).getDomain());
        assertEquals(new IntegerDomain(100), parameters.get(3).getDomain());
    }

    public void testOption_excludesNull() throws Exception {
        Emp emp = new Emp_();
        emp.id().set(10);
        emp.version().set(100);

        AutoUpdateQuery<Emp, Emp_> query = new AutoUpdateQuery<Emp, Emp_>(
                Emp_.class);
        query.setConfig(runtimeConfig);
        query.setEntity(emp);
        query.setNullExcluded(true);
        query.setCallerClassName("aaa");
        query.setCallerMethodName("bbb");
        query.compile();

        PreparedSql sql = query.getSql();
        assertEquals("update emp set version = ? + 1 where id = ? and version = ?", sql
                .getRawSql());
        List<PreparedSqlParameter> parameters = sql.getParameters();
        assertEquals(3, parameters.size());
        assertEquals(new IntegerDomain(100), parameters.get(0).getDomain());
        assertEquals(new IntegerDomain(10), parameters.get(1).getDomain());
        assertEquals(new IntegerDomain(100), parameters.get(2).getDomain());
    }

    public void testOption_includesVersion() throws Exception {
        Emp emp = new Emp_();
        emp.id().set(10);
        emp.name().set("aaa");
        emp.version().set(100);

        AutoUpdateQuery<Emp, Emp_> query = new AutoUpdateQuery<Emp, Emp_>(
                Emp_.class);
        query.setConfig(runtimeConfig);
        query.setEntity(emp);
        query.setVersionIncluded(true);
        query.setCallerClassName("aaa");
        query.setCallerMethodName("bbb");
        query.compile();

        PreparedSql sql = query.getSql();
        assertEquals("update emp set name = ?, version = ? where id = ?", sql
                .getRawSql());
        List<PreparedSqlParameter> parameters = sql.getParameters();
        assertEquals(3, parameters.size());
        assertEquals(new StringDomain("aaa"), parameters.get(0).getDomain());
        assertEquals(new IntegerDomain(100), parameters.get(1).getDomain());
        assertEquals(new IntegerDomain(10), parameters.get(2).getDomain());
    }

    public void testIsExecutable() throws Exception {
        Emp emp = new Emp_();

        AutoUpdateQuery<Emp, Emp_> query = new AutoUpdateQuery<Emp, Emp_>(
                Emp_.class);
        query.setConfig(runtimeConfig);
        query.setEntity(emp);
        query.setCallerClassName("aaa");
        query.setCallerMethodName("bbb");
        query.compile();

        assertFalse(query.isExecutable());
    }

    public void testIllegalEntityInstance() throws Exception {
        AutoUpdateQuery<Emp, Emp_> query = new AutoUpdateQuery<Emp, Emp_>(
                Emp_.class);
        try {
            query.setEntity(new MyEmp());
            fail();
        } catch (JdbcException expected) {
            assertEquals(MessageCode.DOMA2026, expected.getMessageCode());
        }
    }

    private static class MyEmp implements Emp {

        @Override
        public IntegerDomain version() {
            return null;
        }

        @Override
        public BigDecimalDomain salary() {
            return null;
        }

        @Override
        public StringDomain name() {
            return null;
        }

        @Override
        public IntegerDomain id() {
            return null;
        }
    }
}