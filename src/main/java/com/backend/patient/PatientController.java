package com.backend.patient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

@Controller
@RequestMapping("/patient")
public class PatientController {

	
	/**
	 * 환자(고객) 회원가입 화면
	 * @return
	 */
	@GetMapping("/sign-up-view")
	// http:localhost/patient/sign-up-view
	public String signUpView() {
		return "patient/patientSignUp";
	}
	
	
	// 환자(고객) 로그인 화면
	@GetMapping("/sign-in-view")
	// http:localhost/patient/sign-in-view
	public String signInView() {
		return "patient/patientSignIn";
	}
	
	
	// 예약 페이지 화면
	// /patient/{doctors.id}/reserve-view
	// 확인을 위한 임시 @GetMapping : 의사의 고유 id 값을 받아서 해당 의사 예약 페이지로 이동할 것
	@GetMapping("/reserve-view")
	// http:localhost/patient/reserve-view
	public String reserveCreateView(Model model) {

		// 달력에 금일, 최소/최대 예약 날짜 설정을 
		// Thymeleaf 문법으로 구현하기 위해 Model에 값을 할당 
	    LocalDateTime now = LocalDateTime.now();
	    LocalDateTime tomorrow = now.plusDays(1);
	    LocalDateTime nextMonth = now.plusMonths(1);
        
        DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        model.addAttribute("currentDate", now.format(dateformat)); // 오늘 날짜 00:00
        model.addAttribute("minDate", tomorrow.format(dateformat)); // 내일 날짜 00:00
        model.addAttribute("maxDate", nextMonth.format(dateformat)); // 한 달 뒤 날짜 23:59
		
		return "patient/reserveCreate";
	}
	
	
	// 예약 상세 페이지 화면
	// /patient/{reservers.id}/reserve-detail
	// 확인을 위한 임시 @GetMapping : 의사의 고유 id 값을 받아서 해당 의사 예약 페이지로 이동할 것
	@GetMapping("/reserve-detail-view")
	// localhost/patient/reserve-detail-view
	public String reserveDetailView() {
		return "patient/reserveDetail";
	}
	
	
}
