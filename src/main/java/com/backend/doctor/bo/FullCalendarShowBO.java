package com.backend.doctor.bo;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.backend.doctor.dto.DoctorsVacationsDTO;
import com.backend.doctor.dto.PatientEventsDTO;
import com.backend.patient.bo.ReserversBO;
import com.backend.patient.entity.ReserversEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper) <--> DB영역 
*/

// Service(BO)영역


/*
의사의 일정(달력) 화면 (`/doctor/patient-status-view`)에서 수정한 데이터를
의사의 `reservings`, 환자의 `reservers` 테이블로 update하기 위한 두 BO를 담는 상위 BO
*/


@Slf4j
@Service // Spring Bean 등록
@RequiredArgsConstructor // 생성자를 사용한 의존성 주입(DI)
public class FullCalendarShowBO {
	
	
	// 의존성 주입
	private final DoctorsVacationsBO doctorsVacationsBO;
	private final ReserversBO reserversBO;
	private final DoctorsVacationsDTO doctorsVacationsDTO;
	private final PatientEventsDTO patientEventsDTO;
	

	// DoctorRestController - @PostMapping("/calendar-plan-show")
	// FullCalendar에 의사, 환자의 일정을 보여주기위해
	// Map 형식으로 가공한 데이터
	// 하나라도 load 실패 시 rollback
	public List<Map<String, Object>> doctorsAndPatientsSchedule(int doctorNum) {
		
        try {
            // `vacations` DB SELECT - breakpoint
        	List<Map<String, Object>> vacations = doctorsVacationsBO.getDoctorVacationsByDoctorNum(doctorNum);
            if (vacations == null || vacations.isEmpty()) {
                throw new IllegalStateException("의사 일정 데이터 로드 실패");
            }
            log.info("의사 일정 데이터 로드 성공: {}", vacations);

            
            // `reservers` DB SELECT - breakpoint
            List<ReserversEntity> patientEvents = reserversBO.getReservationsByDoctorNum(doctorNum);
            if (patientEvents == null || patientEvents.isEmpty()) {
                throw new IllegalStateException("환자 예약 데이터 로드 실패");
            }
            log.info("환자 예약 데이터 로드 성공: {}", patientEvents);

            // 데이터 가공
            List<Map<String, Object>> formattedVacations = doctorsVacationsDTO.vacationsToFullCalendarFormat(vacations);
            List<Map<String, Object>> formattedPatientEvents = patientEventsDTO.patientsToFullCalendarFormat(patientEvents);

            // 두 리스트 합치기
            formattedVacations.addAll(formattedPatientEvents);

            return formattedVacations;

        } catch (Exception e) {
            log.error("일정 처리 중 오류 발생", e);
            throw e; // 롤백을 위해 예외 재발생
        }
    }
	
}
