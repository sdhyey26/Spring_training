package com.tss.Dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUpdateAccountRequestDto {
    private String accountnumber;
	private String name ;
    private String email;
    private String mobile;
}


