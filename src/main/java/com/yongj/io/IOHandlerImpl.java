package com.yongj.io;

import com.yongj.io.operation.DeleteFileOperation;
import com.yongj.io.operation.ReadFileOperation;
import com.yongj.io.operation.WriteFileOperation;
import com.yongj.io.operation.ZipFileOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author yongjie.zhuang
 */
@Component
public class IOHandlerImpl implements IOHandler {

    private static final Logger logger = LoggerFactory.getLogger(IOHandlerImpl.class);

    @Autowired
    private DeleteFileOperation deleteFileOperation;
    @Autowired
    private ReadFileOperation readFileOperation;
    @Autowired
    private WriteFileOperation writeFileOperation;
    @Autowired
    private ZipFileOperation zipFileOperation;

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
        return Files.exists(Path.of(absPath));
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
}
