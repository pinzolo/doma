package org.seasar.doma.internal.apt.entity;

import org.seasar.doma.Entity;

/**
 * @author taedium
 * 
 */
public interface NotTopLevelEntity {

    @Entity
    interface Hoge {
    }
}