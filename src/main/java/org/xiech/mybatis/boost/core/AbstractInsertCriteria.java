package org.xiech.mybatis.boost.core;



import org.xiech.mybatis.boost.core.constant.SqlConstant;

import java.util.Map;

/**
 * 对insert相关的拼接sql处理
 *
 * @author xiech
 * @date 2020-07-16 22:04
 */
public abstract class AbstractInsertCriteria<T, R extends AbstractInsertCriteria<T, R>>
        extends AbstractWhereCriteria<T, R> {

    @SuppressWarnings("unchecked")
    public <T> AbstractInsertCriteria(Class<T> beanClass) {
        super(beanClass);
        init();
    }

    private void init() {
        getSql().INSERT_INTO(getTableDesc().getDelimiterTableName());
    }

    public R set(String column, Object value) {
        column = resolveColumn(column);
        getSql().SET(resolveSql(column + SqlConstant.EQ + SqlConstant.PARAM0, value));
        return getSelf();
    }

    public R sets(Map<String, Object> params) {
        if (params != null) {
            params.keySet().forEach(column -> {
                getSql().SET(resolveSql(resolveColumn(column) + SqlConstant.EQ + SqlConstant.PARAM0, params.get(column)));
            });
        }
        return getSelf();
    }
}
