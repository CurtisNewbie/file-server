package com.yongj.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * fileServer's version of operate_log vo
 *
 * @author yongjie.zhuang
 */
@Data
public class OperateLogFsVo implements Serializable {

    /** name of operation */
    private String operateName;

    /** description of operation */
    private String operateDesc;

    /** when the operation happens */
    private String operateTime;

    /** parameters used for the operation */
    private String operateParam;

    /** username */
    private String username;

    /** primary key of user */
    private Integer userId;

}