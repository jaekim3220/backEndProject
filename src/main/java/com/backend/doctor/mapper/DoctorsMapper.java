package com.backend.doctor.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.backend.doctor.domain.Doctors;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper, XML) <--> DB영역 
*/

// Repository영역(Mapper, XML)

@Mapper // mybatis 라이브러리
public interface DoctorsMapper {

	
	// input : doctorId
	// output : Doctors or null (단건)
	// @GetMapping("/is-duplicate-id") - 아이디 중복 확인
	// XML에서 map으로 반환
	// Doctors 객체를 받으려면 resultType="com.backend.doctor.domain.Doctors"로 설정
	Map<String, Object> selectDoctorsByDoctorId(@Param("doctorId") String doctorId);
	
	
}