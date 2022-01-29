package com.yongj.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yongjie.zhuang
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileUploaderInfoVo {

    /** file's id */
    private Integer id;

    /** uploader Id */
    private Integer uploaderId;
}
