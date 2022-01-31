package com.yongj.vo;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.util.ValidUtils;
import com.curtisnewbie.common.vo.PageableVo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yongj.enums.FileOwnership;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author yongjie.zhuang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListFileInfoReqVo extends PageableVo {

    /** the group that the file belongs to, 0-public, 1-private */
    private Integer userGroup;

    /** name of the file */
    private String filename;

    /** ownership of file, 0-all files, 1-files that belong to current users */
    private Integer ownership;

    /** id of the user */
    @JsonIgnore
    private Integer userId;

    /** tag name */
    private String tagName;

    /** Validate vo */
    public void validate() throws MsgEmbeddedException {
        ValidUtils.requireNonNull(getPagingVo());
        ValidUtils.requireNonNull(getPagingVo().getLimit());
        ValidUtils.requireNonNull(getPagingVo().getPage());
    }

    /**
     * Check whether we are only requesting files that we own
     */
    public boolean filterForOwnedFilesOnly() {
        return ownership != null && Objects.equals(FileOwnership.parse(ownership), FileOwnership.FILES_OF_THE_REQUESTER);
    }

}
