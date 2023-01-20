package com.yongj.domain;

import com.yongj.enums.FileTaskType;
import com.yongj.repository.FileTaskRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FileTaskDomainTest {

    @Autowired
    private FileTaskRepository repo;

    @Test
    public void should_create_and_finish_file_task() {
        var ft = repo.createFileTask("UE202205142310076187414", FileTaskType.EXPORT, "Test Export");
        Assertions.assertNotNull(ft);

        ft.startTask();
        Assertions.assertTrue(ft.isStarted());
        Assertions.assertFalse(ft.isEnded());

        ft.taskFailed("failed for no reason");
        Assertions.assertTrue(ft.isEnded());
    }

}