package com.common.utils.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Test;
import com.common.utils.TestBase;

/**
 * @author kevin(ssp0xd7 @ gmail.com) 11/04/2018
 */
public class BufferedSingleRead extends TestBase{

    private AtomicLong count = new AtomicLong(0);

    @Test
    public void test() throws Exception {
        String filePath = "/Users/kevin/Temp/严选/201802/支付宝/20884212585612930156_201802_账务明细_2.csv";
        long t1 = System.currentTimeMillis();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(new File(filePath))));
        String line;
        while((line = reader.readLine())!=null){
            count.incrementAndGet();
        }

        System.out.println("Total count: " + count.get());
        System.out.println("Total cost: " + (System.currentTimeMillis() - t1));
    }
}
