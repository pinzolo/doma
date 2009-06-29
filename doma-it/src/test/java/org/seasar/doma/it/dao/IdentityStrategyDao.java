package org.seasar.doma.it.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.it.ItConfig;
import org.seasar.doma.it.entity.IdentityStrategy;

@Dao(config = ItConfig.class)
public interface IdentityStrategyDao extends GenericDao<IdentityStrategy> {

}