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
 * @author kevin(ssp0xd7@gmail.com) 2017/7/12
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
        list = trimToEmpty(list);
        Collections.sort(list, new Comparator<T>() {
            private int typeCompare(Type returnType, Object o1, Object o2) {
                if (Integer.TYPE.equals(returnType) || Integer.class.equals(returnType)) {
                    return ((Integer) o1).compareTo((Integer) o2);
                } else if (Long.TYPE.equals(returnType) || Long.class.equals(returnType)) {
                    return ((Long) o1).compareTo((Long) o2);
                } else if (Float.TYPE.equals(returnType) || Float.class.equals(returnType)) {
                    return ((Float) o1).compareTo((Float) o2);
                } else if (Double.TYPE.equals(returnType) || Double.class.equals(returnType)) {
                    return ((Double) o1).compareTo((Double) o2);
                } else if (Boolean.TYPE.equals(returnType) || Boolean.class.equals(returnType)) {
                    return ((Boolean) o1).compareTo((Boolean) o2);
                } else if (BigDecimal.class.equals(returnType)) {
                    return ((BigDecimal) o1).compareTo((BigDecimal) o2);
                } else {
                    return o1.toString().compareTo(o2.toString());
                }
            }

            public int compare(T o1, T o2) {
                int ret = 0;
                String getMethod = parGetName(field);
                Class[] params = new Class[0];
                Object[] param = new Object[0];
                try {
                    Method m1 = o1.getClass().getMethod(getMethod, params);
                    m1.setAccessible(true);
                    Method m2 = o2.getClass().getMethod(getMethod, params);
                    m2.setAccessible(true);
                    Type returnType = m1.getReturnType();

                    Object m1Result = m1.invoke(o1, param);
                    Object m2Result = m1.invoke(o2, param);

                    if (sortDirection == SortDirection.DESC) {
                        Object tmp = m1Result;
                        m1Result = m2Result;
                        m2Result = tmp;
                    }

                    if (m1Result == null && m2Result == null) {
                        return 0;
                    } else if (m1Result == null) {
                        return -1;
                    } else if (m2Result == null) {
                        return 1;
                    }
                    ret = typeCompare(returnType, m1Result, m2Result);
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
