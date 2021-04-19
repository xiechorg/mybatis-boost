package org.xiech.mybatis.boost.core;


/**
 * SQL语句拼接的工具类，适用于mybatis的provider对象
 *
 * @author xiech
 * @date 2020-07-14 17:21
 */
public class LambdaInsertCriteria<T> extends AbstractLambdaInsertCriteria<T, LambdaInsertCriteria<T>> {

    public <T> LambdaInsertCriteria(Class<T> beanClass) {
        super(beanClass);
    }

    @Override
    public LambdaInsertCriteria getSelf() {
        return this;
    }
}