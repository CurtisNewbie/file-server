package com.yongj.vo;

import lombok.Data;

import java.util.List;

/**
 * @author yongj.zhuang
 */
@Data
public class ExportAsZipReq {

    private List<Integer> fileIds;
}
