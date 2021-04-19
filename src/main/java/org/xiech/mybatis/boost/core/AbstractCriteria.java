package org.xiech.mybatis.boost.core;

import org.apache.ibatis.jdbc.SQL;
import org.xiech.mybatis.boost.bean.TableColumnDesc;
import org.xiech.mybatis.boost.bean.TableDesc;
import org.xiech.mybatis.boost.core.constant.SqlConstant;
import org.xiech.mybatis.boost.exception.CriteriaException;
import org.xiech.mybatis.boost.util.lambda.LambdaUtils;
import org.xiech.mybatis.boost.util.lambda.explain.SerializeFunction;
import org.xiech.mybatis.boost.util.string.RegexUtils;
import org.xiech.mybatis.boost.util.string.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SQL语句拼接的工具抽象类，主要包含where条件的封装
 *
 * @author xiech
 * @date 2020-07-16 21:45
 */
public abstract class AbstractCriteria<T, R extends AbstractCriteria<T, R>> implements Criterion<T, String, R> {


    private Class<? extends Object> beanClass;
    private TableDesc tableDesc;
    private Map<String, Object> params = new ConcurrentHashMap<>();
    private int paramsKeyIndex;
    private SQL sql = new SQL();


    public Class<? extends Object> getBeanClass() {
        return beanClass;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public TableDesc getTableDesc() {
        return tableDesc;
    }

    @SuppressWarnings("unchecked")
    public <T> AbstractCriteria(Class<T> beanClass) {
        init(beanClass);
    }


    private <T> void init(Class<T> beanClass) {
        this.beanClass = beanClass;
        this.tableDesc = Reflections.getTableDesc(beanClass);
        if (this.beanClass == null || this.tableDesc == null) {
            throw new CriteriaException(this.beanClass.getName() + ": init error");
        }
    }


    /**
     * 添加自定义的sql参数，返回添加到map前自动生成的key
     *
     * @param value
     * @return
     */
    private String addParams(Object value) {
        String key = SqlConstant.PARAM + this.params.size();
        this.params.put(key, value);
        return key;
    }

    /**
     * 解析带有形参的sql片段，并添加sql参数
     *
     * @param sql    带有形参的sql片段，若需要入参则使用'{此方法params参数的索引，从0开始}'格式；例如：u.name = {0} and u.age = {1}；
     * @param params sql里面需要传入的参数
     */
    public String resolveSql(String sql, Object... params) {
        Map<String, Object> newParams = new HashMap<>();
        String newCondition = StringUtils.formatSuper(sql, RegexUtils.REGEX_FORMAL_PARAM_NUMBER, (findGroupIndex, find) -> {
            int paramsIndex = Integer.valueOf(find.substring(1, find.length() - 1));
            return SqlConstant.HASH_PARAMS + addParams(params[paramsIndex]) + SqlConstant.DELIM_END;
        });
        return newCondition;
    }

    /**
     * 查找表的映射信息对象
     *
     * @param beanClass 实体类的class
     * @return
     */
    public TableDesc findTableDesc(Class<?> beanClass) {
        TableDesc tableDesc = Reflections.getTableDesc(beanClass);
        if (tableDesc == null) {
            throw new CriteriaException(this.beanClass.getName() + ": bean \"" + (beanClass == null ? "" : beanClass.getName()) + "\" can`t find!");
        }
        return tableDesc;
    }

    /**
     * 查找列名的映射信息对象
     *
     * @param column 数据库表的列名、实体类的属性名
     * @return
     */
    private TableColumnDesc findTableColumnDesc(String column) {
        TableColumnDesc tableColumnDesc = this.tableDesc.getColumns().values().stream()
                .filter(x -> x.getFieldName().equals(column) || x.getColumnName().equals(column))
                .findFirst().orElse(null);
        if (tableColumnDesc == null) {
            throw new CriteriaException(this.beanClass.getName() + ": column \"" + (column == null ? "" : column) + "\" can`t find!");
        }
        return tableColumnDesc;
    }

    public String resolveColumn(String column) {
        return resolveColumn(null, column, false);
    }

    public <E> String resolveColumn(SerializeFunction<T, E> column, boolean isAsFieldName) {
        return resolveColumn(null, column, isAsFieldName);
    }

    public <E> String resolveColumn(String prefix, SerializeFunction<T, E> column, boolean isAsFieldName) {
        return resolveColumn(prefix, LambdaUtils.explainFieldName(column), isAsFieldName);
    }

    public String resolveColumn(String prefix, String column, boolean isAsFieldName) {
        StringBuilder newColumn = new StringBuilder();
        if (StringUtils.isNotBlank(prefix)) {
            newColumn.append(prefix);
        }
        TableColumnDesc tableColumnDesc = findTableColumnDesc(column);
        newColumn.append(tableColumnDesc.getDelimiterColumnName());
        if (isAsFieldName) {
            newColumn.append(SqlConstant.AS).append(tableColumnDesc.getFieldName());
        }
        return newColumn.toString();
    }

    @Override
    public SQL getSql() {
        return this.sql;
    }

    @Override
    public String toString() {
        return this.sql.toString();
    }

    @Override
    public R clear() {
        this.sql = new SQL();
        this.params.clear();
        return getSelf();
    }
}
