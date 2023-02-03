package com.korit.library.web.api;

import com.korit.library.web.dto.CMRestDto;
import com.korit.library.web.dto.SearchBookReqDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SearchApi {

    @GetMapping("/search")
    public ResponseEntity<CMRestDto<?>> search(SearchBookReqDto searchBookReqDto) {


        return ResponseEntity.ok()
                .body(new CMRestDto<>(HttpStatus.OK.value(), "Successfully", null));
    }

}
