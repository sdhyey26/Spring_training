package com.tss.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tss.Dto.MailRequestDto;
import com.tss.Dto.MailResponseDto;
import com.tss.Service.MailService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/mail")
public class MailController {
    @Autowired
    private MailService mailService;

    @PostMapping("/send")
    public ResponseEntity<MailResponseDto> sendMail(@Valid @RequestBody MailRequestDto mailRequestDto) {
        MailResponseDto response = mailService.sendMail(mailRequestDto);
        return ResponseEntity.ok(response);
    }
}
