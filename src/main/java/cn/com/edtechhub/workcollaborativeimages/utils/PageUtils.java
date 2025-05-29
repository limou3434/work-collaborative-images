package cn.com.edtechhub.workcollaborativeimages.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Collections;
import java.util.List;

/**
 * 分页工具类
 */
public class PageUtils {

    /**
     * 构造单个数据的分页
     */
    public static <T> Page<T> singlePage(T record) {
        Page<T> page = new Page<>();
        List<T> records = Collections.singletonList(record);
        page.setCurrent(1);
        page.setSize(records.size());
        page.setTotal(records.size());
        page.setRecords(records);
        return page;
    }
}
