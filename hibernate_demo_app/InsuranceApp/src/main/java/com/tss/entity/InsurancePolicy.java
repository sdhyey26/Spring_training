package com.tss.entity;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "insurance_policies")
public class InsurancePolicy {

	
    public InsurancePolicy() {
		super();
	}

	public InsurancePolicy(Long id, String policyNumber, String holderName, LocalDate startDate, LocalDate endDate,
			double amount) {
		super();
		this.id = id;
		this.policyNumber = policyNumber;
		this.holderName = holderName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.amount = amount;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public String getHolderName() {
		return holderName;
	}

	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Column(unique = true, nullable = false)
    private String policyNumber;

    @Column
    private String holderName;

    @Column
    private LocalDate startDate;
    private LocalDate endDate;

    @Column
    private double amount;

    @PrePersist
    public void generatePolicyNumber() {
        this.policyNumber = "POL-" + UUID.randomUUID().toString().substring(0, 8);
    }

}
