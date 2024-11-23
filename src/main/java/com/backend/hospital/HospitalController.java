package com.backend.hospital;

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
public class HospitalController {


	/**
	 * 병원 메인 화면
	 * @return
	 */
	@GetMapping("/hospital")
	// http:localhost/hospital
	public String hospital() {
		return "hospital/mainpage";
	}
	
	
	// TODO : 의료진 소개 및 예약 화면
	// 확인을 위한 임시 @GetMapping : 의사의 고유 id 값을 받아서 해당 의사 예약 페이지로 이동할 것
	// /hospital/{doctors.id}/doctor-reserve-view
	@GetMapping("/doctor-reserve-view")
	// http:localhost/hospital
	public String doctorReserve() {
		return "hospital/doctorReserve";
	}

	
}
