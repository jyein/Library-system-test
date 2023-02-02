package com.korit.library.web.api.admin;

import com.korit.library.aop.annotation.ParamsAspect;
import com.korit.library.aop.annotation.ValidAspect;
import com.korit.library.entity.BookImage;
import com.korit.library.entity.BookMst;
import com.korit.library.entity.CategoryView;
import com.korit.library.service.BookService;
import com.korit.library.web.dto.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = {"관리자 도서관리 API"})
@RequestMapping("/api/admin")
@RestController
//@CrossOrigin(origins = "http://172.30.64.1:5500")
public class BookApi {

    @Autowired
    private BookService bookService;

    @GetMapping("/book/{bookCode}")
    public ResponseEntity<CMRestDto<Map<String, Object>>> getBook(@PathVariable String bookCode) {

        return ResponseEntity.ok()
                .body(new CMRestDto<>(HttpStatus.OK.value(), "Successfully", bookService.getBookAndImg(bookCode)));
    }

    @ParamsAspect
    @ValidAspect
    @GetMapping("/books")
    public ResponseEntity<CMRestDto<List<BookMst>>> searchBook(@Valid SearchReqDto searchReqDto, BindingResult bindingResult) {
        return ResponseEntity.ok()
                .body(new CMRestDto<>(HttpStatus.OK.value(), "Successfully", bookService.searchBook(searchReqDto)));
    }

    @GetMapping("/books/totalcount")
    public ResponseEntity<CMRestDto<?>> getBookTotalCount(SearchNumberListReqDto searchNumberListReqDto) {
        return ResponseEntity.ok()
                .body(new CMRestDto<>(HttpStatus.OK.value(), "Successfully", bookService.getBookTotalCount(searchNumberListReqDto)));
    }

    @GetMapping("/categories")
    public ResponseEntity<CMRestDto<List<CategoryView>>> getCategories() {
        return ResponseEntity.ok()
                .body(new CMRestDto<>(HttpStatus.OK.value(), "Successfully", bookService.getCategories()));
    }

    @ParamsAspect
    @ValidAspect
    @PostMapping("/book")
    public ResponseEntity<CMRestDto<?>> registerBook(@Valid @RequestBody BookReqDto bookReqDto, BindingResult bindingResult) {
        // validation체크를 할려면 bindingResult를 꼭 적어야 한다

        bookService.registerBook(bookReqDto);

        return ResponseEntity.created(null)
                .body(new CMRestDto<>(HttpStatus.CREATED.value(), "Successfully", true));
    }


    // Put은 아무것도 입력하지 않으면 null이 들어가고
    // Path는 아무것도 입력하지않으면 그대로 유지된다 (공백이라면 공백이 들어감)
    // path에서 아무것도 입력하지 않으면 그전에 값을 유지
    // put에선 null
    @ParamsAspect
    @ValidAspect
    @PutMapping("/book/{bookCode}")
    public ResponseEntity<CMRestDto<?>> modifyBook(@PathVariable String bookCode, @Valid @RequestBody BookReqDto bookReqDto, BindingResult bindingResult) {
        bookService.modifyBook(bookReqDto);

        return ResponseEntity.ok()
                .body(new CMRestDto<>(HttpStatus.OK.value(), "Successfully", true));
    }

    @ParamsAspect
    @ValidAspect
    @PatchMapping("/book/{bookCode}")
    public ResponseEntity<CMRestDto<?>> maintainModifyBook(@PathVariable String bookCode, @Valid @RequestBody BookReqDto bookReqDto, BindingResult bindingResult) {
        bookService.maintainModifyBook(bookReqDto);

        return ResponseEntity.ok()
                .body(new CMRestDto<>(HttpStatus.OK.value(), "Successfully", true));
    }

    @ParamsAspect
    @DeleteMapping("/book/{bookCode}")
    public ResponseEntity<CMRestDto<?>> removeBook(@PathVariable String bookCode) {
        bookService.removeBook(bookCode);

        return ResponseEntity.ok()
                .body(new CMRestDto<>(HttpStatus.OK.value(), "Successfully", true));
    }


    @ParamsAspect
    @DeleteMapping("/books")
    public ResponseEntity<CMRestDto<?>> removeBooks(@RequestBody DeleteBooksReqDto deleteBooksReqDto) {
        bookService.removeBooks(deleteBooksReqDto);

        return ResponseEntity.ok()
                .body(new CMRestDto<>(HttpStatus.OK.value(), "Successfully", true));
    }


    @ParamsAspect
    @PostMapping("/book/{bookCode}/images")
    // 파일 객체를 가지고온다
    public ResponseEntity<CMRestDto<?>> registerBookImg(@PathVariable String bookCode, @ApiParam(required = false) @RequestPart List<MultipartFile> files) {
        bookService.registerBookImages(bookCode, files);

        return ResponseEntity.ok()
                .body(new CMRestDto<>(HttpStatus.OK.value(), "Successfully", true));
    }

    @ParamsAspect
    @PostMapping("/book/{bookCode}/images/modification")
    // 파일 객체를 가지고온다
    public ResponseEntity<CMRestDto<?>> modifyBookImg(@PathVariable String bookCode, @ApiParam(required = false) @RequestPart List<MultipartFile> files) {
        bookService.registerBookImages(bookCode, files);

        return ResponseEntity.ok()
                .body(new CMRestDto<>(HttpStatus.OK.value(), "Successfully", true));
    }
    // 이거는 delete랑 register를 합쳐둔 기능으로 설계해보자

    @ParamsAspect
    @GetMapping("/book/{bookCode}/images")
    public ResponseEntity<CMRestDto<List<BookImage>>> getImages(@PathVariable String bookCode) {
        List<BookImage> bookImages = bookService.getBooks(bookCode);

        return ResponseEntity.ok()
                .body(new CMRestDto<>(HttpStatus.OK.value(), "Successfully", bookImages));
    }

    @DeleteMapping("/book/{bookCode}/image/{imageId}")
    public ResponseEntity<CMRestDto<?>> removeBookImg(@PathVariable String bookCode, @PathVariable int imageId) {

        bookService.removeBookImage(imageId);

        return ResponseEntity.ok()
                .body(new CMRestDto<>(HttpStatus.OK.value(), "Successfully", null));
    }

}
