package com.korit.library.service;

import com.korit.library.entity.RentalDtl;
import com.korit.library.entity.RentalMst;
import com.korit.library.exception.CustomRentalException;
import com.korit.library.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;


    public void rentalOne(int userId, int bookId) { // 누가 어떤책을 빌렷는가
        // 하나만 렌탈하는 경우
        availability(userId);
        // 같은책 대여하는 경우
        availabilityLoan(bookId);
        // 위의 두개를 통화해야 실행

        RentalMst rentalMst = RentalMst.builder()
                .userId(userId)
                .build();

        rentalRepository.saveRentalMst(rentalMst);

        List<RentalDtl> rentalDtlList = new ArrayList<>();
        RentalDtl rentalDtl = RentalDtl.builder()
                .rentalId(rentalMst.getRentalId())
                .bookId(bookId)
                .build();

        rentalDtlList.add(rentalDtl);

        rentalRepository.saveRentalDtl(rentalDtlList);
    }

    public void returnBook(int bookId) {
        notAvailabilityLoan(bookId);
        rentalRepository.updateReturnDate(bookId);
    }

    private void availability(int userId) {
        int rentalCount = rentalRepository.rentalAvailability(userId);
        if (rentalCount > 2) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("rentalCountError", "대여 횟수를 초과하엿습니다.");
            throw new CustomRentalException(errorMap);
        }
    }

    private void availabilityLoan(int bookId) {
        int loanCount = rentalRepository.loanRental(bookId);
        if (loanCount > 0) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("loanError", "현재 대여중인 도서입니다.");
            throw new CustomRentalException(errorMap);
        }
    }
    private void notAvailabilityLoan(int bookId) {
        int loanCount = rentalRepository.loanRental(bookId);
        if (loanCount < 1) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("loanError", "현재 대여중인 도서가 아입니다.");
            throw new CustomRentalException(errorMap);
        }
    }

}
