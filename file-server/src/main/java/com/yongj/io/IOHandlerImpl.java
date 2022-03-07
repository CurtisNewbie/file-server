package com.yongj.io;

import com.curtisnewbie.common.util.SpiUtils;
import com.yongj.io.operation.DeleteFileOperation;
import com.yongj.io.operation.ReadFileOperation;
import com.yongj.io.operation.WriteFileOperation;
import com.yongj.io.operation.ZipFileOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author yongjie.zhuang
 */
@Slf4j
@Component
public class IOHandlerImpl implements IOHandler {

    @Autowired
    private DeleteFileOperation deleteFileOperation;
    @Autowired
    private ReadFileOperation readFileOperation;
    @Autowired
    private WriteFileOperation writeFileOperation;
    @Autowired
    private ZipFileOperation zipFileOperation;

    @PostConstruct
    void onInit() {
        log.info("{} using: {}", DeleteFileOperation.class.getSimpleName(), deleteFileOperation.getClass().getName());
        log.info("{} using: {}", ReadFileOperation.class.getSimpleName(), readFileOperation.getClass().getName());
        log.info("{} using: {}", WriteFileOperation.class.getSimpleName(), writeFileOperation.getClass().getName());
        log.info("{} using: {}", ZipFileOperation.class.getSimpleName(), zipFileOperation.getClass().getName());
    }

    @Override
    public long writeFile(@NotEmpty String absPath, @NotNull InputStream inputStream) throws IOException {
        return writeFileOperation.writeFile(absPath, inputStream);
    }

    @Override
    public void createParentDirIfNotExists(@NotEmpty String absPath) throws IOException {
        Files.createDirectories(Paths.get(absPath).getParent());
    }

    @Override
    public boolean exists(@NotEmpty String absPath) {
        return Files.exists(Paths.get(absPath));
    }

    @Override
    public long writeZipFile(@NotEmpty String absPath, @NotEmpty List<ZipCompressEntry> entries) throws IOException {
        return zipFileOperation.compressFile(absPath, entries);
    }

    @Override
    public void readFile(@NotEmpty String absPath, @NotNull OutputStream outputStream) throws IOException {
        readFileOperation.readFile(absPath, outputStream);
    }

    @Override
    public void deleteFile(@NotEmpty String absPath) throws IOException {
        deleteFileOperation.deleteFile(absPath);
    }

    @Configuration
    private static class OperationLoader {

        public OperationLoader() {
        }

        @Bean
        public DeleteFileOperation deleteFileOperation() {
            return SpiUtils.loadFirst(DeleteFileOperation.class);
        }

        @Bean
        public ReadFileOperation readFileOperation() {
            return SpiUtils.loadFirst(ReadFileOperation.class);
        }

        @Bean
        public WriteFileOperation writeFileOperation() {
            return SpiUtils.loadFirst(WriteFileOperation.class);
        }

        @Bean
        public ZipFileOperation zipFileOperation() {
            return SpiUtils.loadFirst(ZipFileOperation.class);
        }
    }
}
