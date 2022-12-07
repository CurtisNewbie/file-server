package com.yongj.services.qry;

import com.curtisnewbie.common.util.*;
import com.curtisnewbie.common.vo.*;
import com.yongj.dao.*;
import com.yongj.enums.*;
import com.yongj.vo.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.transaction.annotation.*;

/**
 * @author yongj.zhuang
 */
@Slf4j
@SpringBootTest
public class VFolderQueryServiceTest {

    @Autowired
    private VFolderQueryService vFolderQueryService;
    @Autowired
    private UserVFolderMapper userVFolderMapper;
    @Autowired
    private VFolderMapper vFolderMapper;

    @Test
    @Transactional
    public void should_list_granted_access() {
        var userNo = "UE202205142310076187414";
        var folderNo = "VFLD20220831004456448796016365959";

        // pre-test
        VFolder folder = new VFolder();
        folder.setFolderNo(folderNo);
        folder.setName("some test folder");
        vFolderMapper.insert(folder);

        UserVFolder uv = new UserVFolder();
        uv.setFolderNo(folderNo);
        uv.setUserNo(userNo);
        uv.setOwnership(VFOwnership.OWNER);
        uv.setGrantedBy(userNo);
        userVFolderMapper.insert(uv);

        for (int i = 0; i < 3; i++) {
            UserVFolder granted = new UserVFolder();
            granted.setFolderNo(folderNo);
            granted.setUserNo(IdUtils.gen("TEST_"));
            granted.setOwnership(VFOwnership.GRANTED);
            granted.setGrantedBy(userNo);
            userVFolderMapper.insert(granted);
        }

        // test
        var req = new ListGrantedFolderAccessReq();
        req.setFolderNo(folderNo);
        final PageableList<GrantedFolderAccess> resp = vFolderQueryService.listGrantedAccess(req, userNo);
        Assertions.assertNotNull(resp);
        Assertions.assertNotNull(resp.getPayload());
        Assertions.assertEquals(3, resp.getPayload().size());
        log.info("Resp.payload: {}", resp.getPayload());
    }

}
