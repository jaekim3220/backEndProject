package com.backend.doctor.bo;

import org.springframework.stereotype.Service;

import com.backend.doctor.mapper.PatientReserversMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper) <--> DB영역 
*/

// Service(BO)영역

@Slf4j
@Service // Spring Bean 등록
@RequiredArgsConstructor
public class PatientReserversBO {

	
	// 생성자를 사용한 의존성 주입(DI)
	private final PatientReserversMapper patientReserversMapper;
	
	
	// input : customerId, doctorId
	// output : int
	// @PostMapping("/statusUpdate")
	public int updateReserversByCustomerIdDoctorId(
			int id, int customerId, int doctorId, String status) {
		log.info("##### id={}, customerId={}, doctorId={}, status={} #####", id, customerId, doctorId, status);
		return patientReserversMapper.updateReserversByIdCustomerIdDoctorId(id, customerId, doctorId, status);
	}

	
	// input : int id, String status
	// output : X
	// @PostMapping("/calendar-patient-update")
	public int updateReserversByIdStatus(
			int id, String status) {
		log.info("##### id={}, status={} #####", id, status);
		return patientReserversMapper.updateReserversByIdAndStatus(id, status);
	}
	
	
}
