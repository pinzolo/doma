package org.seasar.doma.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author taedium
 * 
 */
public interface Dialect {

    String getName();

    SqlNode transformSelectSqlNode(SqlNode original, SelectOptions options);

    boolean isUniqueConstraintViolated(SQLException sqlException);

    boolean includesIdentityColumn();

    boolean supportsIdentity();

    boolean supportsSequence();

    boolean supportsAutoGeneratedKeys();

    boolean supportsBatchUpdateResults();

    boolean supportsSelectForUpdate(SelectForUpdateType type,
            boolean withTargets);

    boolean supportsResultSetReturningAsOutParameter();

    Sql<?> getIdentitySelectSql(String qualifiedTableName, String columnName);

    Sql<?> getSequenceNextValSql(String qualifiedSequenceName,
            long allocationSize);

    JdbcType<ResultSet> getResultSetType();

    String applyQuote(String name);

    String removeQuote(String name);

}