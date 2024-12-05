package com.backend.doctor.bo;

import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class DoctorUpdateBO {

	
	// 생성자를 사용한 의존성 주입(DI)
	private final PatientReserversBO patientReserversBO;
	private final DoctorsReservingsBO doctorsReservingsBO;
	
	// 두 개의 업데이트 작업을 트랜잭션으로 처리
	// 메서드 실행 중에 하나라도 예외가 발생하면 트랜잭션 전체가 롤백
	@Transactional
	public int doctorUpdateBO(int id, int doctorId, int customerId, 
			String memo, String status, String treatment) {
		
		
		try {
			// 업데이트 1 : 환자의 예약 상태(reservers)
			int patientUpdateResult = patientReserversBO.updateReserversByCustomerIdDoctorId(id, customerId, doctorId, status);
			log.info("##### patient reservers update result : {} #####", patientUpdateResult);
			if(patientUpdateResult == 0) {
				throw new RuntimeException("Failed to update `reservers`");
			}
			
			// 업데이트 2 : 의사의 예약 상태(reservings)
			int doctorUpdateResult = doctorsReservingsBO.updateReservingsByIdDoctorNumberDoctorId(id, doctorId, customerId, memo, status, treatment);
			if(doctorUpdateResult == 0) {
				throw new RuntimeException("Failed to update `reservings`");
			}
			
			return 1;
		} catch (Exception e) {
			log.error("Transaction 실패 : {}", e.getMessage());
			throw e; // 트랜잭션 롤백을 위해 예외 다시 던짐
		}
		
	}
	
	
}
