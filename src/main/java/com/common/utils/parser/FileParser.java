package com.common.utils.parser;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * 文件解析器（类外观模式）
 * <p>
 * 支持异步解析，支持阻塞获取全部记录
 * <p>
 * 不支持多线程对象复用
 * 
 * @author kevin(ssp0xd7 @ gmail.com) 01/03/2018
 */
public interface FileParser<T> {

    /**
     * 是否解析完成
     * 
     * @return
     */
    boolean isParseDone();

    /**
     * 是否消费到最后
     *
     * @return
     */
    boolean isEnd();

    /**
     * 尝试获取
     *
     * @return
     */
    T take();

    /**
     * 尝试获取，阻塞 waitSecond 秒
     *
     * @param waitSecond
     * @return
     */
    T take(long waitSecond);

    /**
     * 获取全部记录（阻塞）
     * 
     * @return
     */
    List<T> getAll();

    /**
     * 解析csv
     *
     * @param file
     * @param clazz
     * @param charSet
     * @param nameIndexMap
     */
    void parseCSV(final File file, final Class<T> clazz, final String charSet,
                  final HashMap<String, Integer> nameIndexMap);

    /**
     * 解析excel
     * <p>
     * 使用StreamReader方式{@see https://github.com/monitorjbl/excel-streaming-reader}
     *
     * @param file
     * @param clazz
     * @param nameIndexMap
     */
    void parseExcel(final File file, final Class<T> clazz, final int sheetNum,
                    final HashMap<String, Integer> nameIndexMap);
}
