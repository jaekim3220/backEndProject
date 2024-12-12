package com.backend.patient.bo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

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
	
	
    /**
     * 예약 생성 화면 데이터 전송을 위한 DTO 클래스
     */
	@Getter
	@Setter
    @RequiredArgsConstructor
	// @GetMapping("/{id}/reserve-view")
    public class ReserveViewData {
        private final Doctors doctor;
        private final CustomerEntity patient;
    }
    /**
     * 의사 및 환자 정보를 포함한 예약 화면 데이터를 조회
     */
	// input : int doctorId, String customerLoginId
	// @GetMapping("/{id}/reserve-view")
	public ReserveViewData patientAndDoctor(int doctorId, String customerLoginId) {
		
		// 특정 의사(환자가 예약할 의사) 데이터 추출
        Doctors doctor = doctorsBO.getDoctorsById(doctorId);
        
        // 환자 데이터 추출
        CustomerEntity patient = customerBO.getCustomerEntityByCustomerId(customerLoginId);
        
        if (doctor == null || patient == null) {
            throw new IllegalArgumentException("데이터 불러오기 실패. doctorId 또는 customerLoginId를 확인.");
        }
        
        // 결과 데이터 객체 생성 및 반환
        return new ReserveViewData(doctor, patient);
	}
	
	
	/**
     * UUID ver4 기반 merchant_uid 생성 메서드
     */
	public String generateUUIDV4() {
		// version 4 UUID 생성
		String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
		UUID uuid = UUID.randomUUID();
		String uuidString = uuid.toString().replace("-", "");
		String uuid4 = date + "-" + uuidString;
		log.info("생성한 uudi : {}", uuid4);
		
		return uuid4;
	}
	
}
