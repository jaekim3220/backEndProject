package com.backend.patient.bo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.backend.doctor.bo.DoctorsReservingsBO;
import com.backend.doctor.bo.PatientReserversBO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class PatientReservingsBOTest {

	@Autowired
	PatientReservingsBO patientReservingsBO; 
	@Transactional
	@Test
	void 환자예약TO의사() {
		int a = patientReservingsBO.addToReservings(1, 3, "백아무개", 
				"제목", "내용", "2024-02-23", null, "aaa");
		log.info("!!!{}!!!", a);
	}
	
	@Autowired
	ReserversBO reserversBO;
	@Transactional
	@Test
	/*public int addPatientReserve(int customerId, String customerLoginId, int doctorNum, String title, 
			String description, String visitDate, MultipartFile file)*/
	void 환자영역입력() {
		int a = reserversBO.addPatientReserve(1, "bbbbbb", 3, "제목", "내용", "2024-11-08", null);
		log.info("!!! {} !!!", a);
	}
	
	@Autowired
	PatientReserversBO patientReserversBO;
	@Transactional
	@Test
	/*
	public int updateReserversByCustomerIdDoctorId(
			@Param("customerId") int customerId,@Param("doctorId") int doctorId) {
		
		return patientReserversMapper.updateReserversByCustomerIdDoctorId(customerId, doctorId);
	} */
	void 환자영역업데이트() {
		int a = patientReserversBO.updateReserversByCustomerIdDoctorId(1, 1, 1, "예약확정");
		log.info("!!! {} !!!", a);
	}
	
	
	@Autowired
	DoctorsReservingsBO doctorsReservingsBO;
	@Transactional
	@Test
	void 의사영역업데이트() {
		int a = doctorsReservingsBO.updateReservingsByIdDoctorNumberDoctorId(1, 2, 3, "메모", "예약상태", "진료상태");
	    if (a > 0) {
	        log.info("!!! 업데이트 성공 : {} 업데이트됨 !!!", a);
	    } else {
	        log.warn("!!! 업데이트 실패 !!!");
	    }
		
	}

}
