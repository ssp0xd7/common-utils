package com.common.utils.parser;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.lang3.StringUtils;

/**
 * 文件解析类
 * 
 * @author kevin(ssp0xd7 @ gmail.com) 01/03/2018
 */
public abstract class AbstractFileParse<T> implements FileParser<T> {

    /**
     * 默认队列长
     */
    private int DEFAULT_CAPACITY = 10000;

    /**
     * 字段名-位置映射
     */
    private HashMap<String, Integer> nameIndexMap;

    /**
     * 结束标识
     */
    private AtomicBoolean END_MARK = new AtomicBoolean(false);

    /**
     * 内部队列
     */
    private BlockingQueue<T> datas = new LinkedBlockingQueue<>(DEFAULT_CAPACITY);

    @Override
    public boolean isParseDone() {
        return END_MARK.get();
    }

    @Override
    public boolean isEnd() {
        return isParseDone() && datas.size() == 0;
    }

    /**
     * 尝试获取，取不到返回空
     * 
     * @return
     */
    @Override
    public T take() {
        return datas.poll();
    }

    /**
     * 尝试获取，阻塞 waitSecond 秒
     * 
     * @param waitSecond
     * @return
     */
    @Override
    public T take(long waitSecond) {
        T take;
        try {
            take = datas.poll(waitSecond, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return null;
        }
        return take;
    }

    @Override
    public List<T> getAll() {
        List<T> result = new ArrayList<>();
        T data;
        while (!this.isEnd()) {
            if ((data = this.take()) != null) {
                result.add(data);
            }
        }
        return result;
    }

    /**
     * 设置解析完成
     */
    protected void setEnd() {
        END_MARK.set(true);
    }

    /**
     * 新增对象，子类可用
     * 
     * @param columns
     * @param clazz
     */
    protected void put(List<String> columns, Class<T> clazz) throws Exception {
        T t = getObject(columns, clazz);
        if (t != null) {
            datas.put(t);
        }
    }

    /**
     * 前置操作
     */
    protected void preHandle() {
        nameIndexMap = null;
        END_MARK.set(false);
        datas.clear();
    }

    /**
     * set Name Index Map
     * 
     * @param nameIndexMap
     */
    protected void setNameIndexMap(HashMap<String, Integer> nameIndexMap) {
        this.nameIndexMap = nameIndexMap;
    }

    /**
     * 逐行转对象
     *
     * @param columns
     * @return
     * @throws Exception
     */
    private T getObject(List<String> columns, Class<T> clazz) throws Exception {
        T t = clazz.newInstance();
        Field[] fs = clazz.getDeclaredFields();
        if (nameIndexMap == null) {
            return null;
        }

        for (Field f: fs) {
            ColumnName columnName = f.getAnnotation(ColumnName.class);
            if (columnName == null) {
                continue;
            }

            Integer index = nameIndexMap.get(columnName.name());
            if (index == null) {
                continue;
            }
            if (index >= columns.size()) {
                continue;
            }
            String column = columns.get(index);
            if (StringUtils.isBlank(column)) {
                continue;
            }
            //去除字符串中的回车、换行符、制表符、单引号、双引号
            String value = column;
            value = value.replaceAll("\t|\n|r|\'|\"|`", "");

            f.setAccessible(true); //设置些属性是可以访问的
            Class<?> type = f.getType();//得到此属性的类型

            if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
                f.set(t, Integer.valueOf(value));
            } else if (type.equals(Long.TYPE) || type.equals(Long.class)) {
                f.set(t, Long.valueOf(value));
            } else if (type.equals(Byte.TYPE) || type.equals(Byte.class)) {
                f.set(t, Byte.valueOf(value));
            } else if (type.equals(Short.TYPE) || type.equals(Short.class)) {
                f.set(t, Short.valueOf(value));
            } else if (type.equals(Character.TYPE) || type.equals(Character.class)) {
                // 取第一个char
                f.set(t, value.charAt(0));
            } else if (type.equals(Float.TYPE) || type.equals(Float.class)) {
                f.set(t, Float.valueOf(value));
            } else if (type.equals(Double.TYPE) || type.equals(Double.class)) {
                f.set(t, Double.valueOf(value));
            } else if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
                f.set(t, Boolean.valueOf(value));
            } else if (type.equals(BigDecimal.class)) {
                f.set(t, new BigDecimal(value));
            } else {
                // 当字符串处理
                f.set(t, value);
            }
        }
        return t;
    }
}
