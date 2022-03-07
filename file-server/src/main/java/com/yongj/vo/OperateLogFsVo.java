package com.yongj.vo;

import com.curtisnewbie.common.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

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
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY_HH_MM)
    private Date operateTime;

    /** parameters used for the operation */
    private String operateParam;

    /** username */
    private String username;

    /** primary key of user */
    private Integer userId;

}