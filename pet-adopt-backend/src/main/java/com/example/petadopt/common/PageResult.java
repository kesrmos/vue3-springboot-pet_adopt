package com.example.petadopt.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页返回结构：{total, records, page, size}
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总条数 */
    private long total;
    /** 当前页数据 */
    private List<T> records;
    /** 当前页码（从 1 开始） */
    private long page;
    /** 每页条数 */
    private long size;

    public PageResult() {
    }

    public PageResult(long total, List<T> records, long page, long size) {
        this.total = total;
        this.records = records == null ? new ArrayList<>() : records;
        this.page = page;
        this.size = size;
    }

    /** 由 MyBatis-Plus 分页对象 + 转换后的列表构建 */
    public static <T> PageResult<T> of(IPage<?> page, List<T> records) {
        return new PageResult<>(page.getTotal(), records, page.getCurrent(), page.getSize());
    }

    /** 由原始分页对象构建（元素类型相同） */
    public static <T> PageResult<T> of(IPage<T> page) {
        return new PageResult<>(page.getTotal(), page.getRecords(), page.getCurrent(), page.getSize());
    }
}
