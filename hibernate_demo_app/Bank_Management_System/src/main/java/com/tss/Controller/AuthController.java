package com.tss.Controller;

import com.tss.Dto.AuthLoginRequestDto;
import com.tss.Dto.AuthRegisterRequestDto;
import com.tss.Dto.AuthResponseDto;
import com.tss.Service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody AuthRegisterRequestDto request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthLoginRequestDto request, HttpServletRequest httpRequest) {
        AuthResponseDto resp = authService.login(request);
        httpRequest.getSession(true).setAttribute("userId", resp.getUserId());
        httpRequest.getSession().setAttribute("role", resp.getRole());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        return ResponseEntity.noContent().build();
    }
}


