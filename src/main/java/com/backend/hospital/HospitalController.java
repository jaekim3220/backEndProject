package com.backend.hospital;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.backend.doctor.bo.DoctorsBO;
import com.backend.doctor.domain.Doctors;

import lombok.RequiredArgsConstructor;

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
@RequestMapping("/hospital")
@RequiredArgsConstructor
public class HospitalController {

	private final DoctorsBO doctorsBO;
	

	/**
	 * 병원 메인 화면
	 * @return
	 */
	@GetMapping("")
	// http:localhost/hospital
	public String hospital(Model model) {
		
        // 부서별 의사 목록 추출
        List<Doctors> cosmeticDoctors = doctorsBO.getDoctorsByDepartment(1);
        List<Doctors> orthopedicDoctors = doctorsBO.getDoctorsByDepartment(2);
        List<Doctors> internalDoctors = doctorsBO.getDoctorsByDepartment(3);
        
        // Model에 데이터 삽입
        model.addAttribute("cosmeticDoctors", cosmeticDoctors);
        model.addAttribute("orthopedicDoctors", orthopedicDoctors);
        model.addAttribute("internalDoctors", internalDoctors);
		
		return "hospital/mainpage";
	}
	
	
	// TODO : 의료진 소개 및 예약 화면
	// 확인을 위한 임시 @GetMapping : 의사의 고유 id 값을 받아서 해당 의사 예약 페이지로 이동할 것
	// /hospital/{doctors.id}/doctor-reserve-view
	@GetMapping("/doctor-reserve-view")
	// http:localhost/hospital/doctor-reserve-view
	public String doctorReserve() {
		return "hospital/doctorReserve";
	}

	
}
