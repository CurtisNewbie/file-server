package com.yongj.domain;

import com.curtisnewbie.common.domain.Domain;
import com.curtisnewbie.common.util.*;
import com.yongj.dao.AppFile;
import com.yongj.dao.AppFileMapper;
import com.yongj.dao.FsGroup;
import com.yongj.enums.FsGroupType;
import com.yongj.helper.FsGroupIdResolver;
import com.yongj.helper.WriteFsGroupSupplier;
import com.yongj.io.IOHandler;
import com.yongj.io.PathResolver;
import com.yongj.vo.AppFileDownloadInfo;
import com.yongj.vo.UploadAppFileCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static com.curtisnewbie.common.util.AssertUtils.notNull;

/**
 * AppFile Domain
 *
 * @author yongj.zhuang
 */
@Slf4j
@Domain
@Validated
public class AppFileDomain {

    public static final String APP_FILE_KEY_PRE = "AF";

    @Nullable
    private AppFile appFile;

    private final AppFileMapper appFileMapper;
    private final PathResolver pathResolver;
    private final IOHandler ioHandler;
    private final WriteFsGroupSupplier writeFsGroupSupplier;
    private final FsGroupIdResolver fsGroupIdResolver;

    public AppFileDomain(AppFileMapper appFileMapper,
                         PathResolver pathResolver,
                         IOHandler ioHandler,
                         WriteFsGroupSupplier writeFsGroupSupplier,
                         FsGroupIdResolver fsGroupIdResolver) {
        this.appFileMapper = appFileMapper;
        this.pathResolver = pathResolver;
        this.ioHandler = ioHandler;
        this.writeFsGroupSupplier = writeFsGroupSupplier;
        this.fsGroupIdResolver = fsGroupIdResolver;
    }

    /**
     * Upload App File
     *
     * @return uuid
     */
    public String uploadAppFile(@Valid @NotNull UploadAppFileCmd cmd) throws IOException {

        // gen fileKey
        final String fileKey = IdUtils.gen(APP_FILE_KEY_PRE);

        // resolve abs path
        final FsGroupType fst = FsGroupType.APP;
        final FsGroup fsGroup = writeFsGroupSupplier.supply(fst);
        notNull(fsGroup, "Writable FsGroup for type: %s is not found", fst);
        final String absPath = pathResolver.resolveAbsolutePath(fileKey, cmd.getAppName(), fsGroup.getBaseFolder());

        // write the file and get the size of it
        final long size = ioHandler.writeFile(absPath, cmd.getInputStream());

        // persist record
        AppFile appFile = new AppFile();
        appFile.setName(cmd.getFileName());
        appFile.setUuid(fileKey);
        appFile.setSize(size);
        appFile.setAppName(cmd.getAppName());
        appFile.setUserId(cmd.getUserId());
        appFile.setFsGroupId(fsGroup.getId());
        appFileMapper.insert(appFile);

        return fileKey;
    }

    /** Obtain an download info for the app file */
    public AppFileDownloadInfo obtainDownloadInfo() throws IOException {
        Assert.notNull(appFile, "AppFile == null");

        final int fsgId = appFile.getFsGroupId();
        final FsGroup fsGroup = fsGroupIdResolver.resolve(fsgId);
        notNull(fsGroup, "FsGroup of id: %s is not found", fsgId);

        final String absPath = pathResolver.resolveAbsolutePath(appFile.getUuid(), appFile.getAppName(), fsGroup.getBaseFolder());
        return AppFileDownloadInfo.builder()
                .fileChannel(FileChannel.open(Paths.get(absPath), StandardOpenOption.READ))
                .name(appFile.getName())
                .size(appFile.getSize())
                .build();
    }

    // -------------------------------------- getters ----------------------------------------

    public long getSize() {
        return appFile.getSize();
    }

    // -------------------------------------- private ----------------------------------------

    public AppFileDomain _with(AppFile ap) {
        Assert.notNull(ap, "AppFile == null");
        this.appFile = ap;
        return this;
    }

}
