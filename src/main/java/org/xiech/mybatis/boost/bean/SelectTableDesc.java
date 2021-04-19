package org.xiech.mybatis.boost.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author xiech
 * @date 2020-07-18 19:22
 */
@Data
//@ToString
@AllArgsConstructor
public class SelectTableDesc {
    private TableDesc tableDesc;
    private String tableAlias;
    private String column;
    private String otherColumn;

    public SelectTableDesc(TableDesc tableDesc, String tableAlias) {
        this.tableDesc = tableDesc;
        this.tableAlias = tableAlias;
    }
}
