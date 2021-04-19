package org.xiech.mybatis.boost.core;



import org.xiech.mybatis.boost.core.constant.SqlConstant;
import org.xiech.mybatis.boost.core.constant.SqlLikeType;
import org.xiech.mybatis.boost.core.constant.SqlWhereBasicOperator;
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
public abstract class AbstractWhereCriteria<T, R extends AbstractWhereCriteria<T, R>> extends AbstractCriteria<T, R> {

    private List<String> wheres = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public <T> AbstractWhereCriteria(Class<T> beanClass) {
        super(beanClass);
    }

    /**
     * 添加 = 的where条件
     *
     * @param column 实体类的属性名、数据库表的字段名
     * @param value
     * @return
     */
    public R eq(String column, Object value) {
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
    public R gt(String column, Object value) {
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
    public R lt(String column, Object value) {
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
    public R ne(String column, Object value) {
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
    public R ge(String column, Object value) {
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
    public R le(String column, Object value) {
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
    public R between(String column, Object valueStart, Object valueEnd) {
        boolean b1 = checkParamValue(valueStart);
        boolean b2 = checkParamValue(valueEnd);
        if (!b1 && !b2)
            return getSelf();
        column = resolveColumn(column);
        if (b1) {
            String newCondition = column
                    + SqlConstant.GE
                    + SqlConstant.PARAM0;
            where(newCondition, valueStart);
        }
        if (b2) {
            String newCondition = column
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
    public R notBetween(String column, Object valueStart, Object valueEnd) {
        boolean b1 = checkParamValue(valueStart);
        boolean b2 = checkParamValue(valueEnd);
        if (!(b1 && b2))
            return getSelf();
        column = resolveColumn(column);
        String newCondition = column
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
    public R like(String keywords, String... columns) {
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
    public R likeLeft(String keywords, String... columns) {
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
    public R likeRight(String keywords, String... columns) {
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
    public R in(String column, Collection<?> collections) {
        if (!checkParamValue(collections)) {
            return getSelf();
        }
        column = resolveColumn(column);
        String newCondition = column
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
    public R notIn(String column, Collection<?> collections) {
        if (!checkParamValue(collections)) {
            return getSelf();
        }
        column = resolveColumn(column);
        String newCondition = column
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
    private void whereBasic(SqlWhereBasicOperator operator, String column, Object value) {
        if (operator == null || !checkParamValue(value)) {
            return;
        }
        column = resolveColumn(column);
        String newCondition = column
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
    private void whereLike(SqlLikeType sqlLikeType, String keywords, String... columns) {
        if (sqlLikeType == null || !checkParamValue(keywords)) {
            return;
        }
        for (String column : columns) {
            column = resolveColumn(column);
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
