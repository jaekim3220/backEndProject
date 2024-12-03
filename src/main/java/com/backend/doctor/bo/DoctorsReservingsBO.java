package com.backend.doctor.bo;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import com.backend.doctor.domain.DoctorsReservings;
import com.backend.doctor.mapper.DoctorsMapper;
import com.backend.doctor.mapper.DoctorsReservingsMapper;

import lombok.RequiredArgsConstructor;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper) <--> DB영역 
*/

// Service(BO)영역

@Service // Spring Bean 등록
@RequiredArgsConstructor
public class DoctorsReservingsBO {

	// 생성자를 사용한 의존성 주입(DI)
	private final DoctorsReservingsMapper doctorsReservingsMapper;
	
	
	// input : 의사 고유 번호(doctors.id) => doctorId
	// output : List<Map<String, Object>>
	// @GetMapping("/today-plan-view")
	public List<DoctorsReservings> getReservingsByDoctorId(Integer doctorId) {
		return doctorsReservingsMapper.selectReservingsByDoctorId(doctorId);
	}
	
	
}
