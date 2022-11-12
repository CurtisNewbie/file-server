package com.yongj.vo.sync;

import com.yongj.file.remote.vo.FileInfoResp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yongj.zhuang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SyncFileInfoResp {
    private FileInfoResp fileInfo;
}
