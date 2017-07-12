/**
 * @(#)CryptoTests.java, 2017/7/12.
 * <p/>
 * Copyright 2017 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.common.utils.crypto;

import junit.framework.Assert;

import org.junit.Test;
import com.common.utils.TestBase;

/**
 * cryptoTest
 *
 * @author 宋思鹏(hzsongsipeng@corp.netease.com) 2017/7/12
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