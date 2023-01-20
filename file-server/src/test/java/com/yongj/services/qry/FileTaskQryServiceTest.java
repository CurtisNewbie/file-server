package com.yongj.services.qry;

import com.curtisnewbie.common.vo.PageableList;
import com.curtisnewbie.common.vo.PagingVo;
import com.yongj.vo.filetask.ListFileTaskReq;
import com.yongj.vo.filetask.ListFileTaskVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class FileTaskQryServiceTest {

    @Autowired
    private FileTaskQryService fileTaskQryService;

    @Test
    public void should_list_file_tasks() {
        var req = new ListFileTaskReq();
        var p = new PagingVo();
        p.setLimit(10);
        p.setPage(1);
        req.setPagingVo(p);
        final PageableList<ListFileTaskVo> pl = fileTaskQryService.listFileTasks("UE202205142344122020573", req);
        Assertions.assertTrue(pl.isPayloadPresent());
        Assertions.assertFalse(pl.getPayload().isEmpty());
        log.info("pl: {}", pl.getPayload());
    }

}