package com.backend.doctor;

import org.springframework.stereotype.Controller;
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
@RequestMapping("/doctor")
public class DoctorController {

	
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
	public String todayPlanView() {
		return "doctor/todayPlanList";
	}
	
	
	// 의사 `진료 현황 화면`
	// /doctor/{doctors.id}/patient-status-view
	@GetMapping("/patient-status-view")
	// localhost/doctor/patient-status-view
	public String patientStatusView() {
		return "doctor/patientStatus";
	}
	
	
}
