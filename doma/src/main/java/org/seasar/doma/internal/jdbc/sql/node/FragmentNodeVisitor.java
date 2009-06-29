package org.seasar.doma.internal.jdbc.sql.node;

import org.seasar.doma.jdbc.SqlNodeVisitor;

/**
 * @author taedium
 * 
 */
public interface FragmentNodeVisitor<R, P> extends SqlNodeVisitor<R, P> {

    R visitFragmentNode(FragmentNode node, P p);
}