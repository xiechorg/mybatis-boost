package org.xiech.mybatis.boost.core;



import org.xiech.mybatis.boost.core.constant.SqlConstant;
import org.xiech.mybatis.boost.core.constant.SqlLikeType;
import org.xiech.mybatis.boost.core.constant.SqlWhereBasicOperator;
import org.xiech.mybatis.boost.util.lambda.explain.SerializeFunction;
import org.xiech.mybatis.boost.util.string.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * 对where条件拼接sql的处理
 *
 * @author xiech
 * @date 2020-07-16 22:04
 */
public abstract class AbstractLambdaWhereCriteria<T, R extends AbstractLambdaWhereCriteria<T, R>> extends AbstractCriteria<T, R> {

    private List<String> wheres = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public <T> AbstractLambdaWhereCriteria(Class<T> beanClass) {
        super(beanClass);
    }

    /**
     * 添加 = 的where条件
     *
     * @param column 实体类的属性名、数据库表的字段名
     * @param value
     * @return
     */
    public <E> R eq(SerializeFunction<T, E> column, Object value) {
        whereBasic(SqlWhereBasicOperator.EQ, column, value);
        return getSelf();
    }

    /**
     * 添加 > 的where条件
     *
     * @param column 实体类的属性名、数据库表的字段名
     * @param value
     * @return
     */
    public <E> R gt(SerializeFunction<T, E> column, Object value) {
        whereBasic(SqlWhereBasicOperator.GT, column, value);
        return getSelf();
    }

    /**
     * 添加 < 的where条件
     *
     * @param column 实体类的属性名、数据库表的字段名
     * @param value
     * @return
     */
    public <E> R lt(SerializeFunction<T, E> column, Object value) {
        whereBasic(SqlWhereBasicOperator.LT, column, value);
        return getSelf();
    }

    /**
     * 添加 != 的where条件
     *
     * @param column 实体类的属性名、数据库表的字段名
     * @param value
     * @return
     */
    public <E> R ne(SerializeFunction<T, E> column, Object value) {
        whereBasic(SqlWhereBasicOperator.NE, column, value);
        return getSelf();
    }

    /**
     * 添加 >= 的where条件
     *
     * @param column 实体类的属性名、数据库表的字段名
     * @param value
     * @return
     */
    public <E> R ge(SerializeFunction<T, E> column, Object value) {
        whereBasic(SqlWhereBasicOperator.GE, column, value);
        return getSelf();
    }

    /**
     * 添加 <= 的where条件
     *
     * @param column 实体类的属性名、数据库表的字段名
     * @param value
     * @return
     */
    public <E> R le(SerializeFunction<T, E> column, Object value) {
        whereBasic(SqlWhereBasicOperator.LE, column, value);
        return getSelf();
    }


    /**
     * 添加between的where条件
     *
     * @param column     实体类的属性名、数据库表的字段名
     * @param valueStart 开始值，不传则不生成between其中的大于等于条件
     * @param valueEnd   结束值，不传则不生成between其中的小于等于条件
     * @return
     */
    public <E> R between(SerializeFunction<T, E> column, Object valueStart, Object valueEnd) {
        boolean b1 = checkParamValue(valueStart);
        boolean b2 = checkParamValue(valueEnd);
        if (!b1 && !b2)
            return getSelf();
        String c = resolveColumn(column, false);
        if (b1) {
            String newCondition = c
                    + SqlConstant.GE
                    + SqlConstant.PARAM0;
            where(newCondition, valueStart);
        }
        if (b2) {
            String newCondition = c
                    + SqlConstant.LE
                    + SqlConstant.PARAM0;
            where(newCondition, valueEnd);
        }
        return getSelf();
    }

    /**
     * 添加not between的where条件
     *
     * @param column     实体类的属性名、数据库表的字段名
     * @param valueStart 开始值，必传，如果不传则不生成该条件
     * @param valueEnd   结束值，必传，如果不传则不生成该条件
     * @return
     */
    public <E> R notBetween(SerializeFunction<T, E> column, Object valueStart, Object valueEnd) {
        boolean b1 = checkParamValue(valueStart);
        boolean b2 = checkParamValue(valueEnd);
        if (!(b1 && b2))
            return getSelf();
        String c = resolveColumn(column, false);
        String newCondition = c
                + SqlConstant.LE
                + SqlConstant.PARAM0
                + SqlConstant.AND
                + column
                + SqlConstant.GE
                + SqlConstant.PARAM1;
        return getSelf();
    }

    public R and() {
        getSql().AND();
        return getSelf();
    }

    public R and(Consumer<R> consumer) {
        and();
        consumer.accept(getSelf());
        return and();
    }

    public R or() {
        getSql().OR();
        return getSelf();
    }

    public R or(Consumer<R> consumer) {
        or();
        consumer.accept(getSelf());
        return and();
    }

    public R nested(Consumer<R> consumer) {
        consumer.accept(getSelf());
        return and();
    }

    /**
     * 添加like的where条件：'%{关键词}%'
     *
     * @param keywords 关键词
     * @param columns  实体类的属性名、数据库表的字段名
     * @return
     */
    public <E> R like(String keywords, SerializeFunction<T, E>... columns) {
        whereLike(SqlLikeType.DEFAULT, keywords, columns);
        return getSelf();
    }

    /**
     * 添加like的where条件：'%{关键词}'
     *
     * @param keywords 关键词
     * @param columns  实体类的属性名、数据库表的字段名
     * @return
     */
    public <E> R likeLeft(String keywords, SerializeFunction<T, E>... columns) {
        whereLike(SqlLikeType.LEFT, keywords, columns);
        return getSelf();
    }

    /**
     * 添加like的where条件：'{关键词}%'
     *
     * @param keywords 关键词
     * @param columns  实体类的属性名、数据库表的字段名
     * @return
     */
    public <E> R likeRight(String keywords, SerializeFunction<T, E>... columns) {
        whereLike(SqlLikeType.RIGHT, keywords, columns);
        return getSelf();
    }

    /**
     * 添加in的where条件
     *
     * @param column      实体类的属性名、数据库表的字段名
     * @param collections 要in查询的值列表
     * @return
     */
    public <E> R in(SerializeFunction<T, E> column, Collection<?> collections) {
        if (!checkParamValue(collections)) {
            return getSelf();
        }
        String c = resolveColumn(column, false);
        String newCondition = c
                + SqlConstant.IN
                + SqlConstant.PARAM0;
        where(newCondition, collections);
        return getSelf();
    }

    /**
     * 添加not in的where条件
     *
     * @param column      实体类的属性名、数据库表的字段名
     * @param collections 要in查询的值列表
     * @return
     */
    public <E> R notIn(SerializeFunction<T, E> column, Collection<?> collections) {
        if (!checkParamValue(collections)) {
            return getSelf();
        }
        String c = resolveColumn(column, false);
        String newCondition = c
                + SqlConstant.NOT_IN
                + SqlConstant.PARAM0;
        where(newCondition, collections);
        return getSelf();
    }

    /**
     * 添加exists的where条件，注意会自动添加exists关键字
     *
     * @param sql    where条件的exists的sql片段，若需要入参则使用'{此方法params参数的索引，从0开始}'格式；例如：
     *               (select id from user_has_role where role_id = {0})
     * @param params sql里面需要传入的sql参数
     * @return
     */
    public R exists(String sql, Object... params) {
        if (StringUtils.isBlank(sql)) {
            return getSelf();
        }
        where(SqlConstant.EXISTS + sql, params);
        return getSelf();
    }

    /**
     * 添加not exists的where条件，注意会自动添加exists关键字
     *
     * @param sql    where条件的exists的sql片段，若需要入参则使用'{此方法params参数的索引，从0开始}'格式；例如：
     *               (select id from user_has_role where role_id = {0})
     * @param params sql里面需要传入的sql参数
     * @return
     */
    public R notExists(String sql, Object... params) {
        if (StringUtils.isBlank(sql)) {
            return getSelf();
        }
        where(SqlConstant.NOT_EXISTS + sql, params);
        return getSelf();
    }

    /**
     * 添加eq、gt、lt、ne、ge、le的where条件
     *
     * @param operator WhereOperator枚举
     * @param column   实体类的属性名、数据库表的字段名
     * @param value
     * @return
     */
    private <E> void whereBasic(SqlWhereBasicOperator operator, SerializeFunction<T, E> column, Object value) {
        if (operator == null || !checkParamValue(value)) {
            return;
        }
        String c = resolveColumn(column, false);
        String newCondition = c
                + operator.getOperator()
                + SqlConstant.PARAM0;
        where(newCondition, value);
    }


    /**
     * 添加like的where条件
     *
     * @param sqlLikeType like查询类型：'%{关键词}%'、'%{关键词}'、'{关键词}%'
     * @param keywords    关键词
     * @param columns     实体类的属性名、数据库表的字段名
     * @return
     */
    private <E> void whereLike(SqlLikeType sqlLikeType, String keywords, SerializeFunction<T, E>... columns) {
        if (sqlLikeType == null || !checkParamValue(keywords)) {
            return;
        }
        for (SerializeFunction<T, E> column : columns) {
            String c = resolveColumn(column, false);
            String value = null;
            switch (sqlLikeType) {
                case LEFT:
                    value = SqlConstant.PERCENT + keywords;
                case RIGHT:
                    value = keywords + SqlConstant.PERCENT;
                default:
                    value = SqlConstant.PERCENT + keywords + SqlConstant.PERCENT;
            }

            String newCondition = column
                    + SqlConstant.LIKE
                    + SqlConstant.PARAM0;
            where(newCondition, value);
        }
    }


    /**
     * 检查where条件里面传入的参数值，如果返回false则不会往sql添加这个where条件
     *
     * @param value
     * @return
     */
    private boolean checkParamValue(Object value) {
        boolean flag = true;
        if (value instanceof String) {
            if (StringUtils.isBlank((String) value)) {
                flag = false;
            }
        } else {
            if (value == null) {
                flag = false;
            }
        }
        return flag;
    }

    /**
     * 添加自定义的where条件
     *
     * @param condition where条件的sql片段，若需要入参则使用'{此方法params参数的索引，从0开始}'格式；例如：u.name = {0} and u.age = {1}；
     * @param params    condition里面需要传入的sql参数
     */
    private void where(String condition, Object... params) {
        if (StringUtils.isNotBlank(condition)) {
            getSql().WHERE(resolveSql(condition, params));
        }
    }

}
