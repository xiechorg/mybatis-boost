package org.xiech.mybatis.boost.bean;

import lombok.Data;
import lombok.ToString;

import java.beans.BeanInfo;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 表的映射信息
 *
 * @author xiech
 * @date 2020-07-15 10:52
 */
@Data
@ToString
public class TableDesc implements Serializable {
    private BeanInfo beanInfo;
    private String beanClassName;
    private String beanName;
    private String tableName;
    private String delimiterTableName;

    private TableColumnDesc primaryKey;
    private String insertSQL;
    private String updateSQL;
    private String selectSQL;
    private String deleteSQL;

    private Map<String, TableColumnDesc> columns = new LinkedHashMap<>();
}
