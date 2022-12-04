package com.yongj.helper;

import com.yongj.util.*;
import org.junit.jupiter.api.*;

/**
 * @author yongj.zhuang
 */
public class RandTokenGeneratorTest {

    @Test
    public void should_gen_rand_token(){
        var gen = new RandTokenGenerator();
        final String generate = gen.generate();
        System.out.println(generate);
    }

}
