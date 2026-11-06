package com.kaoyan.common.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@ApiModel("分页查询参数")
public class PageQuery {

    @ApiModelProperty(value = "页码，从 1 开始", example = "1")
    @Min(value = 1, message = "页码最小为 1")
    private Long current = 1L;

    @ApiModelProperty(value = "每页条数", example = "10")
    @Min(value = 1, message = "每页条数最小为 1")
    @Max(value = 200, message = "每页条数最大为 200")
    private Long size = 10L;
}
