package com.backend.patient.bo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper) <--> DB영역 
*/

// Service(BO)영역


/*
의사의 진료 상황 화면 (`/doctor/patient-status-view`)에서 수정한 데이터를
의사의 `reservings`, 환자의 `reservers` 테이블로 update하기 위한 두 BO를 담는 상위 BO
*/


@Slf4j
@Service // Spring Bean 등록
@RequiredArgsConstructor // 생성자를 사용한 의존성 주입(DI)
public class PatientUpdateBO {
	
	private final PatientReservingsBO patientReservingsBO;
	private final ReserversBO reserversBO;
	
	// 두 개의 업데이트 작업을 트랜잭션으로 처리
	// 메서드 실행 중에 하나라도 예외가 발생하면 트랜잭션 전체가 롤백
	@Transactional
	public int patientUpdateBO(int id, Integer customerId, String customerLoginId, 
			String customerName, int doctorNum, String title, String description, 
			String visitDate, MultipartFile file) {
		
		try {
			// 환자 `reservers` 테이블
			reserversBO.updateByIdcustomerId(id, customerId, customerLoginId, doctorNum, title, description, visitDate, file);		
			
			// 의사 `reservings` 테이블
			patientReservingsBO.updateToReservings(id, customerId, customerLoginId, customerName, doctorNum, title, description, visitDate, file);
			
			return 1;
		} catch (Exception e) {
			log.error("!!!!! 오류 발생, 롤백 : {}", e.getMessage());
			throw e;
		}
	}
 	
}
