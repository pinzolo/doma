package org.seasar.doma.internal.jdbc.query;

import java.sql.Statement;

/**
 * @author taedium
 * 
 */
public interface InsertQuery extends ModifyQuery {

    void generateId(Statement statement);
}