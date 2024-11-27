package com.backend.doctor.bo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.backend.doctor.domain.Doctors;
import com.backend.doctor.mapper.DoctorsMapper;

import lombok.RequiredArgsConstructor;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper) <--> DB영역 
*/

// Service(BO)영역

@Service // Spring Bean 등록
@RequiredArgsConstructor
public class DoctorsBO {


	// 생성자를 사용한 의존성 주입
	private final DoctorsMapper doctorsMapper;
	
	
	// input : doctorId
	// output : Doctors or null (단건)
	// @GetMapping("/is-duplicate-id") - 아이디 중복 확인
    public Map<String, Object> getDoctorsByDoctorId(String doctorId) {
    	
    	Map<String, Object> doctors = doctorsMapper.selectDoctorsByDoctorId(doctorId);
    	
    	// null 방지
    	return doctors != null ? doctors : new HashMap<>(); // null 방지 처리
    }
	
	
}