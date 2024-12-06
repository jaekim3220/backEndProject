package com.backend.doctor.bo;

import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import com.backend.doctor.mapper.DoctorsVacationsMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper) <--> DB영역 
*/

// Service(BO)영역


/*
의사의 진료 상황 화면 (`/doctor/patient-status-view`)에서 수정한 데이터를
의사의 `reservings`, 환자의 `reservers` 테이블로 update하기 위한 두 BO를 담는 상위 BO
*/


@Slf4j
@Service // Spring Bean 등록
@RequiredArgsConstructor // 생성자를 사용한 의존성 주입(DI)
public class DoctorsVacationsBO {
	
	
	// 생성자를 사용한 의존성 주입(DI)
	private final DoctorsVacationsMapper doctorsVacationsMapper;
	
	
	// input : int doctorNum, String title, String vacationStart, String vacationEnd
	// output : X
	// @PostMapping("/calendar-plan-insert")
	public int addDoctorsVacations(int doctorNum, String title, 
			String vacationStart, String vacationEnd, String scheduleColor) {
		log.info("##### doctorNum : {}, title : {}, "
				+ "vacationStart : {}, vacationEnd : {} #####", doctorNum, title, vacationStart, vacationEnd);
		return doctorsVacationsMapper.insertDoctorsVacations(doctorNum, title, vacationStart, vacationEnd, scheduleColor);
	}

}
