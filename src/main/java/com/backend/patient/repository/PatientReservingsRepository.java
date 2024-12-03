package com.backend.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.PutMapping;

import com.backend.patient.entity.PatientReservingsEntity;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper, XML) <--> DB영역 
*/

// Repository영역(Mapper, XML) 개념

public interface PatientReservingsRepository extends JpaRepository<PatientReservingsEntity, Integer> {

	
	// 메서드 생성
	
	// input : id(reservings.id), customerId(reservings.id) => `reservers`와 동일함
	// output : PatientReservingsEntity(단건) or Null
	// @PutMapping("/update")
	PatientReservingsEntity findByIdAndCustomerId(int id, int customerId);
	
	
}
