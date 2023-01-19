package com.yongj.file.remote.vo;

import lombok.Data;

import java.util.List;

/**
 * @author yongj.zhuang
 */
@Data
public class GenFileTempTokenReq {
    private List<String> fileKeys;
    private Integer expireInMin;
}
