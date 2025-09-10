package com.tss.Service;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tss.Dto.Admin.AdminCardActionRequestDto;
import com.tss.Dto.Admin.AdminUpdateAccountRequestDto;
import com.tss.Dto.Admin.AdminUpdateUserRequestDto;
import com.tss.Entity.Account;
import com.tss.Entity.CardApplication;
import com.tss.Entity.Transaction;
import com.tss.Entity.User;
import com.tss.Exception.BadRequestException;
import com.tss.Exception.ResourceNotFoundException;
import com.tss.Repository.AccountRepository;
import com.tss.Repository.CardApplicationRepository;
import com.tss.Repository.TransactionRepository;
import com.tss.Repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final UserRepository userRepository;
	private final AccountRepository accountRepository;
	private final CardApplicationRepository cardApplicationRepository;
	private final TransactionRepository transactionRepository;

	public List<User> listUsers() {
		return userRepository.findAll();
	}

	public List<Account> listAccounts() {
		return accountRepository.findAll();
	}

	public List<CardApplication> listCardApplications() {
		return cardApplicationRepository.findAll();
	}

	public Page<Transaction> listTransactions(String accountNumber, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		if (accountNumber == null || accountNumber.isBlank()) {
			return transactionRepository.findAll(pageable);
		}
		return transactionRepository.findByFromAccountOrToAccount(accountNumber, accountNumber, pageable);
	}

	@Transactional
	public Account updateUser(AdminUpdateUserRequestDto request) {

		Account user = accountRepository.findById(request.getAccountnumber())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		if (user == null) {
			throw new BadRequestException("user doesnt exist with this id");
		} else {
			user.setName(request.getUsername());
			user.setEmail(request.getEmail());
			return accountRepository.save(user);
		}
	}

	@Transactional
	public void deleteUser(Long userId) {
		userRepository.deleteById(userId);
	}

	@Transactional
	public Account updateAccount(AdminUpdateAccountRequestDto request) {
		Account account = accountRepository.findById(request.getAccountnumber())
				.orElseThrow(() -> new ResourceNotFoundException("Account not found"));
		account.setEmail(request.getEmail());
		account.setMobile(request.getMobile());
		account.setName(request.getName());		
		return accountRepository.save(account);
	}

	@Transactional
	public void deleteAccount(String accountNumber) {
		if (!accountRepository.existsById(accountNumber)) {
			throw new ResourceNotFoundException("Account not found");
		}

		cardApplicationRepository.deleteByAccountNumberNative(accountNumber);
		System.out.println("Deleted all card applications for account " + accountNumber + " using native SQL");

		accountRepository.deleteById(accountNumber);
		System.out.println("Successfully deleted account " + accountNumber);
	}

	@Transactional
	public CardApplication cardAction(AdminCardActionRequestDto request) {
		CardApplication app = cardApplicationRepository.findById(request.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Card application not found"));
		String action = request.getAction();
		if ("approve".equalsIgnoreCase(action)) {
			app.setStatus("Approved");
			app.setApprovedAt(Instant.now());
		} else if ("reject".equalsIgnoreCase(action)) {
			app.setStatus("Rejected");
			app.setApprovedAt(null);
		} else {
			throw new BadRequestException("Invalid action");
		}
		return cardApplicationRepository.save(app);
	}
}
