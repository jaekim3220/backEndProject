package com.backend.doctor;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.backend.doctor.bo.DoctorsReservingsBO;
import com.backend.doctor.domain.DoctorsReservings;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper) <--> DB영역 
*/

/*
<Response 방법 - 서버 기준>
@Controller + return String => ViewResolver => HTML 파일 렌더링(Model) => HTML
@Controller + @ResponseBody return String => HTTPMessageConverter => HTML
@Controller + @ResponseBody return 객체(map, list) => HTTPMessageConverter => jackson => JSON
@RestController return map => JSON
Model은 HTML일 경우 사용(@ResponseBody일 경우 Model 사용 불가)
*/

@Slf4j
@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorController {

	
	// 생성자를 사용한 의존성 주입(DI)
	private final DoctorsReservingsBO doctorsReservingsBO;
	
	
	// 의사 회원가입 화면
	@GetMapping("/sign-up-view")
	// http:localhost/doctor/sign-up-view
	public String signUpView() {
		return "doctor/doctorSignUp";
	}
	
	
	// 환자(고객) 로그인 화면
	@GetMapping("/sign-in-view")
	// http:localhost/doctor/sign-in-view
	public String signInView() {
		return "doctor/doctorSignIn";
	}
		
	
	// 의사 `예약 현황 화면`
	// /doctor/{doctors.id}/today-plan-view
	@GetMapping("/today-plan-view")
	// localhost/doctor/today-plan-view
	public String todayPlanView(
			// 비필수 파라미터 불러오기2 : 기본값 설정 value, required 입력 (추천) - URL에서 추출
			@RequestParam(value = "prevVisitDate", required = false) String prevVisitDateParam,
			@RequestParam(value = "nextVisitDate", required = false) String nextVisitDateParam,
            Model model, HttpSession session) {
		
		
		// 로그인 여부 검사 - breakpoint
		// session에 담을 변수(parameter)가 기억나지 않을 경우 RestController - @PostMapping("/sign-in") 참고
		Integer doctorId = (Integer) session.getAttribute("doctorId");
		if(doctorId == null) {
			return "redirect:/doctor/sign-in-view";
		}
		
		
		// DB SELECT - breakpoint
		// `reservings` 테이블에서 (reservings.doctorNumber)가 본인 id(doctors.id)와 동일한 행 List 추출
		List<DoctorsReservings> doctorsReservingsList = doctorsReservingsBO.getReservingsByDoctorId(doctorId, prevVisitDateParam, nextVisitDateParam);
	    String prevVisitDate = null;
	    String nextVisitDate = null;
		log.info("##### 의사의 예약 목록 : {}개 #####", doctorsReservingsList.size());

		
		// 공백 처리 - breakpoint
		if(!doctorsReservingsList.isEmpty()) { // postList가 비어있지 않을 때 페이징 정보 세팅
			// 버튼을 클릭하면 이전/다음 버튼에 따라 서로 다른 visitDate 값 할당(화면에 보이는 첫 번째/마지막 번째 객체의 visitDate 값)
			nextVisitDate = doctorsReservingsList.get(doctorsReservingsList.size() - 1).getVisitDate(); // List에 있는 가장 마지막 칸의 객체(행) 날짜 추출 => 해당 날짜의 다음 글을 추출하도록 유도
			log.info("##### doctorsReservingsList : {} #####", doctorsReservingsList);
			log.info("##### doctorsReservingsList.size() : {} #####", doctorsReservingsList.size());
			log.info("##### nextVisitDate : {} #####", nextVisitDate);
			prevVisitDate = doctorsReservingsList.get(0).getVisitDate(); // List에 있는 가장 첫 번째 칸의 객체(행) 날짜 추출 => 해당 날짜의 이전 글을 추출하도록 유도
			log.info("##### prevVisitDate : {} #####", prevVisitDate);
			
			
			// 더 이상 받아 올 이전 visitDate 데이터가 없다면 - breakpoint
			// 유저가 쓴 글들 중 제일 큰 작은 날짜가 visitDate와 같을 때 더 이상 없음
			if (doctorsReservingsBO.isPrevLastPageByVisitDate(doctorId, prevVisitDate)) {
	            prevVisitDate = null; // 비활성화 조건
	        }
			// 더 이상 받아 올 다음 visitDate 데이터가 없다면 - breakpoint
			// 유저가 쓴 글들 중 제일 큰 큰 날짜가 visitDate와 같을 때 더 이상 없음
			if (doctorsReservingsBO.isNextLastPageByVisitDate(doctorId, nextVisitDate)) {
				nextVisitDate = null; // 비활성화 조건
	        }
		}
		
		// Model에 객체 삽입
		model.addAttribute("doctorsReservingsList", doctorsReservingsList);
	    model.addAttribute("nextVisitDate", nextVisitDate);
	    model.addAttribute("prevVisitDate", prevVisitDate);
		
		
		return "doctor/todayPlanList";
	}
	
	
	// 의사 `진료 현황 화면`
	@GetMapping("/patient-status-view")
	// localhost/doctor/patient-status-view/{reservings.id}
	public String patientStatusView(
			// 필수 파라미터 불러오기1 : value, required 생략 (추천) - null이 아닌 column
			@RequestParam("id") int id,
			Model model, HttpSession session) {
		
		// 로그인 여부 검사 - breakpoint
		// session에 담을 변수(parameter)가 기억나지 않을 경우 RestController - @PostMapping("/sign-in") 참고
		Integer doctorNumber = (Integer) session.getAttribute("doctorId");
		if(doctorNumber == null) {
			return "redirect:/doctor/sign-in-view";
		}
		
		
		// DB SELECT - breakpoint
		DoctorsReservings reservingsMap = doctorsReservingsBO.getReservingsByIdDoctorNumber(id, doctorNumber);
		log.info("##### 불러온 데이터 : {} #####", reservingsMap);
		log.info("##### 불러온 데이터 환자 이름 : {} #####", reservingsMap.getCustomerName());
		
		
		// Model에 데이터 삽입
		model.addAttribute("doctorReservings", reservingsMap);
		
		
		return "doctor/patientStatus";
	}
	
	
	// 의사 `일정표 화면`
	// /doctor/{doctors.id}/week-plan-view
	@GetMapping("/calendar-plan-view")
	// localhost/doctor/calendar-plan-view
	public String calendarPlanView() {
		return "doctor/calendarPlan";
	}
	
	
	// 로그아웃 기능 구현
	// PatientRestController에서 생성한 session 초기화
	@GetMapping("/sign-out")
	public String doctorSignOut(HttpSession session) {
		// session 내용 초기화
		session.removeAttribute("doctorId");
		session.removeAttribute("doctorLoginId");
		session.removeAttribute("doctorName");
		
		// 로그인 페이지로 이동(redirect)
		return "redirect:/doctor/sign-in-view";
	}
	
	
}
