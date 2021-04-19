package org.xiech.mybatis.boost.core;


/**
 * SQL语句拼接的工具类，适用于mybatis的provider对象
 *
 * @author xiech
 * @date 2020-07-14 17:21
 */
public class LambdaUpdateCriteria<T> extends AbstractLambdaUpdateCriteria<T, LambdaUpdateCriteria<T>> {

    public <T> LambdaUpdateCriteria(Class<T> beanClass) {
        super(beanClass);
    }

    @Override
    public LambdaUpdateCriteria getSelf() {
        return this;
    }
}