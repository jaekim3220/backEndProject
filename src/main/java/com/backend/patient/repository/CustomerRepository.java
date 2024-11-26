package com.backend.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.patient.entity.CustomerEntity;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper, XML) <--> DB영역 
*/

// Repository영역(Mapper, XML) 개념

public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer> {

	// 메서드 생성
	
	// @GetMapping("/is-duplicate-id") - 아이디 중복 확인
	public CustomerEntity findByCustomerId(String customerId); // 단건은 Optional. List는 Optional 불필요
	
	
	// @PostMapping("/sign-in")
	public CustomerEntity findByCustomerIdAndPassword(String customerId, String password); // Correct method name
}
