package com.tss.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tss.dto.InsurancePolicyRequestDto;
import com.tss.dto.InsurancePolicyResponseDto;
import com.tss.dto.InsurancePolicyResponsePage;
import com.tss.service.InsurancePolicyServiceImpl;

@RestController
@RequestMapping("/policies")
public class InsurancePolicyController {

    @Autowired
    private InsurancePolicyServiceImpl service;

    @PostMapping
    public InsurancePolicyResponseDto createPolicy(@RequestBody InsurancePolicyRequestDto dto) {
        return service.createPolicy(dto);
    }

    @GetMapping
    public InsurancePolicyResponsePage getAllPolicies(@RequestParam int page, @RequestParam int size) {
        return service.getAllPolicies(page, size);
    }

    @GetMapping("/{id}")
    public Optional<InsurancePolicyResponseDto> getPolicyById(@PathVariable Long id) {
        return service.getPolicyById(id);
    }

    @GetMapping("/search/holder")
    public List<InsurancePolicyResponseDto> searchByHolder(@RequestParam String name,
                                                           @RequestParam int page,
                                                           @RequestParam int size) {
        return service.searchByHolderName(name, page, size);
    }

    @GetMapping("/search/duration")
    public List<InsurancePolicyResponseDto> searchByDuration(@RequestParam int years,
                                                             @RequestParam int page,
                                                             @RequestParam int size) {
        return service.findByDurationLessThanYears(years, page, size);
    }

    @DeleteMapping("/{policyNumber}")
    public void deleteByPolicyNumber(@PathVariable String policyNumber) {
        service.deleteByPolicyNumber(policyNumber);
    }
}
