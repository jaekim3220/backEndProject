package com.backend.hospital;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	
	
	@GetMapping("/{id}/doctor-reserve-view")
	// http://localhost/hospital/{doctors.id}/doctor-reserve-view
	// URL 중간에 parameter가 삽입되어 @RequestParam 대신 @PathVariable 사용
	public String doctorReserve(@PathVariable("id") int id, Model model) {
		
		// 특정 의사 데이터 추출
		Doctors doctor = doctorsBO.getDoctorsById(id);
		
		// Model에 데이터 삽입
		model.addAttribute("doctorId", doctor.getId());
		
		return "hospital/doctorReserve";
	}

	
}
