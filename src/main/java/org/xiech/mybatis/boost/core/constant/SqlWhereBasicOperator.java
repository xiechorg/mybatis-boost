package org.xiech.mybatis.boost.core.constant;

public enum SqlWhereBasicOperator {
    EQ(SqlConstant.EQ),
    GT(SqlConstant.GT),
    LT(SqlConstant.LT),
    NE(SqlConstant.NE),
    GE(SqlConstant.GE),
    LE(SqlConstant.LE);
    private String operator;

    SqlWhereBasicOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return this.operator;
    }
}