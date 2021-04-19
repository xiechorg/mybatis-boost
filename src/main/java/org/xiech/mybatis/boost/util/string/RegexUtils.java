package org.xiech.mybatis.boost.util.string;


import java.util.Arrays;

/**
 * 正则表达式相关操作的工具类
 *
 * @author xiech
 * @date 2020-07-16 18:33
 */
public class RegexUtils {
    /**
     * 匹配由key=“value”这种格式的字符
     * 例如：
     * |key1=“value1” key2=“”|
     * 匹配上诉字符串会得到如下结果：
     * =“url”
     * =“”
     */
    public static final String REGEX_MATCHER_KEY_VALUE_INNER_TEXT = "=\"(.*?)\"";
    /**
     * 正则表达式的特殊字符：
     * '\', '$', '(', ')', '*', '+', '.', '[', ']', '?', '^', '{', '}', '|'
     */
    public static final char[] REGEX_SPECIAL_CHARS = {92, 36, 40, 41, 42, 43, 46, 91, 93, 63, 94, 123, 125, 124};

    /**
     * 匹配由大括号{}包裹的除"\r\n"之外的任何字符
     * 例如：
     * |{ad}|{12}|{}|
     * 匹配上诉字符串会得到如下结果：
     * {ad}
     * {12}
     */
    public static final String REGEX_FORMAL_PARAM_ALL = "\\{(.+?)\\}";
    /**
     * 匹配由大括号{}包裹的带数字的字符
     * 例如：
     * |{ad}|{12}|{}|
     * 匹配上诉字符串会得到如下结果：
     * {12}
     */
    public static final String REGEX_FORMAL_PARAM_NUMBER = "\\{(\\d+?)\\}";
    /**
     * 匹配由大括号{}组成的字符
     * 例如：
     * |{ad}|{12}|{}|
     * 匹配上诉字符串会得到如下结果：
     * {}
     */
    public static final String REGEX_FORMAL_PARAM_EMPTY = "\\{\\}";


    /**
     * 转义正则特殊字符 （$()*+.[]?^{},|），注意是否需要转义反斜杠'\'，由于在Java中正则表达式的反斜线后面的字符具有特殊的意义，因此看情况是否对反斜杠做转义。
     *
     * @param string    普通字符串，注意内容不要正则表达式的写法
     * @param backslash 是否需要转义反斜杠
     * @return 发生异常则返回原值
     */
    public static String escapeRegexSpecial(String string, boolean backslash) {
        try {
            StringBuilder newRegex = new StringBuilder();
            char[] chars = string.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                char curr = chars[i];
                if (Arrays.binarySearch(RegexUtils.REGEX_SPECIAL_CHARS, curr) > -1) {
                    char prev = i == 0 ? ' ' : chars[i - 1];
                    char next = i == chars.length - 1 ? ' ' : chars[i + 1];
                    if (backslash || (curr != RegexUtils.REGEX_SPECIAL_CHARS[0])) {
                        newRegex.append(RegexUtils.REGEX_SPECIAL_CHARS[0]).append(curr);
                        continue;
                    }
                }
                newRegex.append(curr);
            }
            return newRegex.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return string;
    }
}
