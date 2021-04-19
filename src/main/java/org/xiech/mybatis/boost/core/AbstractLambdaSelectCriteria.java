package org.xiech.mybatis.boost.core;



import org.xiech.mybatis.boost.bean.SelectTableDesc;
import org.xiech.mybatis.boost.bean.TableDesc;
import org.xiech.mybatis.boost.core.constant.SqlConstant;
import org.xiech.mybatis.boost.util.lambda.explain.SerializeFunction;
import org.xiech.mybatis.boost.util.string.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 对select相关的拼接sql处理
 *
 * @author xiech
 * @date 2020-07-16 22:04
 */
public abstract class AbstractLambdaSelectCriteria<T, R extends AbstractLambdaSelectCriteria<T, R>>
        extends AbstractWhereCriteria<T, R> {

    private SelectTableDesc currSelectTableDesc;
    private List<SelectTableDesc> fromTables = new ArrayList<>();
    private List<String> froms = new ArrayList<>();
    private List<String> joins = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public <T> AbstractLambdaSelectCriteria(Class<T> beanClass) {
        super(beanClass);
        this.currSelectTableDesc = new SelectTableDesc(getTableDesc(), SqlConstant.TABLE_DEFAULT_ALIAS);
        init();
    }

    private void init() {
        this.fromTables.add(this.currSelectTableDesc);
        this.froms.add(this.getTableDesc().getDelimiterTableName() + SqlConstant.SPACE + this.currSelectTableDesc.getTableAlias());
    }

    public <E> R select(SerializeFunction<T, E>... columns) {
        for (SerializeFunction<T, E> column : columns) {
            String c = resolveColumn(column, false);
            getSql().SELECT(c);
        }
        return getSelf();
    }

    public R from(Class<T> otherBeanClass) {
        return from(otherBeanClass, null);
    }

    /**
     * 多表查询（只是生成from关键词后面的table1,table2这种），可以为表名指定别名（默认按照添加顺序自动生成别名：t、t0、t1...）
     *
     * @param otherBeanClass
     * @param tableAlias
     * @return
     */
    public R from(Class<T> otherBeanClass, String tableAlias) {
        if (otherBeanClass == null) {
            return getSelf();
        }

        TableDesc tableDesc = findTableDesc(otherBeanClass);
        if (StringUtils.isBlank(tableAlias)) {
            tableAlias = SqlConstant.TABLE_DEFAULT_ALIAS + this.fromTables.size();
        }
        this.fromTables.add(new SelectTableDesc(tableDesc, tableAlias));
        this.froms.add(tableDesc.getDelimiterTableName() + SqlConstant.SPACE + tableAlias);

        return getSelf();
    }

    public <E> R outerJoin(Class<T> otherBeanClass, String tableAlias, SerializeFunction<T, E> column, SerializeFunction<T, E> otherColumn) {
        return join("outer join", otherBeanClass, tableAlias, column, otherColumn);
    }

    public <E> R leftJoin(Class<T> otherBeanClass, String tableAlias, SerializeFunction<T, E> column, SerializeFunction<T, E> otherColumn) {
        return join("left join", otherBeanClass, tableAlias, column, otherColumn);
    }

    public <E> R rightJoin(Class<T> otherBeanClass, String tableAlias, SerializeFunction<T, E> column, SerializeFunction<T, E> otherColumn) {
        return join("right join", otherBeanClass, tableAlias, column, otherColumn);
    }

    /**
     * 连接查询
     *
     * @param joinType       连接类型
     * @param otherBeanClass
     * @param tableAlias
     * @param column         实体<T>的字段
     * @param otherColumn    外键字段
     * @return
     */
    private <E> R join(String joinType, Class<T> otherBeanClass, String tableAlias, SerializeFunction<T, E> column, SerializeFunction<T, E> otherColumn) {
        if (otherBeanClass == null) {
            return getSelf();
        }

        TableDesc tableDesc = findTableDesc(otherBeanClass);
        if (StringUtils.isBlank(tableAlias)) {
            tableAlias = SqlConstant.TABLE_DEFAULT_ALIAS + this.fromTables.size();
        }

        String c = resolveColumn(this.currSelectTableDesc.getTableAlias(), column, false);
        String otherC = resolveColumn(tableAlias, otherColumn, false);
        this.joins.add(joinType + SqlConstant.SPACE + tableDesc.getDelimiterTableName() + SqlConstant.SPACE + tableAlias + SqlConstant.SPACE
                + otherC + SqlConstant.EQ + c);

        return getSelf();
    }

    public <E> R orderByAsc(SerializeFunction<T, E>... columns) {
        return orderBy(true, columns);
    }

    public <E> R orderByDesc(SerializeFunction<T, E>... columns) {
        return orderBy(false, columns);
    }

    /**
     * 添加order by
     *
     * @param sort    排序方式：true正序、false倒序
     * @param columns 实体类的属性名、数据库表的字段名
     * @return
     */
    private <E> R orderBy(boolean sort, SerializeFunction<T, E>... columns) {
        for (SerializeFunction<T, E> column : columns) {
            String c = resolveColumn(column, false);
            String newCondition = column
                    + (sort ? SqlConstant.ASC : SqlConstant.DESC);
            getSql().ORDER_BY(newCondition);
        }
        return getSelf();
    }

    /**
     * 添加group by
     *
     * @param columns 实体类的属性名、数据库表的字段名
     * @return
     */
    public <E> R groupBy(SerializeFunction<T, E>... columns) {
        for (SerializeFunction<T, E> column : columns) {
            getSql().ORDER_BY(resolveColumn(column, false));
        }
        return getSelf();
    }

    @Override
    public <E> String resolveColumn(SerializeFunction<T, E> column, boolean isAsFieldName) {
        return resolveColumn(null, column, false);
    }

    @Override
    public <E> String resolveColumn(String prefix, SerializeFunction<T, E> column, boolean isAsFieldName) {
        if (StringUtils.isBlank(prefix)) {
            //如果没有指定，那么就是默认查询本表的列名
            prefix = this.currSelectTableDesc.getTableAlias() + SqlConstant.DOT;
        }
        return super.resolveColumn(prefix, column, false);
    }

    @Override
    public R clear() {
        this.fromTables.clear();
        this.froms.clear();
        this.joins.clear();

        init();
        return super.clear();
    }

    @Override
    public String toString() {
        // from tables
        this.froms.forEach(x -> getSql().FROM(x));
        // join tables
        this.joins.forEach(x -> getSql().FROM(x));

        return super.toString();
    }
}
