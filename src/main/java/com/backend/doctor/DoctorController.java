package com.backend.doctor;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.backend.doctor.bo.DoctorsBO;
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
	public String todayPlanView(Model model, HttpSession session) {
		
		
		// 로그인 여부 검사 - breakpoint
		// session에 담을 변수(parameter)가 기억나지 않을 경우 RestController - @PostMapping("/sign-in") 참고
		Integer doctorId = (Integer) session.getAttribute("doctorId");
		if(doctorId == null) {
			return "redirect:/doctor/sign-in-view";
		}
		
		
		// DB SELECT - breakpoint
		// `reservings` 테이블에서 (reservings.doctorNumber)가 본인 id(doctors.id)와 동일한 행 List 추출
		List<DoctorsReservings> doctorsReservings = doctorsReservingsBO.getReservingsByDoctorId(doctorId);
		log.info("##### 의사의 예약 목록 : {}개 #####", doctorsReservings.size());
		
		
		// Model에 객체 삽입
		model.addAttribute("doctorsReservingsList", doctorsReservings);
		
		
		return "doctor/todayPlanList";
	}
	
	
	// 의사 `진료 현황 화면`
	// /doctor/{doctors.id}/patient-status-view
	@GetMapping("/patient-status-view")
	// localhost/doctor/patient-status-view
	public String patientStatusView() {
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
