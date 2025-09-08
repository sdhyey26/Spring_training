package com.tss.Controller;

import com.tss.Dto.Admin.AdminCardActionRequestDto;
import com.tss.Dto.Admin.AdminUpdateAccountRequestDto;
import com.tss.Dto.Admin.AdminUpdateUserRequestDto;
import com.tss.Entity.Account;
import com.tss.Entity.CardApplication;
import com.tss.Entity.User;
import com.tss.Service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> listUsers() {
        return ResponseEntity.ok(adminService.listUsers());
    }

    @PostMapping("/users/update")
    public ResponseEntity<User> updateUser(@RequestBody AdminUpdateUserRequestDto request) {
        return ResponseEntity.ok(adminService.updateUser(request));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> listAccounts() {
        return ResponseEntity.ok(adminService.listAccounts());
    }

    @PostMapping("/accounts/update")
    public ResponseEntity<Account> updateAccount(@RequestBody AdminUpdateAccountRequestDto request) {
        return ResponseEntity.ok(adminService.updateAccount(request));
    }

    @DeleteMapping("/accounts/{accountNumber}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String accountNumber) {
        adminService.deleteAccount(accountNumber);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cards/action")
    public ResponseEntity<CardApplication> cardAction(@RequestBody AdminCardActionRequestDto request) {
        return ResponseEntity.ok(adminService.cardAction(request));
    }
}


