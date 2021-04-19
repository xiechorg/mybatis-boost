package org.xiech.mybatis.boost.core;



import org.xiech.mybatis.boost.bean.SelectTableDesc;
import org.xiech.mybatis.boost.bean.TableDesc;
import org.xiech.mybatis.boost.core.constant.SqlConstant;
import org.xiech.mybatis.boost.util.string.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 对select相关的拼接sql处理
 *
 * @author xiech
 * @date 2020-07-16 22:04
 */
public abstract class AbstractSelectCriteria<T, R extends AbstractSelectCriteria<T, R>>
        extends AbstractWhereCriteria<T, R> {

    private SelectTableDesc currSelectTableDesc;
    private List<SelectTableDesc> fromTables = new ArrayList<>();
    private List<String> froms = new ArrayList<>();
    private List<String> joins = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public <T> AbstractSelectCriteria(Class<T> beanClass) {
        super(beanClass);
        this.currSelectTableDesc = new SelectTableDesc(getTableDesc(), SqlConstant.TABLE_DEFAULT_ALIAS);
        init();
    }

    private void init() {
        this.fromTables.add(this.currSelectTableDesc);
        this.froms.add(this.getTableDesc().getDelimiterTableName() + SqlConstant.SPACE + this.currSelectTableDesc.getTableAlias());
    }

    public R select(String... columns) {
        for (String column : columns) {
            column = resolveColumn(column);
            getSql().SELECT(column);
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

    public R outerJoin(Class<T> otherBeanClass, String tableAlias, String column, String otherColumn) {
        return join("outer join", otherBeanClass, tableAlias, column, otherColumn);
    }

    public R leftJoin(Class<T> otherBeanClass, String tableAlias, String column, String otherColumn) {
        return join("left join", otherBeanClass, tableAlias, column, otherColumn);
    }

    public R rightJoin(Class<T> otherBeanClass, String tableAlias, String column, String otherColumn) {
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
    private R join(String joinType, Class<T> otherBeanClass, String tableAlias, String column, String otherColumn) {
        if (otherBeanClass == null) {
            return getSelf();
        }

        TableDesc tableDesc = findTableDesc(otherBeanClass);
        if (StringUtils.isBlank(tableAlias)) {
            tableAlias = SqlConstant.TABLE_DEFAULT_ALIAS + this.fromTables.size();
        }

        column = resolveColumn(this.currSelectTableDesc.getTableAlias(), column);
        otherColumn = resolveColumn(tableAlias, otherColumn);
        this.joins.add(joinType + SqlConstant.SPACE + tableDesc.getDelimiterTableName() + SqlConstant.SPACE + tableAlias + SqlConstant.SPACE
                + otherColumn + SqlConstant.EQ + column);

        return getSelf();
    }

    public R orderByAsc(String... columns) {
        return orderBy(true, columns);
    }

    public R orderByDesc(String... columns) {
        return orderBy(false, columns);
    }

    /**
     * 添加order by
     *
     * @param sort    排序方式：true正序、false倒序
     * @param columns 实体类的属性名、数据库表的字段名
     * @return
     */
    private R orderBy(boolean sort, String... columns) {
        for (String column : columns) {
            column = resolveColumn(column);
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
    public R groupBy(String... columns) {
        for (String column : columns) {
            getSql().ORDER_BY(resolveColumn(column));
        }
        return getSelf();
    }

    private String resolveColumn(String prefix, String column) {
        return super.resolveColumn(prefix, column, false);
    }

    @Override
    public String resolveColumn(String column) {
        String prefix = null;
        if (StringUtils.isNotBlank(column)) {
            int dotSplit = column.indexOf(SqlConstant.DOT) + 1;
            if (dotSplit > 1) {
                //如果column里面有指定表别名，类似"t0.name"
                prefix = column.substring(0, dotSplit);
                column = column.substring(dotSplit);
            } else {
                //如果没有指定，那么就是默认查询本表的列名
                prefix = this.currSelectTableDesc.getTableAlias() + SqlConstant.DOT;
            }
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
