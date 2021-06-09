package com.yongj.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

/**
 * Utils for JSON processing
 *
 * @author yongjie.zhuang
 */
public final class JsonUtils {

    private static final JsonMapper jsonMapper = new JsonMapper();

    private JsonUtils() {

    }

    /**
     * Write value as String using internally cached {@link JsonMapper}
     *
     * @throws JsonProcessingException
     */
    public static String writeValueAsString(Object o) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(o);
    }

}
