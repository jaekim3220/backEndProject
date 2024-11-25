package com.backend.patient.bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.backend.patient.entity.CustomerEntity;
import com.backend.patient.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper, XML) <--> DB영역 
*/

// Service(BO)영역

@Service
@RequiredArgsConstructor
public class CustomerBO {
	
	
	// 어노테이션(Annotation)
	@Autowired // DI(Dependency Injection) : 필드를 사용한 의존성 주입
	private CustomerRepository customerRepository;
	
	
	// input : customerId
	// output : CustomerEntity or null (단건)
	// @GetMapping("/is-duplicate-id") - 아이디 중복 확인
	public CustomerEntity getCustomerEntityByCustomerId(String customerId) {
		return customerRepository.findByCustomerId(customerId); // 메서드 생성
	}
	
	
}
