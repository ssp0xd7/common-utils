package com.common.utils.io;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import com.common.utils.TestBase;

/**
 * @author kevin(ssp0xd7 @ gmail.com) 12/04/2018
 */
public class NIOSingleTest extends TestBase {

    private final char end_separator = '\n';

    private AtomicLong count = new AtomicLong(0);

    @Test
    public void test() throws Exception {
        String filePath = "/Users/kevin/Temp/严选/201802/支付宝/20884212585612930156_201802_账务明细_2.csv";
        long t1 = System.currentTimeMillis();
        RandomAccessFile fis = new RandomAccessFile(new File(filePath), "r");
        FileChannel channel = fis.getChannel();
        long size = channel.size();
        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, size);

        StringBuffer sb = new StringBuffer();
        byte read;
        long current = 0;
        while (current < size) {
            read = mappedByteBuffer.get();
            if (end_separator == read) {
                sb = new StringBuffer();
                count.incrementAndGet();
            } else {
                sb.append((char) read);
            }
            current++;
        }
        channel.close();

        System.out.println("Total count: " + count.get());
        System.out.println("Total cost: " + (System.currentTimeMillis() - t1));
    }
}
