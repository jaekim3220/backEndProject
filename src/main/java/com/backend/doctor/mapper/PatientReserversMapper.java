package com.backend.doctor.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper, XML) <--> DB영역 
*/

// Repository영역(Mapper, XML)

@Mapper // mybatis 라이브러리
public interface PatientReserversMapper {

	
	// input : customerId, doctorId, status
	// output : int
	// @PostMapping("/statusUpdate")
	public int updateReserversByIdCustomerIdDoctorId(
			@Param("id") int id, @Param("customerId") int customerId, 
			@Param("doctorId") int doctorId, @Param("status") String status);
	
	
	// input : int id, String status
	// output : X
	// @PostMapping("/calendar-patient-update")
	public int updateReserversByIdAndStatus(@Param("id") int id, @Param("status") String status);
	
}
