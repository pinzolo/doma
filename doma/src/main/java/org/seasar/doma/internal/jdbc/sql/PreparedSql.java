/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.internal.jdbc.sql;

import java.util.Collections;
import java.util.List;

import org.seasar.doma.DomaIllegalArgumentException;
import org.seasar.doma.jdbc.Sql;

/**
 * 
 * @author taedium
 * 
 */
public class PreparedSql implements Sql<PreparedSqlParameter> {

    protected final String rawSql;

    protected final String formattedSql;

    protected final List<PreparedSqlParameter> parameters;

    public PreparedSql(CharSequence rawSql) {
        this(rawSql, rawSql, Collections.<PreparedSqlParameter> emptyList());
    }

    public PreparedSql(CharSequence rawSql, CharSequence formattedSql,
            List<? extends PreparedSqlParameter> parameters) {
        if (rawSql == null) {
            throw new DomaIllegalArgumentException("rawSql", rawSql);
        }
        if (formattedSql == null) {
            throw new DomaIllegalArgumentException("formattedSql", formattedSql);
        }
        if (parameters == null) {
            throw new DomaIllegalArgumentException("parameters", parameters);
        }
        this.rawSql = rawSql.toString().trim();
        this.formattedSql = formattedSql.toString().trim();
        this.parameters = Collections.unmodifiableList(parameters);
    }

    public String getRawSql() {
        return rawSql;
    }

    public String getFormattedSql() {
        return formattedSql;
    }

    public List<PreparedSqlParameter> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return rawSql;
    }

}
