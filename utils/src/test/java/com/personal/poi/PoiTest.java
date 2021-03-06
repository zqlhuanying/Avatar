package com.personal.poi;

import com.personal.excel.POI;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author qianliao.zhuang
 */
public class PoiTest {

    private static final String dir = "D:\\1\\";
    private static final String path = dir + "policy10000.xlsx";
    private static final int expectedSize = 60036;

    @Before
    public void init() {
        PolicyMapper.init();
    }

    /**
     * 测试一次性读取大文件
     * POI的用户模式
     * 内存太小，导致内存溢出
     */
    @Test(expected = OutOfMemoryError.class)
    public void outOfMemoryTest() {
        POI.<Policy>fromExcel(new File(path))
                .read(Policy.class);
    }

    /**
     * 测试一次性读取大文件
     * POI的事件驱动模式，可减小内存使用
     */
    @Test
    public void noneOutOfMemoryTest() {
        long start = System.currentTimeMillis();
        List<Policy> policies = POI.<Policy>fromExcel(new File(path))
                .eventReader()
                .read(Policy.class);
        long end = System.currentTimeMillis();
        System.out.println("读取耗时：" + (end - start));
        assert policies.size() == expectedSize;
    }

    /**
     * 测试一次性读取大文件
     * POI的事件驱动模式，可减小内存使用
     * 并发情况下
     */
    @Test
    public void noneOutOfMemoryParallelTest() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 20; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    noneOutOfMemoryTest();
                }
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            long end = System.currentTimeMillis();
            System.out.println("总体耗时：" + (end - start));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fork/Join框架和 POI Event 驱动事件的结合
     */
    @Test
    public void bigFileEventTest() {
        long start = System.currentTimeMillis();
        String path = dir + "policy10000.xlsx";
        List<Policy> policies = POI.<Policy>fromExcel(new File(path))
                .eventReader()
                .bigReader()
                .read(Policy.class);
        long end = System.currentTimeMillis();
        System.out.println("读取耗时：" + (end - start));
        assert policies.size() == expectedSize;
    }

    @Test
    public void smallFileWrite() {
        String in = dir + "policy10000.xlsx";
        List<Policy> policies = POI.<Policy>fromExcel(new File(in))
                .eventReader()
                .read(Policy.class);

        long start = System.currentTimeMillis();
        String out = dir + "out.xlsx";
        String path = POI.<Policy>writeExcel(new File(out))
                .sxssfWriter()
                .write(policies, Policy.class);
        long end = System.currentTimeMillis();
        System.out.println("写入耗时： " + (end - start));

        List<Policy> policies0 = POI.<Policy>fromExcel(new File(path))
                .eventReader()
                .read(Policy.class);
        assert policies0.size() == expectedSize;
    }
}
