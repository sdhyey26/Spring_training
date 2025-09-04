package com.tss.Service;

import com.tss.Dto.MailRequestDto;
import com.tss.Dto.MailResponseDto;

public interface MailService {
    MailResponseDto sendMail(MailRequestDto mailRequestDto);

}
