package com.korit.library.web.api;

import com.korit.library.security.PrincipalDetails;
import com.korit.library.service.LikeService;
import com.korit.library.web.dto.CMRestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeApi {

    private final LikeService likeService;

    @PostMapping("/book/{bookId}/like")
    public ResponseEntity<CMRestDto<Boolean>> like(@PathVariable int bookId,
                                                   @AuthenticationPrincipal PrincipalDetails principalDetails) {

        likeService.like(bookId, principalDetails.getUser().getUserId());

        return ResponseEntity.ok()
                .body(new CMRestDto<>(HttpStatus.OK.value(), "Successfully", true));
    }

    @DeleteMapping("/book/{bookId}/like")
    public ResponseEntity<CMRestDto<Boolean>> disLike(@PathVariable int bookId,
                                                   @AuthenticationPrincipal PrincipalDetails principalDetails) {

        likeService.disLike(bookId, principalDetails.getUser().getUserId());

        return ResponseEntity.ok()
                .body(new CMRestDto<>(HttpStatus.OK.value(), "Successfully", true));
    }
}
