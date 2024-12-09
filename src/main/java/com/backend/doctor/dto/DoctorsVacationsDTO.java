package com.backend.doctor.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component // Bean 등록
public class DoctorsVacationsDTO {

    // 데이터 변환 메서드
    public List<Map<String, Object>> vacationsToFullCalendarFormat(List<Map<String, Object>> vacations) {
        // Stream을 이용해 데이터를 변환
    	// stream()은 Java 컬렉션(예: List, Set)이나 배열의 요소를 하나씩 처리하기 위한 파이프라인 생성 메서드
        return vacations.stream()
                .distinct() // 중복 제거
                .map(vacation -> { // 각각의 vacation 데이터를 변환
                    Map<String, Object> doctorEvent = new HashMap<>(); // FullCalendar의 event
                    doctorEvent .put("id", vacation.get("id")); // 일정 ID
                    doctorEvent .put("title", vacation.get("title")); // 일정 제목
                    doctorEvent .put("start", vacation.get("vacationStart")); // 시작 날짜
                    doctorEvent .put("end", vacation.get("vacationEnd")); // 종료 날짜
                    return doctorEvent ;
                }).collect(Collectors.toList()); // 변환한 데이터를 List로 반환
    }
}
