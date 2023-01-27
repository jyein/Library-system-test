package com.korit.library.web.api;

import com.korit.library.security.PrincipalDetails;
import com.korit.library.service.RentalService;
import com.korit.library.web.dto.CMRestDto;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"도서 대여 API"})
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RentalApi {
    
    // @RequiredArgsConstructor 를 쓴뒤에 아래와같이 final을 쓰면 @Autowired가 된다
    private final RentalService rentalService;
    // final을 다는순간 상수가되서 필수적으로 초기화가 일어나야되는데 그러면 생성자에서
//     아래처럼 만들어줘야 한다.
//    public RentalApi(RentalService rentalService) {
//        this.rentalService = rentalService;
//    }
    // 하지만 @RequiredArgsConstructor 를 사용하면 final이 달린부분에 위의 메소드를 주입을 시켜준다
    // 이경우엔 무조건 final 을 써야한다.(이건 취향으로  @Autowired를 사용해도 된다
    // 그때엔 @RequiredArgsConstructor 를 지워서 써야함)

    
    /*
        /rental/{bookId}
        대여 요청 -> 대여 요청 날린 사용자의 대여가능 여부 확인
         -> 가능함(대여 가능 횟수 3권 미만일 때) -> 대여정보 추가 rental_mst(대여코드), rental_dtl 
         -> 불가능함(대여 가능 횟수 0이면) -> 예외처리
     */


    @PostMapping("/rental/{bookId}")
    public ResponseEntity<CMRestDto<?>> rental(@PathVariable int bookId,
                                               @AuthenticationPrincipal PrincipalDetails principalDetails) {
                                                    // 로그인이 되어있으면 principalDetails에 들어간다

        rentalService.rentalOne(principalDetails.getUser().getUserId(), bookId);
        return ResponseEntity.ok()
                .body(new CMRestDto<>(HttpStatus.OK.value(), "Successfuly", null));
    }

    @PutMapping("/rental/{bookId}")
    public ResponseEntity<CMRestDto<?>> rentalReturn(@PathVariable int bookId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        rentalService.returnBook(bookId);

        return ResponseEntity.ok()
                .body(new CMRestDto<>(HttpStatus.OK.value(), "Successfuly", null));
    }


}
