package com.backend.doctor.mapper;

import java.util.List;
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
	
	
	// input : 7 parameters
	// output : X
	// @PostMapping("/sign-up") - 회원가입
	public int insertDoctorsSignUp(
			@Param("doctorId") String doctorId,
			@Param("password") String password,
			@Param("salt") String salt,
			@Param("name") String name,
			@Param("birthDate") String birthDate,
			@Param("email") String email,
			@Param("department") Integer department);
	
	
	// input : doctorId, password
	// output : Doctors(Map 형식) or null (단건)
	// @PostMapping("/sign-up") - 회원가입
	Map<String, Object> selectDoctorsByDoctorIdAndPassword(@Param("doctorId") String doctorId, 
			@Param("password") String password);
	
	
	// input : department
	// output : 부서별 의사 목록
	// @GetMapping("") : localhost/hospital - 부서별 의사 목록
	public List<Doctors> selectDoctorsByDepartment(@Param("department") Integer department);

	
	
}
