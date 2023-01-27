package com.korit.library.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RentalMst {
    private int rentalId;
    private int userId;
    private LocalDate rentalDate;
    private LocalDate fixedDate;
}

// entity를 dto로 dto를 entity로 바꾸는것이 service 부분이다