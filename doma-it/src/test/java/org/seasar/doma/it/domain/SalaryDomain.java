package org.seasar.doma.it.domain;

import java.math.BigDecimal;

import org.seasar.doma.domain.AbstractBigDecimalDomain;

public class SalaryDomain extends AbstractBigDecimalDomain<SalaryDomain> {

    public SalaryDomain() {
        super();
    }

    public SalaryDomain(BigDecimal value) {
        super(value);
    }

}