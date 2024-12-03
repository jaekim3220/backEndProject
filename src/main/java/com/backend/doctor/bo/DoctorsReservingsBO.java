package com.backend.doctor.bo;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.doctor.domain.DoctorsReservings;
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
	
	
    // 페이징 정보 필드(limit) 
    // 화면에 보여줄 DoctorsReservings의 개수 설정
    private static final int VIEW_MAX_SIZE = 3; 
	
	
	// input : 의사 고유 번호(doctors.id) => doctorId
	// output : List<Map<String, Object>>
	// @GetMapping("/today-plan-view")
	public List<DoctorsReservings> getReservingsByDoctorId(int doctorId, Integer prevId, Integer nextId) {
		
		// 1) 다음버튼(nextId가 존재) : 작은것 3개 desc
		// 2) 이전버튼(prevId가 존재) : 큰 것 3개 asc =>
		// 3) 페이징 없음(nextId, prevId 모두 부재) : 최신 순서로 3개 desc
		
		// XML에서 하나의 쿼리로 만들기 위해 변수를 정제 - breakpoint
		Integer standardId = null; // 방향 기준 id(prev 또는 next) - prev, next 여부에 따라서 id 값이 다르게 할당됨
		String direction = null; // 페이지 변경 방향(prev 또는 next)
		
		if(prevId != null) { // 2) 이전버튼 - breakpoint
			standardId = prevId;
			direction = "prev";
			
			// 역순으로 생성 - breakpoint
			// 1) row 불러오기
			List<DoctorsReservings> reservingsList = doctorsReservingsMapper.selectReservingsByDoctorId(nextId, standardId, direction, VIEW_MAX_SIZE);
			// 2) 역순 정렬
			Collections.reverse(reservingsList); // 순서 뒤집고 저장
			
			return reservingsList;
		} else if(nextId != null) { // 1) 다음버튼
			standardId = nextId;
			direction = "next";
		}
		
		// 기본 동작 : 모든 데이터를 오름차순 정렬
		return doctorsReservingsMapper.selectReservingsByDoctorId(doctorId, standardId, direction, VIEW_MAX_SIZE);
	}
	
	
}
