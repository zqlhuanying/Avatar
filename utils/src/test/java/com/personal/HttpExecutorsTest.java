package com.personal;

import org.junit.Test;

/**
 * @author qianliao.zhuang
 */
public class HttpExecutorsTest {

    @Test
    public void httpsGetTest() {
        String url = "https://www.baidu.com";

        String res = HttpExecutors.create(url)
                .build()
                .httpGet();
        System.out.println(res);
    }
}
