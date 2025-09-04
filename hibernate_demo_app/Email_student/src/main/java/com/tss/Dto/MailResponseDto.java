package com.tss.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MailResponseDto {

    private String status;
    private String message;
}
