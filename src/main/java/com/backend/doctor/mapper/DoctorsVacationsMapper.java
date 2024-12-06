package com.backend.doctor.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper, XML) <--> DB영역 
*/

// Repository영역(Mapper, XML)

@Mapper // mybatis 라이브러리
public interface DoctorsVacationsMapper {

	
	// input : int doctorNum, String title, String vacationStart, String vacationEnd
	// output : X
	// @PostMapping("/calendar-plan-insert")
	public int insertDoctorsVacations(@Param("doctorNum") int doctorNum, 
			@Param("title") String title, @Param("vacationStart") String vacationStart, 
			@Param("vacationEnd") String vacationEnd);
	
	
}
