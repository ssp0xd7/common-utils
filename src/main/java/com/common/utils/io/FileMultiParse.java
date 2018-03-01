package com.common.utils.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.lang3.StringUtils;

/**
 * 多线程读文件
 *
 * @author kevin(ssp0xd7 @ gmail.com) 01/03/2018
 */
public abstract class FileMultiParse {

    private ExecutorService threads = Executors.newFixedThreadPool(30);

    private final int THREAD_NUM = 10;

    private final char end_separator = '\n';

    private String charSet = "GBK";

    /**
     * 文件解析
     * <p>
     * 一行数据解析为一个对象
     * <p>
     * 对象属性仅支持基本类型(时间格式待定)
     * <p>
     * 多线程读，多线程消费
     *
     * @param clazz
     *            解析后的对象
     * @return
     * @throws Exception
     */
    public <T> List<T> parse(final File file, final Class<T> clazz, String charSet,
        HashMap<String, Integer> nameIndexMap) throws Exception {
        if (StringUtils.isNotBlank(charSet)) {
            this.charSet = charSet;
        }
        if (nameIndexMap == null) {
            return Collections.emptyList();
        }
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        long totalLength = randomAccessFile.length();

        long gap = totalLength / THREAD_NUM;
        long checkIndex = 0;
        final long[] beginIndexs = new long[THREAD_NUM];
        final long[] endIndexs = new long[THREAD_NUM];

        for (int n = 0; n < THREAD_NUM; n++) {
            beginIndexs[n] = checkIndex;
            if (n + 1 == THREAD_NUM) {
                endIndexs[n] = totalLength;
                break;
            }
            checkIndex += gap;
            long gapToEof = getGapToEOF(checkIndex, randomAccessFile);

            checkIndex += gapToEof;
            endIndexs[n] = checkIndex;
        }
        final List<T> result = Collections.synchronizedList(new ArrayList<T>());
        final CountDownLatch latch = new CountDownLatch(THREAD_NUM);
        for (int n = 0; n < THREAD_NUM; n++) {
            final int finalN = n;
            threads.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        result.addAll(readDatas(beginIndexs[finalN], endIndexs[finalN], file, clazz));
                        latch.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        latch.await();
        randomAccessFile.close();
        return result;
    }

    /**
     * 获取每行结束标识位
     *
     * @param beginIndex
     * @param randomAccessFile
     * @return
     * @throws IOException
     */
    private long getGapToEOF(long beginIndex, RandomAccessFile randomAccessFile) throws IOException {
        randomAccessFile.seek(beginIndex);
        long count = 0;
        while (randomAccessFile.read() != end_separator) {
            count++;
        }
        return ++count;
    }

    private <T> List<T> readDatas(long begin, long end, File file, Class<T> clazz) throws Exception {
        List<T> datas = new ArrayList<T>();

        BufferedRandomAccessFile bufferedRandomAccessFile = new BufferedRandomAccessFile(file, "r");
        bufferedRandomAccessFile.seek(begin);
        StringBuffer sb = new StringBuffer();
        while (begin <= end) {
            int read = bufferedRandomAccessFile.read();
            begin++;
            if (end_separator == read) {
                String line = new String(sb.toString().getBytes("ISO-8859-1"), charSet);
                datas.add(getObject(line, clazz));
                sb = new StringBuffer();
            } else {
                sb.append((char) read);
            }
        }
        bufferedRandomAccessFile.close();
        return datas;
    }

    /**
     * 反序列line成对象
     * 
     * @param line
     * @param clazz
     * @param <T>
     * @return
     */
    abstract <T> T getObject(String line, Class<T> clazz);
}
