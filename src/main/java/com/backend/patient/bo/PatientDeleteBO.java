package com.backend.patient.bo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.common.FileManagerService;
import com.backend.patient.repository.PatientReservingsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper, XML) <--> DB영역 
*/

// Service(BO)영역

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientDeleteBO {
	
	
	private final PatientReservingsBO patientReservingsBO;
	private final ReserversBO reserversBO;

	
	// 두 개의 업데이트 작업을 트랜잭션으로 처리
	// 메서드 실행 중에 하나라도 예외가 발생하면 트랜잭션 전체가 롤백
	@Transactional
	public int patientDeleteBO(int id, int customerId) {
		
		try {
			// 환자 `reservers` 테이블
			reserversBO.deleteReserveByIdCustomerId(id, customerId);
			
			// 의사 `reservings` 테이블
			patientReservingsBO.deleteReservingsByIdCustomerId(id, customerId);
			
			return 1;
		} catch (Exception e) {
			log.error("!!!!! 오류 발생, 롤백 : {}", e.getMessage());
			throw e;
		}
		
	}
	
}
