package com.yongj.dao;

import com.curtisnewbie.common.util.EnhancedMapper;
import com.yongj.vo.FileDownloadValidInfo;
import com.yongj.vo.FileInfoVo;
import com.yongj.vo.SelectFileInfoListParam;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yongj.zhuang
 */
public interface FileInfoMapper extends EnhancedMapper<FileInfo> {

    List<FileInfoVo> selectFileListForUserSelective(@Param("offset") long offset, @Param("limit") long limit, @Param("p") SelectFileInfoListParam param);

    /**
     * Count for {@link #selectFileListForUserSelective(long, long, SelectFileInfoListParam)}
     */
    long countFileListForUserSelective(@Param("p") SelectFileInfoListParam param);

    List<FileInfoVo> selectFileListForUserAndTag(@Param("offset") long offset, @Param("limit") long limit, @Param("p") SelectFileInfoListParam param);

    /**
     * Count for {@link #selectFileListForUserAndTag(long, long, SelectFileInfoListParam)} 
     */
    long countFileListForUserAndTag(@Param("p") SelectFileInfoListParam param);

    FileDownloadValidInfo selectValidateInfoById(@Param("id") int fileId, @Param("userId") int userId, @Param("userNo") String userNo);

    Integer selectAnyUserFolderIdForFile(@Param("id") int fileId, @Param("userNo") String userNo);

    /**
     * Select name
     */
    String selectNameById(@Param("id") int id);

    /**
     * Select uploader_id
     */
    Integer selectUploaderIdById(@Param("id") int id);

    /**
     * Logically delete the file by uuid
     */
    void logicDelete(@Param("id") int id);

    /**
     * Select id of files that are logically deleted but not physically deleted
     */
    List<FileInfo> findInfoForPhysicalDeleting();

    /**
     * Mark file being physically deleted
     *
     * @param id         id
     * @param deleteDate the date that is marked as physically deleted
     */
    void markFilePhysicDeleted(@Param("id") int id, @Param("deleteDate") LocalDateTime deleteDate);

    /**
     * Select fs_group_id, uuid, uploader_id, upload_type
     */
    FileInfo selectDownloadInfoById(int id);

}