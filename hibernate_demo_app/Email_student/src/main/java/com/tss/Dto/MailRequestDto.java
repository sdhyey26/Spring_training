package com.tss.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class MailRequestDto {

    @Email
    @NotBlank
    private String receiver;

    @NotBlank
    private String subject;

    @NotBlank
    private String content;
}
