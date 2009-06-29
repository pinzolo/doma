package org.seasar.doma.internal.jdbc.query;

import java.util.List;

import org.seasar.doma.internal.jdbc.sql.PreparedSql;


/**
 * @author taedium
 * 
 */
public interface BatchModifyQuery extends Query {

    List<PreparedSql> getSqls();

    PreparedSql getSql();

    boolean isOptimisticLockCheckRequired();

    boolean isAutoGeneratedKeysSupported();

    boolean isExecutable();

    int getBatchSize();
}