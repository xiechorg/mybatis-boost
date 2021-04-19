package org.xiech.mybatis.boost.core.constant;

/**
 * sql相关的字符常量
 *
 * @author xiech
 * @date 2020-07-18 1:14
 */
public interface SqlConstant {

    public static final String SPACE = " ";
    public static final String TABLE_DEFAULT_ALIAS = "t";


    public static final String EQ = " = ";
    public static final String GT = " > ";
    public static final String LT = " < ";
    public static final String NE = " != ";
    public static final String GE = " >= ";
    public static final String LE = " <= ";
    public static final String AND = " AND ";
    public static final String OR = " OR ";
    public static final String LIKE = " LIKE ";
    public static final String IN = " IN ";
    public static final String NOT_IN = " NOT IN ";
    public static final String EXISTS = " EXISTS ";
    public static final String NOT_EXISTS = " NOT EXISTS ";
    public static final String DESC = " DESC";
    public static final String ASC = " ASC";
    public static final String AS = " AS";


    public static final String PERCENT = "%";
    public static final String UNDERSCORE = "_";
    public static final String DOLLAR = "$";
    public static final String DOT = ".";
    public static final String HASH = "#";
    public static final String DELIM_START = "{";
    public static final String DELIM_END = "}";
    public static final String PARAM = "param";
    public static final String PARAM0 = "{0}";
    public static final String PARAM1 = "{1}";
    public static final String PARAMS = "params";
    public static final String HASH_PARAMS = HASH + DELIM_START + PARAMS + DOT;
}
