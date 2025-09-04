package com.tss.service;

import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tss.dto.InsurancePolicyRequestDto;
import com.tss.dto.InsurancePolicyResponseDto;
import com.tss.dto.InsurancePolicyResponsePage;
import com.tss.entity.InsurancePolicy;
import com.tss.repository.InsurancePolicyRepository;

@Service
public class InsurancePolicyServiceImpl implements InsurancePolicyService{

    @Autowired
    private InsurancePolicyRepository repo;

    public InsurancePolicyResponseDto createPolicy(InsurancePolicyRequestDto dto) {
        InsurancePolicy policy = new InsurancePolicy();
        policy.setHolderName(dto.getHolderName());
        policy.setStartDate(dto.getStartDate());
        policy.setEndDate(dto.getEndDate());
        policy.setAmount(dto.getAmount());

        InsurancePolicy saved = repo.save(policy);
        return toResponseDto(saved);
    }

    public InsurancePolicyResponsePage getAllPolicies(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<InsurancePolicy> page = repo.findAll(pageable);

        InsurancePolicyResponsePage response = new InsurancePolicyResponsePage();
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setSize(page.getSize());
        response.setLastPage(page.isLast());

        List<InsurancePolicyResponseDto> dtos = page.getContent()
                .stream().map(this::toResponseDto).collect(Collectors.toList());
        response.setContents(dtos);

        return response;
    }

    public Optional<InsurancePolicyResponseDto> getPolicyById(Long id) {
        return repo.findById(id).map(this::toResponseDto);
    }

    public List<InsurancePolicyResponseDto> searchByHolderName(String holderName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.findByHolderNameContainingIgnoreCase(holderName)
                .stream().map(this::toResponseDto).collect(Collectors.toList());
    }

    public List<InsurancePolicyResponseDto> findByDurationLessThanYears(int years, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.findAll(pageable).getContent().stream()
                .filter(policy -> Period.between(policy.getStartDate(), policy.getEndDate()).getYears() < years)
                .map(this::toResponseDto).collect(Collectors.toList());
    }

    public void deleteByPolicyNumber(String policyNumber) {
        repo.findByPolicyNumber(policyNumber).ifPresent(repo::delete);
    }

    private InsurancePolicyResponseDto toResponseDto(InsurancePolicy policy) {
        InsurancePolicyResponseDto dto = new InsurancePolicyResponseDto();
        dto.setPolicyNumber(policy.getPolicyNumber());
        dto.setHolderName(policy.getHolderName());
        dto.setStartDate(policy.getStartDate());
        dto.setEndDate(policy.getEndDate());
        dto.setAmount(policy.getAmount());
        return dto;
    }
}
