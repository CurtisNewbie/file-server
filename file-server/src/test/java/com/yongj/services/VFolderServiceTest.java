package com.yongj.services;

import com.yongj.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * @author yongj.zhuang
 */
@Slf4j
@SpringBootTest
public class VFolderServiceTest {

    @Autowired
    private VFolderService vFolderService;


    @Test
    @Transactional
    public void should_not_create_vfolder() {
        var userNo = "UE202205142310076187414";

        // vfolder with same name
        Assertions.assertThrows(Exception.class, () -> {
            vFolderService.createVFolder(CreateVFolderCmd.builder()
                    .name("My Folder")
                    .userNo(userNo)
                    .username("zhuangyongj")
                    .build());
        });
    }

    @Test
    @Transactional
    public void should_create_vfolder() {
        var userNo = "UE202205142310076187414";

        vFolderService.createVFolder(CreateVFolderCmd.builder()
                .name("My Folder 1")
                .userNo(userNo)
                .username("zhuangyongj")
                .build());
    }

    @Test
    @Transactional
    public void should_add_file_to_vfolder() {
        var userId = 3;
        var userNo = "UE202205142310076187414";
        var folderNo = "VFLD20220831004456448796016365959";
        var fk1 = "ZZZ473720639389696107422";
        var fk2 = "ZZZ467894597206016842309";
        var fk3 = "33e7ac8a-6b8a-4a37-9ff7-04515c8a9f74";

        vFolderService.addFileToVFolder(AddFileToVFolderCmd.builder()
                .userId(userId)
                .userNo(userNo)
                .folderNo(folderNo)
                .fileKeys(Arrays.asList(fk1, fk2, fk3))
                .build());
    }

    @Test
    @Transactional
    public void should_remove_file_from_vfolder() {
        var userNo = "UE202205142310076187414";
        var folderNo = "VFLD20220831004456448796016365959";

        vFolderService.removeFileFromVFolder(RemoveFileFromVFolderCmd.builder()
                .userNo(userNo)
                .folderNo(folderNo)
                .fileKeys(Arrays.asList("e1549102-2c01-425b-98a1-07d421acbbfc", "475d453e-0242-41f2-9f26-473cf91f1e35"))
                .build());
    }

    @Test
    @Transactional
    public void should_share_vfolder() {
        var userNo = "UE202205142310076187414";
        var sharedUser = "UE202205142310074386952";
        var folderNo = "VFLD20220831004456448796016365959";

        vFolderService.shareVFolder(ShareVFolderCmd.builder()
                .currUserNo(userNo)
                .sharedToUserNo(sharedUser)
                .folderNo(folderNo)
                .build());
    }

    @Test
    @Transactional
    public void should_remove_granted_access() {
        var userNo = "UE202205142310076187414";
        var sharedUser = "UE202205142310074386952";
        var folderNo = "VFLD20220831004456448796016365959";

        vFolderService.removeGrantedAccess(RemoveGrantedVFolderAccessCmd.builder()
                .currUserNo(userNo)
                .sharedToUserNo(sharedUser)
                .folderNo(folderNo)
                .build());
    }
}


