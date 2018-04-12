package com.common.utils.io;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Test;

/**
 * @author kevin(ssp0xd7 @ gmail.com) 11/04/2018
 */
public class BufferedRandomAccessFileTest {

    private ExecutorService threads = new ThreadPoolExecutor(20, 30, 0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>(50));

    private final int THREAD_NUM = 10;

    private final char end_separator = '\n';

    private AtomicLong count = new AtomicLong(0);

    public void parse(final File file) throws Exception {
        BufferedRandomAccessFile randomAccessFile = new BufferedRandomAccessFile(file, "r", 1 << 12);
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
        final CountDownLatch latch = new CountDownLatch(THREAD_NUM);
        for (int n = 0; n < THREAD_NUM; n++) {
            final int finalN = n;
            threads.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        readDatas(beginIndexs[finalN], endIndexs[finalN], file);
                        latch.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        latch.await();
        randomAccessFile.close();
    }

    /**
     * 获取每行结束标识位
     *
     * @param beginIndex
     * @param randomAccessFile
     * @return
     * @throws IOException
     */
    private long getGapToEOF(long beginIndex, BufferedRandomAccessFile randomAccessFile) throws IOException {
        randomAccessFile.seek(beginIndex);
        long count = 0;
        while (randomAccessFile.read() != end_separator) {
            count++;
        }
        return ++count;
    }

    private void readDatas(long begin, long end, File file) throws Exception {
        BufferedRandomAccessFile randomAccessFile = new BufferedRandomAccessFile(file, "r", 1 << 12);
        randomAccessFile.seek(begin);
        StringBuffer sb = new StringBuffer();
        while (begin <= end) {
            int read = randomAccessFile.read();
            begin++;
            if (end_separator == read) {
                // TODO: 11/04/2018
                sb = new StringBuffer();
                count.incrementAndGet();
            } else {
                sb.append((char) read);
            }
        }
        randomAccessFile.close();
    }

    @Test
    public void test() throws Exception {
        String filePath = "/Users/kevin/Temp/严选/201802/支付宝/20884212585612930156_201802_账务明细_2.csv";
        long t1 = System.currentTimeMillis();
        parse(new File(filePath));
        System.out.println("Total count: " + count.get());
        System.out.println("Total cost: " + (System.currentTimeMillis() - t1));
    }
}
