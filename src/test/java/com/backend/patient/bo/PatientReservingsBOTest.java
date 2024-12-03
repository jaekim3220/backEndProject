package com.backend.patient.bo;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

}
