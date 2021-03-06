package com.common.utils.crypto;

import junit.framework.Assert;

import org.junit.Test;
import com.common.utils.TestBase;

/**
 * cryptoTest
 *
 * @author kevin(ssp0xd7@gmail.com) 2017/7/12
 */
public class CryptoTests extends TestBase{

    @Test
    public void testDES() throws Exception {
        String source = "Slippedthesurlybondsofearth,totouchthefaceofGod.";
        System.out.println("原文: " + source);
        String key = DESUtil.createRandomKey(source.length());
        System.out.println("key: " + key);
        String encryptData = DESUtil.encrypt(source, key);
        System.out.println("加密后base64: " + encryptData);
        String decryptData = DESUtil.decrypt(encryptData, key);
        System.out.println("解密后: " + decryptData);
        Assert.assertEquals(source,decryptData);
    }
}