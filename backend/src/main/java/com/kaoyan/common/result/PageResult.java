package com.kaoyan.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@ApiModel("统一分页结构")
public class PageResult<T> implements Serializable {

    @ApiModelProperty("总记录数")
    private Long total;

    @ApiModelProperty("当前页数据")
    private List<T> records;

    @ApiModelProperty("当前页码")
    private Long current;

    @ApiModelProperty("每页条数")
    private Long size;

    public PageResult() {
        this.total = 0L;
        this.records = Collections.emptyList();
        this.current = 1L;
        this.size = 10L;
    }

    public PageResult(Long total, List<T> records, Long current, Long size) {
        this.total = total;
        this.records = records;
        this.current = current;
        this.size = size;
    }

    public static <T> PageResult<T> of(IPage<T> page) {
        return new PageResult<>(page.getTotal(), page.getRecords(), page.getCurrent(), page.getSize());
    }

    public static <E, T> PageResult<T> of(IPage<E> page, Function<E, T> converter) {
        List<T> list = page.getRecords().stream().map(converter).collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), list, page.getCurrent(), page.getSize());
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>();
    }
}
