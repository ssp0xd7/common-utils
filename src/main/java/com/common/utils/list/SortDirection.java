package com.common.utils.list;

/**
 * 排序方式
 *
 * @author kevin(ssp0xd7@gmail.com) 2017/7/12
 */
public enum SortDirection {
    /**
     * 从大到小排序
     */
    DESC(0), /**
     * 从小到大排序
     */
    ASC(1);

    private int code;

    private SortDirection(int code) {
        this.setCode(code);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static SortDirection getSortDirection(int sortDirection) {
        switch (sortDirection) {
            case 0:
                return DESC;
            case 1:
                return ASC;
            default:
                return DESC;
        }
    }
}
