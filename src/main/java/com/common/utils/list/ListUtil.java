package com.common.utils.list;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * 对象的排序,排序字段必须包含get方法
 * 
 * @author 宋思鹏(hzsongsipeng@corp.netease.com) 2017/7/12
 * @param <T>
 */
public class ListUtil<T> {

    /**
     * 判断list是否为空列表
     * 
     * @param list
     * @return
     */
    public static <T> boolean isEmptyList(List<T> list) {
        return list == null || list.size() == 0;
    }

    /**
     * XXX 此方法只区分了int long float double boolean，其他参数类型都直接使用toString之后再比较
     * 
     * @param list
     * @param field
     * @param sortDirection
     */
    public static <T> void sort(List<T> list, final String field, final SortDirection sortDirection) {
        Collections.sort(list, new Comparator<T>() {
            public int compare(T o1, T o2) {
                int ret = 0;
                String getMethod = parGetName(field);
                Class<Object>[] params = new Class[0];
                Object[] param = new Object[0];
                try {
                    Method m1 = o1.getClass().getMethod(getMethod, params);
                    m1.setAccessible(true);
                    Method m2 = o2.getClass().getMethod(getMethod, params);
                    m2.setAccessible(true);
                    Type returnType = m1.getReturnType();

                    Object m1Result = m1.invoke(o1, param);
                    Object m2Result = m1.invoke(o2, param);

                    if (sortDirection == SortDirection.ASC) {
                        if (m1Result == null && m2Result == null) {
                            return 0;
                        } else if (m1Result == null) {
                            return -1;
                        } else if (m2Result == null) {
                            return 1;
                        }

                        if (Integer.TYPE.equals(returnType) || Integer.class.equals(returnType)) {
                            ret = ((Integer) m1Result).compareTo((Integer) m2Result);
                        } else if (Long.TYPE.equals(returnType) || Long.class.equals(returnType)) {
                            ret = ((Long) m1Result).compareTo((Long) m2Result);
                        } else if (Float.TYPE.equals(returnType) || Float.class.equals(returnType)) {
                            ret = ((Float) m1Result).compareTo((Float) m2Result);
                        } else if (Double.TYPE.equals(returnType) || Double.class.equals(returnType)) {
                            ret = ((Double) m1Result).compareTo((Double) m2Result);
                        } else if (Boolean.TYPE.equals(returnType) || Boolean.class.equals(returnType)) {
                            ret = ((Boolean) m1Result).compareTo((Boolean) m2Result);
                        } else if (BigDecimal.class.equals(returnType)) {
                            ret = ((BigDecimal) m1Result).compareTo((BigDecimal) m2Result);
                        } else {
                            ret = m1Result.toString().compareTo(m2Result.toString());
                        }
                    } else if (sortDirection == SortDirection.DESC) {
                        //如果有结果为null的，直接返回
                        if (m1Result == null && m2Result == null) {
                            return 0;
                        } else if (m1Result == null) {
                            return 1;
                        } else if (m2Result == null) {
                            return -1;
                        }

                        if (Integer.TYPE.equals(returnType) || Integer.class.equals(returnType)) {
                            ret = ((Integer) m2Result).compareTo((Integer) m1Result);
                        } else if (Long.TYPE.equals(returnType) || Long.class.equals(returnType)) {
                            ret = ((Long) m2Result).compareTo((Long) m1Result);
                        } else if (Float.TYPE.equals(returnType) || Float.class.equals(returnType)) {
                            ret = ((Float) m2Result).compareTo((Float) m1Result);
                        } else if (Double.TYPE.equals(returnType) || Double.class.equals(returnType)) {
                            ret = ((Double) m2Result).compareTo((Double) m1Result);
                        } else if (Boolean.TYPE.equals(returnType) || Boolean.class.equals(returnType)) {
                            ret = ((Boolean) m2Result).compareTo((Boolean) m1Result);
                        } else if (BigDecimal.class.equals(returnType)) {
                            ret = ((BigDecimal) m2Result).compareTo((BigDecimal) m1Result);
                        } else {
                            ret = m2Result.toString().compareTo(m1Result.toString());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return ret;
            }
        });
    }

    /**
     * 对于List为null时返回空列表
     * 
     * @param listMayNull
     * @return
     */
    public static <T> List<T> trimToEmpty(List<T> listMayNull) {
        if (listMayNull == null) {
            return new ArrayList<T>();
        } else {
            return listMayNull;
        }
    }

    private static String parGetName(String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            return StringUtils.EMPTY;
        }

        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    public static <T> List<T> buildList(T... data) {
        if (data == null) {
            return null;
        }
        ArrayList<T> list = new ArrayList<T>();
        Collections.addAll(list, data);
        return list;
    }
}
