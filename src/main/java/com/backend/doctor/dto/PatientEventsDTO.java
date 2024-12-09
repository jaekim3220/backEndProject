package com.backend.doctor.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.backend.patient.entity.ReserversEntity;

@Component // Bean 등록
public class PatientEventsDTO {

	
	// 데이터 변환 메서드
	public List<Map<String, Object>> patientsToFullCalendarFormat(List<ReserversEntity> customerEvents) {
        // Stream을 이용해 데이터를 변환
    	// stream()은 Java 컬렉션(예: List, Set)이나 배열의 요소를 하나씩 처리하기 위한 파이프라인 생성 메서드
        return customerEvents.stream()
                .distinct() // 중복 제거
                .map(customerEvent -> { // 각각의 patientEvents 데이터를 변환
                    Map<String, Object> patientEvent = new HashMap<>(); // FullCalendar의 event
                    patientEvent.put("id", customerEvent.getId()); // 예약
                    patientEvent.put("title", customerEvent.getTitle()); // 예약 제목
                    patientEvent.put("visitDate", customerEvent.getVisitDate()); // 내원 날짜
                    patientEvent.put("status", customerEvent.getStatus()); // 예약 상태
                    return patientEvent;
                }).collect(Collectors.toList()); // 변환한 데이터를 List로 반환
	}
}
