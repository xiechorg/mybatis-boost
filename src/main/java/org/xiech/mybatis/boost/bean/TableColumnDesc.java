package org.xiech.mybatis.boost.bean;

import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.TypeHandler;
import org.xiech.mybatis.boost.annotation.GenerationType;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.sql.JDBCType;

/**
 * 表的字段映射信息
 *
 * @author xiech
 * @date 2020-07-15 10:52
 */
@Data
@ToString
public class TableColumnDesc implements Serializable {
    private TableDesc tableDesc;
    private String fieldName;

    private Class<? extends Object> foreignBeanClass; //外键关联的目标对象class，默认为null
    private String foreignBeanFieldName; //外键关联的目标对象的属性，默认为id
    private String columnName;
    private String delimiterColumnName;
    private boolean primaryKey;
    private GenerationType idGenerationType;
    private String idGenerator;

    private int precision;
    private int scale;
    private boolean signed;
    private boolean unique;
    private boolean nullable;
    private boolean insertable;
    private boolean updatable;
    private boolean searchable;
    private boolean clob;
    private boolean blob;

    private JDBCType jdbcType;
    private PropertyDescriptor propertyDescriptor;

    private Class<? extends TypeHandler<?>> typeHandler;
}
