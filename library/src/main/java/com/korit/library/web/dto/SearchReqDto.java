package com.korit.library.web.dto;

import com.korit.library.aop.annotation.ValidAspect;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SearchReqDto {

    @ApiModelProperty(value = "검색어", required = false)
    private String searchValue;

    @ApiModelProperty(value = "카테고리", required = false, example = "소설")
    private String category;

    @ApiModelProperty(value = "정렬", required = false, example = "bookName")
    private String order;

    @NotBlank
    @ApiModelProperty(value = "조회전체 = N, 조회제한 = Y", required = true)
    private String limit;

    @ApiModelProperty(value = "페이지번호", required = false, example = "1")
    private int page;

    @ApiModelProperty(value = "표시할 개시글 개수", required = false, example = "20")
    private int count;

    @ApiModelProperty(hidden = true)
    private int index;

    public void setIndex() {
        index = (page -1) * count;
    }
}
