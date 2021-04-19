package org.xiech.mybatis.boost.core;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;
import org.xiech.mybatis.boost.annotation.Column;
import org.xiech.mybatis.boost.annotation.GenerationType;
import org.xiech.mybatis.boost.annotation.Id;
import org.xiech.mybatis.boost.annotation.Table;
import org.xiech.mybatis.boost.bean.TableColumnDesc;
import org.xiech.mybatis.boost.bean.TableDesc;
import org.xiech.mybatis.boost.util.lambda.LambdaUtils;
import org.xiech.mybatis.boost.util.lambda.explain.ExplainFieldNameResult;
import org.xiech.mybatis.boost.util.lambda.explain.SerializeFunction;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实体类映射的工具类，优化java bean的反射缓存
 *
 * @author xiech
 * @date 2020-07-15 00:56:25
 **/
public class Reflections {

    /**
     * 字段映射到数据库的命名规则
     **/
    public static enum DatabaseColumnNameStyle {
        NORMAL,                        //原值
        CAMELHUMP_AND_LOWERCASE,        //驼峰转下划线小写形式
        CAMELHUMP_AND_UPPERCASE        //驼峰转下划线大写形式
        //UPPERCASE,                  	//转换为大写
        //LOWERCASE,                  	//转换为小写
    }

    private static DatabaseColumnNameStyle DATABASE_COLUMN_NAME_STYLE = DatabaseColumnNameStyle.CAMELHUMP_AND_LOWERCASE;
    private static String DATABASE_DELIMITER = "";


    private static final String ID_DEFAULT = "id";
    private static final String[] MOVE_DOWN_FIELDS = {"createTime", "createBy", "updateTime", "updateBy"};

    /**
     * SerializeFunction解析属性名的缓存，使用弱引用对象存储
     */
    private static final Map<String, TableDesc> BEAN_DESC_CACHE = new ConcurrentHashMap<>();
    private static final List<String> POJO_FIELD_EXCLUDE = new ArrayList<String>() {{
        add("class");
    }};


    public static void init(DatabaseColumnNameStyle databaseColumnNameStyle, String databaseDelimiter) {
        Reflections.DATABASE_COLUMN_NAME_STYLE = databaseColumnNameStyle;
        Reflections.DATABASE_DELIMITER = databaseDelimiter;
    }

    /**
     * 通过Bean::getField()这种无参有返回值的Lambda表达式来获取JavaBean的属性名，获取失败时返回null。
     *
     * @param function 无参有返回值的Lambda表达式，类似Bean::getField()
     * @return
     */
    public static <T, R> String explainFieldName(SerializeFunction<T, R> function) {
        ExplainFieldNameResult result = LambdaUtils.explainFieldNameResult(function);
        if (result == null) {
            return null;
        }
        // 判断是否是正常的实体类属性名，如果不是则返回null
        TableDesc tableDesc = explainTableDesc(result.getBeanClassName());
        if (tableDesc == null || !tableDesc.getColumns().containsKey(result.getFieldName())) {
            return null;
        }
        return result.getFieldName();
    }

    /**
     * 通过class获取缓存的TableDesc
     *
     * @param beanClass
     * @return
     */
    public static TableDesc getTableDesc(Class<? extends Object> beanClass) {
        if (beanClass != null) {
            return explainTableDesc(beanClass.getName());
        }
        return null;
    }


    /**
     * 通过class name获取缓存的TableDesc
     *
     * @param beanClassName
     * @return
     */
    private static TableDesc explainTableDesc(String beanClassName) {
        return Optional.ofNullable(BEAN_DESC_CACHE.get(beanClassName))
                .orElseGet(() -> {
                    TableDesc tableDesc = resolveTableDesc(beanClassName);
                    if (tableDesc != null) {
                        BEAN_DESC_CACHE.put(beanClassName, tableDesc);

                        // 生成SQL语句
                        tableDesc.setInsertSQL(getInsertSQL(tableDesc));
                        tableDesc.setUpdateSQL(getUpdateSQL(tableDesc));
                        tableDesc.setSelectSQL(getSelectSQL(tableDesc));
                        tableDesc.setDeleteSQL(getDeleteSQL(tableDesc));
                    }
                    return tableDesc;
                });
    }

    /**
     * 通过class name生成TableDesc
     *
     * @param beanClassName
     * @return
     */
    private static TableDesc resolveTableDesc(String beanClassName) {
        TableDesc tableDesc = null;
        try {
            Class<?> beanClass = Class.forName(beanClassName);
            Table tableAnnotation = beanClass.getAnnotation(Table.class);
            /*if (tableAnnotation == null) {
                return null;
            }*/
            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            if (beanInfo.getPropertyDescriptors() == null || beanInfo.getPropertyDescriptors().length == 0) {
                return null;
            }
            tableDesc = new TableDesc();
            tableDesc.setBeanInfo(beanInfo);
            tableDesc.setBeanClassName(beanClass.getName());
            tableDesc.setBeanName(beanClass.getSimpleName());
            tableDesc.setTableName(tableAnnotation != null && StringUtils.isNotBlank(tableAnnotation.name())
                    ? tableAnnotation.name() : renameByStyle(tableDesc.getBeanName()));
            tableDesc.setDelimiterTableName(delimiter(tableDesc.getTableName()));

            List<PropertyDescriptor> propertyDescriptors = new ArrayList<>();
            PropertyDescriptor idPD = null;
            PropertyDescriptor createTimePD = null;
            PropertyDescriptor createByPD = null;
            PropertyDescriptor updateTimePD = null;
            PropertyDescriptor updateByPD = null;
            for (PropertyDescriptor p : beanInfo.getPropertyDescriptors()) {
                if (p.getName().equals(ID_DEFAULT)) {
                    idPD = p;
                } else if (p.getName().equals(MOVE_DOWN_FIELDS[0])) {
                    createTimePD = p;
                } else if (p.getName().equals(MOVE_DOWN_FIELDS[1])) {
                    createByPD = p;
                } else if (p.getName().equals(MOVE_DOWN_FIELDS[2])) {
                    updateTimePD = p;
                } else if (p.getName().equals(MOVE_DOWN_FIELDS[3])) {
                    updateByPD = p;
                } else {
                    propertyDescriptors.add(p);
                }
            }
            if (idPD != null) {
                propertyDescriptors.add(0, idPD);
            }
            if (createTimePD != null) {
                propertyDescriptors.add(propertyDescriptors.size(), createTimePD);
            }
            if (createByPD != null) {
                propertyDescriptors.add(propertyDescriptors.size(), createByPD);
            }
            if (updateTimePD != null) {
                propertyDescriptors.add(propertyDescriptors.size(), updateTimePD);
            }
            if (updateByPD != null) {
                propertyDescriptors.add(propertyDescriptors.size(), updateByPD);
            }

            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                TableColumnDesc tableColumnDesc = resolveTableColumnDesc(tableDesc, propertyDescriptor);
                if (tableColumnDesc != null) {
                    tableDesc.getColumns().put(tableColumnDesc.getFieldName(), tableColumnDesc);

                    // 设置主键
                    if (tableColumnDesc.isPrimaryKey()) {
                        tableDesc.setPrimaryKey(tableColumnDesc);
                    }
                }
            }

            // 没有添加Id注解时，则设置默认主键为id列（如果查找到有id列的话）
            if (tableDesc.getPrimaryKey() == null) {
                TableColumnDesc tableColumnDesc = tableDesc.getColumns().entrySet().parallelStream()
                        .map(x -> x.getValue()).filter(x -> ID_DEFAULT.equalsIgnoreCase(x.getFieldName()))
                        .findFirst().orElse(null);
                tableDesc.setPrimaryKey(tableColumnDesc);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tableDesc;
    }

    /**
     * 通过PropertyDescriptor生成BeanColumnDesc
     *
     * @param tableDesc
     * @param propertyDescriptor
     * @return
     */
    private static TableColumnDesc resolveTableColumnDesc(TableDesc tableDesc, PropertyDescriptor propertyDescriptor) {
        TableColumnDesc tableColumnDesc = null;
        try {
            if (tableDesc == null || propertyDescriptor == null
                    || (propertyDescriptor.getReadMethod() == null && propertyDescriptor.getWriteMethod() == null)
                    || POJO_FIELD_EXCLUDE.contains(propertyDescriptor.getName())) {
                return null;
            }

            tableColumnDesc = new TableColumnDesc();
            tableColumnDesc.setTableDesc(tableDesc);
            tableColumnDesc.setFieldName(propertyDescriptor.getName());
            tableColumnDesc.setPropertyDescriptor(propertyDescriptor);

            Id idAnnotation = propertyDescriptor.getPropertyType().getAnnotation(Id.class);
            Column columnAnnotation = propertyDescriptor.getPropertyType().getAnnotation(Column.class);
            if (idAnnotation != null) {
                tableColumnDesc.setPrimaryKey(true);
                tableColumnDesc.setIdGenerationType(idAnnotation.generationType());
                tableColumnDesc.setIdGenerator(idAnnotation.generator());
            } else {
                tableColumnDesc.setIdGenerationType(GenerationType.AUTO);
            }
            if (columnAnnotation != null) {
                tableColumnDesc.setColumnName(StringUtils.isNotBlank(columnAnnotation.name()) ? columnAnnotation.name() : renameByStyle(tableColumnDesc.getFieldName()));
                tableColumnDesc.setDelimiterColumnName(delimiter(tableColumnDesc.getColumnName()));

                //外键关联属性
                tableColumnDesc.setForeignBeanClass(columnAnnotation.foreignBeanClass());
                tableColumnDesc.setForeignBeanFieldName(columnAnnotation.foreignBeanFieldName());

                tableColumnDesc.setPrecision(columnAnnotation.precision());
                tableColumnDesc.setScale(columnAnnotation.scale());
                tableColumnDesc.setSigned(columnAnnotation.signed());
                tableColumnDesc.setUnique(columnAnnotation.unique());
                tableColumnDesc.setNullable(columnAnnotation.nullable());
                tableColumnDesc.setInsertable(columnAnnotation.insertable());
                tableColumnDesc.setUpdatable(columnAnnotation.updatable());
                tableColumnDesc.setSearchable(columnAnnotation.searchable());
                tableColumnDesc.setClob(columnAnnotation.isClob());
                tableColumnDesc.setBlob(columnAnnotation.isBlob());
                tableColumnDesc.setJdbcType(columnAnnotation.jdbcType());
                tableColumnDesc.setTypeHandler(columnAnnotation.typeHandler());
            } else {
                tableColumnDesc.setColumnName(renameByStyle(tableColumnDesc.getFieldName()));
                tableColumnDesc.setDelimiterColumnName(delimiter(tableColumnDesc.getColumnName()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tableColumnDesc;
    }


    private static String delimiter(String name) {
        if (name == null)
            return null;
        return DATABASE_DELIMITER + name + DATABASE_DELIMITER;
    }

    private static String renameByStyle(String name) {
        if (name == null || DATABASE_COLUMN_NAME_STYLE == null) {
            return name;
        }
        switch (DATABASE_COLUMN_NAME_STYLE) {
            case NORMAL:
                return name;
            /*case UPPERCASE:
                return name.toUpperCase();
            case LOWERCASE:
                return name.toLowerCase();*/
            case CAMELHUMP_AND_UPPERCASE:
                return camelhump(name).toUpperCase();
            case CAMELHUMP_AND_LOWERCASE:
                return camelhump(name).toLowerCase();
            default:
                return name;
        }
    }

    private static String camelhump(String name) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);

            if (Character.isUpperCase(c)) {
                if (sb.length() != 0) {
                    sb.append("_");
                }
                sb.append(c);
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * 还原 驼峰命名
     *
     * @param name
     * @return
     */
    private static String restoreCamelhump(String name) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);

            if (i == 0) {
                sb.append(c);
            } else if (c == '_') {
                c = name.charAt(i + 1);
                name = name.replace(c, Character.toUpperCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String getInsertSQL(TableDesc tableDesc) {

        SQL sql = new SQL();
        sql.INSERT_INTO(tableDesc.getDelimiterTableName());
        for (TableColumnDesc tableColumnDesc : tableDesc.getColumns().values()) {
            String value = "";
            if (tableColumnDesc.isPrimaryKey()) {
                if (tableColumnDesc.getIdGenerationType() == GenerationType.AUTO) {
                    continue;
                } else if (tableColumnDesc.getIdGenerationType() == GenerationType.SEQUENCE) {
                    value = tableColumnDesc.getIdGenerator() + ".NEXTVAL";
                } else if (tableColumnDesc.getIdGenerationType() == GenerationType.UUID) {
                    value = "#{@static@UUID}";
                } else if (tableColumnDesc.getIdGenerationType() == GenerationType.METHOD) {
                    value = "#{@static@" + tableColumnDesc.getIdGenerator() + "}";
                } else {
                    throw new RuntimeException(tableDesc.getBeanClassName() + ".getInsertSQL() run error!");
                }
            } else if (tableColumnDesc.getForeignBeanClass() != null) {
                TableDesc joinTableDesc = getTableDesc(tableColumnDesc.getPropertyDescriptor().getPropertyType());
                if (joinTableDesc != null) {
                    TableColumnDesc joinTableColumnDesc = joinTableDesc.getColumns().get(tableColumnDesc.getForeignBeanFieldName());
                    if (joinTableColumnDesc != null) {
                        value = "#{" + tableColumnDesc.getFieldName() + "." + joinTableColumnDesc.getFieldName() + "}";
                    } else {
                        throw new RuntimeException(tableDesc.getBeanClassName() + ".getInsertSQL() run error!");
                    }
                } else {
                    throw new RuntimeException(tableDesc.getBeanClassName() + ".getInsertSQL() run error!");
                }
            } else {
                value = "#{" + tableColumnDesc.getFieldName() + "}";
            }
            sql.VALUES(tableColumnDesc.getDelimiterColumnName(), value);
        }

        return sql.toString();
    }

    private static String getUpdateSQL(TableDesc tableDesc) {
        SQL sql = new SQL();
        sql.UPDATE(tableDesc.getDelimiterTableName());
        for (TableColumnDesc tableColumnDesc : tableDesc.getColumns().values()) {

            String value = "";
            if (tableColumnDesc.isPrimaryKey()) {
                continue;
            } else if (tableColumnDesc.getForeignBeanClass() != null) {
                TableDesc joinTableDesc = getTableDesc(tableColumnDesc.getPropertyDescriptor().getPropertyType());
                if (joinTableDesc != null) {
                    TableColumnDesc joinTableColumnDesc = joinTableDesc.getColumns().get(tableColumnDesc.getForeignBeanFieldName());
                    if (joinTableColumnDesc != null) {
                        value = "#{" + tableColumnDesc.getFieldName() + "." + joinTableColumnDesc.getFieldName() + "}";
                    } else {
                        throw new RuntimeException(tableDesc.getBeanClassName() + ".getInsertSQL() run error!");
                    }
                } else {
                    throw new RuntimeException(tableDesc.getBeanClassName() + ".getInsertSQL() run error!");
                }
            } else {
                value = "#{" + tableColumnDesc.getFieldName() + "}";
            }

            sql.SET(tableColumnDesc.getDelimiterColumnName() + "=" + value);
        }
        /*if (tableDesc.getPrimaryKey() != null) {
            sql = sql.WHERE(tableDesc.getPrimaryKey().getDelimiterColumnName() + "=" + "#{" + tableDesc.getPrimaryKey().getFieldName() + "}");
        }*/

        return sql.toString();
    }

    private static String getDeleteSQL(TableDesc tableDesc) {

        SQL sql = new SQL();
        sql.DELETE_FROM(tableDesc.getDelimiterTableName());
        /*if (tableDesc.getPrimaryKey() != null) {
            sql = sql.WHERE(tableDesc.getPrimaryKey().getDelimiterColumnName() + "=" + "#{" + tableDesc.getPrimaryKey().getFieldName() + "}");
        }*/

        return sql.toString();
    }

    private static String getSelectSQL(TableDesc tableDesc) {
        SQL sql = new SQL();
        for (TableColumnDesc tableColumnDesc : tableDesc.getColumns().values()) {
            sql.SELECT(tableColumnDesc.getDelimiterColumnName() + "AS" + tableColumnDesc.getFieldName());
        }
        sql.FROM(tableDesc.getDelimiterTableName());
        /*if (tableDesc.getPrimaryKey() != null) {
            sql = sql.WHERE(tableDesc.getPrimaryKey().getDelimiterColumnName() + "=" + "#{" + tableDesc.getPrimaryKey().getFieldName() + "}");
        }*/

        return sql.toString();
    }

}