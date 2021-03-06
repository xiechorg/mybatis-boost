package org.xiech.mybatis.boost.core;



import org.xiech.mybatis.boost.core.constant.SqlConstant;
import org.xiech.mybatis.boost.util.lambda.explain.SerializeFunction;

import java.util.Map;

/**
 * 对update相关的拼接sql处理
 *
 * @author xiech
 * @date 2020-07-16 22:04
 */
public abstract class AbstractLambdaUpdateCriteria<T, R extends AbstractLambdaUpdateCriteria<T, R>>
        extends AbstractWhereCriteria<T, R> {

    @SuppressWarnings("unchecked")
    public <T> AbstractLambdaUpdateCriteria(Class<T> beanClass) {
        super(beanClass);
        init();
    }

    private void init() {
        getSql().UPDATE(getTableDesc().getDelimiterTableName());
    }

    public <E> R set(SerializeFunction<T, E> column, Object value) {
        String c = resolveColumn(column, false);
        getSql().SET(resolveSql(column + SqlConstant.EQ + SqlConstant.PARAM0, value));
        return getSelf();
    }

    public <E> R sets(Map<SerializeFunction<T, E>, Object> params) {
        if (params != null) {
            params.keySet().forEach(column -> {
                getSql().SET(resolveSql(resolveColumn(column, false) + SqlConstant.EQ + SqlConstant.PARAM0, params.get(column)));
            });
        }
        return getSelf();
    }
}
