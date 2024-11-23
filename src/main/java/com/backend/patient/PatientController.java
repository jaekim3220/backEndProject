package com.backend.patient;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
public class PatientController {

	
	/**
	 * 환자(고객) 회원가입 화면
	 * @return
	 */
	@GetMapping("/patient/sign-up-view")
	// http:localhost/patient/sign-up-view
	public String signUpView() {
		return "patient/patientSignUp";
	}
	
	
	// 환자(고객) 로그인 화면
	@GetMapping("/patient/sign-in-view")
	// http:localhost/patient/sign-in-view
	public String signInView() {
		return "patient/patientSignIn";
	}
	
	
}
