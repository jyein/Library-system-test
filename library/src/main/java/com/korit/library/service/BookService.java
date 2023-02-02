package com.korit.library.service;

import com.korit.library.entity.BookImage;
import com.korit.library.entity.BookMst;
import com.korit.library.entity.CategoryView;
import com.korit.library.exception.CustomValidationException;
import com.korit.library.repository.BookRepository;
import com.korit.library.web.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Service
public class BookService {

    @Value("${file.path}")
    private String filePath;
    // yml에 성정해둿던 경로를 여기로 가져오라는 의미

    public Map<String, Object> getBookAndImg(String bookCode) {
        Map<String, Object> result = new HashMap<>();
        result.put("bookMst", bookRepository.findBookByBookCode(bookCode));
        result.put("bookImage", bookRepository.findBookImageByBookCode(bookCode));

        return result;
    }

    @Autowired
    private BookRepository bookRepository;

    public int getBookTotalCount(SearchNumberListReqDto searchNumberListReqDto) {
        return bookRepository.getBookTotalCount(searchNumberListReqDto);
    }

    public List<BookMst> searchBook(SearchReqDto searchReqDto) {
        searchReqDto.setIndex();
        return bookRepository.searchBook(searchReqDto);
    }


    public List<CategoryView> getCategories() {
        return bookRepository.findAllCategory();
    }

    public void registerBook(BookReqDto bookReqDto) {
        duplicateBookCode(bookReqDto.getBookCode());
        bookRepository.saveBook(bookReqDto);
    }

    public void modifyBook(BookReqDto bookReqDto) {
        bookRepository.maintainUpdateBookByBookCode(bookReqDto);
    }

    public void maintainModifyBook(BookReqDto bookReqDto) {
        bookRepository.maintainUpdateBookByBookCode(bookReqDto);
    }

    public void removeBook(String bookCode) {
        bookRepository.deleteBook(bookCode);
    }

    public void removeBooks(DeleteBooksReqDto deleteBooksReqDto) {
        bookRepository.deleteBooks(deleteBooksReqDto.getBookIds());
    }

    private void duplicateBookCode(String bookCode) {
        BookMst bookMst = bookRepository.findBookByBookCode(bookCode);
        if (bookMst != null) { // null이 아니면 중복
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("bookCode", "이미 존재하는 도서코드 입니다.");

            throw new CustomValidationException(errorMap);
        }
    }

    public void registerBookImages(String bookCode, List<MultipartFile> files) {
        // (files.size() > 0) 0보다 크다는것은 파일이 하나이상 들어가있다는 소리이다.
        if (files.size() < 1) { // 하나도 없을경우 실행
            Map<String, String> errorMap = new HashMap<String, String>();
            errorMap.put("flies", "이미지를 선택하세요.");

            throw new CustomValidationException(errorMap);
        }

        List<BookImage> bookImages = new ArrayList<BookImage>();

        files.forEach(file -> {
            String originFileName = file.getOriginalFilename();
            String extension = originFileName.substring(originFileName.lastIndexOf("."));
            // originFileName 의 마지막 위치부터 .을 찾아서 끝까지 짤라라 ex) asd.png 에서 .png 이부분을 말한다
            String tempFileName = UUID.randomUUID().toString().replaceAll("-", "") + extension;

            Path uploadPath = Paths.get(filePath + "book/" + tempFileName); //java.nio.file
            // ()에 파일 경로까지를 객체에 담겟다.
            File f = new File(filePath + "book"); // java.io
            if (!f.exists()) { // 해당 경로가 없으면 트루
                f.mkdirs(); // 모든 경로를 다 생성
                // 경로가 없으면 경로를 만들어라
            }

            try {
                Files.write(uploadPath, file.getBytes()); // getBytes()를 쓸때는 try/catch를 사용해서 예외처리를 시켜줘야 한다.
                // 컴파일러에서 서버로 보낼때 text,byte 로 보낸다
            } catch (IOException e) { // IOException = 경로가 잘못됫을때 (파일 이름, 경로 등등)
                throw new RuntimeException(e);
            }

            BookImage bookImage = BookImage.builder()
                    .bookCode(bookCode)
                    .saveName(tempFileName)
                    .originName(originFileName)
                    .build();

            bookImages.add(bookImage);
        });

        bookRepository.registerBookImages(bookImages);
    }

    public List<BookImage> getBooks(String bookCode) {
        return bookRepository.findBookImageAll(bookCode);

//        if( == null) {
//            Map<String, String> errorMap = new HashMap<String, String>();
//            errorMap.put("bookImageDto", "존재하지 않는 도서코드 입니다.");
//            throw new CustomValidationException(errorMap);
//        }
    }

    public void removeBookImage(int imageId) {
        BookImage bookImage = bookRepository.findBookImageByImageId(imageId);

        if(bookImage == null) {
            Map<String, String> errorMap = new HashMap<String,String>();
            errorMap.put("bookImageDto", "존재하지 않는 이미지ID 입니다.");

            throw new CustomValidationException(errorMap);
        }

        if(bookRepository.deleteBookImage(imageId) > 0) {
            File file = new File(filePath + "book/" + bookImage.getSaveName());
            if(file.exists()) {
                file.delete();
            }
            log.info("파일 삭제 완료!");
        }
    }


}
