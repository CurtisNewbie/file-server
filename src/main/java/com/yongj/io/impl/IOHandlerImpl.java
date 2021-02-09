package com.yongj.io.impl;

import com.yongj.io.api.IOHandler;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * @author yongjie.zhuang
 */
@Service
public class IOHandlerImpl implements IOHandler {


    @Override
    public Future<byte[]> asyncRead(String relPath) {
        return null;
    }

    @Override
    public void asyncWrite(String relPath, byte[] data) {

    }

    @Override
    public boolean exists(String relPath) {
        return false;
    }
}
