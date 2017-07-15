package com.common.utils.list;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.common.utils.TestBase;

/**
 * list util test
 *
 * @author kevin(ssp0xd7@gmail.com) 2017/7/13
 */
public class ListUtilTest extends TestBase {

    @Test
    public void testEmpty() {
        List<String> list = null;
        Assert.assertTrue(ListUtil.isEmptyList(list));
        list = new ArrayList<String>();
        Assert.assertTrue(ListUtil.isEmptyList(list));
    }

    @Test
    public void testSort() {
        List<CompareParam> list = new ArrayList<CompareParam>() {
            {
                add(new CompareParam(3));
                add(new CompareParam(4));
            }
        };
        Assert.assertTrue(list.get(0).item < list.get(1).item);
        ListUtil.sort(list,"item",SortDirection.DESC);
        Assert.assertTrue(list.get(0).item > list.get(1).item);
    }

    private class CompareParam {

        private int item;

        public CompareParam(int value){
            this.item = value;
        }

        public int getItem() {
            return item;
        }

        public void setItem(int item) {
            this.item = item;
        }
    }
}
