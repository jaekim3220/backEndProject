package com.backend.patient.bo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.backend.doctor.bo.DoctorsBO;
import com.backend.doctor.domain.Doctors;
import com.backend.patient.entity.CustomerEntity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper, XML) <--> DB영역 
*/

// Service(BO)영역

@Slf4j
@Service
@RequiredArgsConstructor
public class ReserversPayBO {

	
	// 생성자를 사용한 DI
	private final DoctorsBO doctorsBO;
	private final CustomerBO customerBO;
	
	@Value("${portone.api-url}")
    private String apiUrl;
	
	@Value("${portone.api-key}")
    private String apiKey; 
	
	@Value("${portone.api-secretk-key}")
    private String apiSecretKey;
	
	@Value("${portone.imp-key}")
	private String impKey;
	
	public String getImpKey() {
		return this.impKey;
	};
	
	
}
