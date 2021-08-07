package com.yongj.vo;

import lombok.Data;

/**
 * @author yongjie.zhuang
 */
@Data
public class PhysicDeleteFileVo {

    private Integer id;

    /** file's uuid */
    private String uuid;

    /** uploader id, i.e., user.id */
    private Integer uploaderId;

    /** fs_group's Id */
    private Integer fsGroupId;

}
