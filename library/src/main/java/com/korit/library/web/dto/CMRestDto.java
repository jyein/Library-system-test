package com.korit.library.web.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CMRestDto<T> {

    @ApiModelProperty(value = "HTTP STATUS CODE", example = "200")
    private int code;
//    콘솔창에서 확인하지 않아도 응답 코드의 벨류가 출력된다

    @ApiModelProperty(value = "응답 메세지", example = "Successfully")
    private String message;
    private T data;
}
