package com.yongj.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Request vo for new file extension
 *
 * @author yongjie.zhuang
 */
@Data
public class AddFileExtReqVo implements Serializable {

    /**
     * Name of file extension
     */
    private String name;

}
