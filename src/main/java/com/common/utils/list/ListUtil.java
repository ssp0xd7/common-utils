package com.common.utils.list;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        return list == null || list.isEmpty();
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
        list.sort(new Comparator<T>() {
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

                try {
                    Object field1 = getField(o1, field);
                    Object field2 = getField(o2, field);

                    Class<?> type = getType(o1, field);

                    if (sortDirection == SortDirection.DESC) {
                        Object tmp = field1;
                        field1 = field2;
                        field2 = tmp;
                    }

                    if (field1 == null && field2 == null) {
                        return 0;
                    } else if (field1 == null) {
                        return -1;
                    } else if (field2 == null) {
                        return 1;
                    }
                    ret = typeCompare(type, field1, field2);
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
            return Collections.emptyList();
        } else {
            return listMayNull;
        }
    }

    private static Object getField(Object o, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field item = o.getClass().getDeclaredField(fieldName);
        item.setAccessible(true);
        return item.get(o);
    }

    private static Class<?> getType(Object o, String fieldName) throws NoSuchFieldException {
        Field item = o.getClass().getDeclaredField(fieldName);
        item.setAccessible(true);
        return item.getType();
    }

    public static <T> List<T> buildList(T... data) {
        if (data == null) {
            return Collections.emptyList();
        }
        ArrayList<T> list = new ArrayList<>();
        Collections.addAll(list, data);
        return list;
    }
}
