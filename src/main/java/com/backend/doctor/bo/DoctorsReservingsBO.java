package com.backend.doctor.bo;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.backend.doctor.DoctorController;
import com.backend.doctor.domain.DoctorsReservings;
import com.backend.doctor.mapper.DoctorsReservingsMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper) <--> DB영역 
*/

// Service(BO)영역

@Slf4j
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
	public List<DoctorsReservings> getReservingsByDoctorId(int doctorId, String prevVisitDate, String nextVisitDate) {
		
		// 1) 다음버튼(nextId가 존재) : 작은것 3개 desc
		// 2) 이전버튼(prevId가 존재) : 큰 것 3개 asc =>
		// 3) 페이징 없음(nextId, prevId 모두 부재) : 최신 순서로 3개 desc
		
		// XML에서 하나의 쿼리로 만들기 위해 변수를 정제 - breakpoint
		String standardVisitDate = null; // 방향 기준 date(prev 또는 next) 초기 값 null - prev, next 여부에 따라서 id 값이 다르게 할당됨
		String direction = null; // 페이지 변경 방향(prev 또는 next) 초기 값 null - prev, next 여부에 따라서 (`prev` or `next`)
		
		if(prevVisitDate != null) { // 이전버튼 클릭 한 경우 - breakpoint
			standardVisitDate = prevVisitDate; // 화면에 보이는 글 목록의 첫 번째 날짜 데이터 값
			direction = "prev";
			
			// 역순으로 생성 - breakpoint
			// 1) row 불러오기
			List<DoctorsReservings> reservingsList = doctorsReservingsMapper.selectReservingsByDoctorId(doctorId, standardVisitDate, direction, VIEW_MAX_SIZE);
			log.info("##### reservingsList : {} #####", reservingsList);
			// 2) 역순 정렬
			Collections.reverse(reservingsList); // 순서 뒤집고 저장
			log.info("##### 역순 정렬한 reservingsList : {} #####", reservingsList);
			
			return reservingsList;
		} else if(nextVisitDate != null) { // 다음버튼 클릭 한 경우 - breakpoint
			standardVisitDate = nextVisitDate; // 화면에 보이는 글 목록의 마지막 날짜 데이터 값
			direction = "next";
		}
		
		// 기본 동작 : 모든 데이터를 오름차순 정렬
		return doctorsReservingsMapper.selectReservingsByDoctorId(doctorId, standardVisitDate, direction, VIEW_MAX_SIZE);
	}
	
	// `이전` 페이지의 마지막인가
	// 날짜가 가장 빠른(작은) 경우 첫 번째 ROW
	public boolean isPrevLastPageByVisitDate(int doctorId, String prevVisitDate) {
		String minVisitDate = doctorsReservingsMapper.selectReservingsdBydoctorNumberAsSort(doctorId, "ASC");
		return minVisitDate.equals(prevVisitDate); // 같으면 true 반환
	}
	// `다음` 페이지의 마지막인가
	// 날짜가 가장 빠른(작은) 경우 첫 번째 ROW
	public boolean isNextLastPageByVisitDate(int doctorId, String nextVisitDate) {
		String minVisitDate = doctorsReservingsMapper.selectReservingsdBydoctorNumberAsSort(doctorId, "DESC");
		return minVisitDate.equals(nextVisitDate); // 같으면 true 반환
	}
	
	
	// input : int id, Integer doctorNumber
	// output : DoctorsReservings or null(단건)
	// @GetMapping("/patient-status-view")
	public DoctorsReservings getReservingsByIdDoctorNumber(int id, Integer doctorNumber) {
		return doctorsReservingsMapper.selectByIdAndDoctorNumber(id, doctorNumber);
	}
	
}
