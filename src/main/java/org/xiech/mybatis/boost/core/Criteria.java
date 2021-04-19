package org.xiech.mybatis.boost.core;


/**
 * SQL语句拼接的工具类，适用于mybatis的provider对象
 *
 * @author xiech
 * @date 2020-07-14 17:21
 */
public class Criteria<T> extends AbstractSelectCriteria<T, Criteria<T>> {

    public <T> Criteria(Class<T> beanClass) {
        super(beanClass);
    }

    @Override
    public Criteria getSelf() {
        return this;
    }
}