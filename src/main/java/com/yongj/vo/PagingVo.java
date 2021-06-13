package com.yongj.vo;

import lombok.Data;

/**
 * @author yongjie.zhuang
 */
@Data
public class PagingVo {

    /**
     * Size of a page
     */
    private Integer limit;

    /**
     * Page number
     */
    private Integer page;

    /**
     * Total items
     */
    private Long total;

}
