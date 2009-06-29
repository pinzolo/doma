package org.seasar.doma.internal.jdbc.query;

import java.util.List;

import org.seasar.doma.domain.IntegerDomain;
import org.seasar.doma.internal.jdbc.mock.MockConfig;
import org.seasar.doma.internal.jdbc.query.AutoDeleteQuery;
import org.seasar.doma.internal.jdbc.query.DeleteQuery;
import org.seasar.doma.internal.jdbc.sql.PreparedSql;
import org.seasar.doma.internal.jdbc.sql.PreparedSqlParameter;

import junit.framework.TestCase;
import example.entity.Emp;
import example.entity.Emp_;

/**
 * @author taedium
 * 
 */
public class AutoDeleteQueryTest extends TestCase {

    private MockConfig runtimeConfig = new MockConfig();

    public void testCompile() throws Exception {
        Emp emp = new Emp_();

        AutoDeleteQuery<Emp, Emp_> query = new AutoDeleteQuery<Emp, Emp_>(
                Emp_.class);
        query.setConfig(runtimeConfig);
        query.setEntity(emp);
        query.setCallerClassName("aaa");
        query.setCallerMethodName("bbb");
        query.compile();

        DeleteQuery deleteQuery = query;
        assertNotNull(deleteQuery.getSql());
    }

    public void testOption_default() throws Exception {
        Emp emp = new Emp_();
        emp.id().set(10);
        emp.name().set("aaa");
        emp.version().set(100);

        AutoDeleteQuery<Emp, Emp_> query = new AutoDeleteQuery<Emp, Emp_>(
                Emp_.class);
        query.setConfig(runtimeConfig);
        query.setEntity(emp);
        query.setCallerClassName("aaa");
        query.setCallerMethodName("bbb");
        query.compile();

        PreparedSql sql = query.getSql();
        assertEquals("delete from emp where id = ? and version = ?", sql
                .getRawSql());
        List<PreparedSqlParameter> parameters = sql.getParameters();
        assertEquals(2, parameters.size());
        assertEquals(new IntegerDomain(10), parameters.get(0).getDomain());
        assertEquals(new IntegerDomain(100), parameters.get(1).getDomain());
    }

    public void testOption_ignoresVersion() throws Exception {
        Emp emp = new Emp_();
        emp.id().set(10);
        emp.name().set("aaa");
        emp.version().set(100);

        AutoDeleteQuery<Emp, Emp_> query = new AutoDeleteQuery<Emp, Emp_>(
                Emp_.class);
        query.setConfig(runtimeConfig);
        query.setEntity(emp);
        query.setVersionIgnored(true);
        query.setCallerClassName("aaa");
        query.setCallerMethodName("bbb");
        query.compile();

        PreparedSql sql = query.getSql();
        assertEquals("delete from emp where id = ?", sql.getRawSql());
        List<PreparedSqlParameter> parameters = sql.getParameters();
        assertEquals(1, parameters.size());
        assertEquals(new IntegerDomain(10), parameters.get(0).getDomain());
    }
}