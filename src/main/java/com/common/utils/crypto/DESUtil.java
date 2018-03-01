package com.common.utils.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import java.security.Key;
import java.security.SecureRandom;
import org.apache.commons.codec.binary.Base64;

/**
 * des对称加解密工具
 *
 * @author kevin(ssp0xd7@gmail.com) 2017/7/12
 */
public class DESUtil {

    /**
     * 算法名称
     */
    private static final String KEY_ALGORITHM = "DES";

    /**
     * 算法名称/加密模式/填充方式
     * <p/>
     * DES共有四种工作模式-->>ECB：电子密码本模式、CBC：加密分组链接模式、CFB：加密反馈模式、OFB：输出反馈模式
     * <p/>
     * 常用填充方式：NoPadding（不填充）、Zeros填充（0填充）、PKCS5Padding填充
     */
    private static final String CIPHER_ALGORITHM = "DES/ECB/NoPadding";

    /**
     * 生成密钥key对象
     * 
     * @param keyStr
     *            密钥字符串
     * @return 密钥对象
     * @throws Exception
     */
    private static SecretKey keyGenerator(String keyStr) throws Exception {
        byte[] input = HexString2Bytes(keyStr);
        DESKeySpec desKey = new DESKeySpec(input);
        //创建一个密匙工厂，然后用它把DESKeySpec转换成
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generateSecret(desKey);
    }

    /**
     * 取低四位
     * 
     * @param c
     * @return
     */
    private static int parse(char c) {
        // TODO: 2017/7/12 校验是否为十六进制字符 
        if (c >= 'a')
            return (c - 'a' + 10) & 0x0f;
        if (c >= 'A')
            return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;
    }

    /**
     * 从十六进制字符串转换到字节数组
     * 
     * @param hexstr
     * @return
     */
    private static byte[] HexString2Bytes(String hexstr) {
        byte[] b = new byte[hexstr.length() / 2];
        int j = 0;
        for (int i = 0; i < b.length; i++) {
            char c0 = hexstr.charAt(j++);
            char c1 = hexstr.charAt(j++);
            b[i] = (byte) ((parse(c0) << 4) | parse(c1));
        }
        return b;
    }

    /**
     * 加密数据
     * 
     * @param data
     *            待加密数据
     * @param key
     *            密钥（约定为十六进制格式）
     * @return 加密后的数据
     */
    public static String encrypt(String data, String key) throws Exception {
        Key deskey = keyGenerator(key);
        // 实例化Cipher对象，它用于完成实际的加密操作
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        SecureRandom random = new SecureRandom();
        // 初始化Cipher对象，设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, deskey, random);
        // 执行加密操作。加密后的结果通常都会用Base64编码进行传输
        return Base64.encodeBase64String(cipher.doFinal(data.getBytes()));
    }

    /**
     * 解密数据
     * 
     * @param data
     *            待解密数据
     * @param key
     *            密钥
     * @return 解密后的数据
     */
    public static String decrypt(String data, String key) throws Exception {
        Key deskey = keyGenerator(key);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        //初始化Cipher对象，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, deskey);
        // 执行解密操作
        return new String(cipher.doFinal(Base64.decodeBase64(data)));
    }

    /**
     * 根据加密原文随机生成十六进制key
     * 
     * @param sourceLength
     * @return
     */
    public static String createRandomKey(int sourceLength) {
        if (sourceLength < 0) {
            return "";
        } else {
            SecureRandom r = new SecureRandom();
            r.setSeed(System.currentTimeMillis());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < sourceLength; ++i) {
                sb.append("abcdefABCDEF0123456789".charAt(r.nextInt("abcdeABCDEF0123456789".length())));
                sb.append("abcdefABCDEF0123456789".charAt(r.nextInt("abcdeABCDEF0123456789".length())));
            }

            return sb.toString();
        }
    }


}
