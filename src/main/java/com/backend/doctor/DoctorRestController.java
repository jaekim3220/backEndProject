package com.backend.doctor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.doctor.bo.DoctorsBO;
import com.backend.doctor.domain.Doctors;
import com.backend.doctor.mapper.DoctorsMapper;

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

@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorRestController {

	
	// 생성자를 사용한 의존성 주입
	private final DoctorsBO doctorsBO;
	
    // 아이디 중복 확인
	@GetMapping("/is-duplicate-id")
	public Map<String, Object> isDuplicateId(
			// 필수 파라미터 불러오기1 : value, required 생략 (추천) - null이 아닌 column
			@RequestParam("doctorId") String doctorId) {
		
		
		// DB SELECT - breakpoint
		Map<String, Object> doctors = doctorsBO.getDoctorsByDoctorId(doctorId);

		
		// Response 응답값 - breakpoint
		// 중복인 경우/아닌 경우 구분 (Console 창의 SQL문에서 입력 값 return 확인) - breakpoint
        // 응답 값 처리 (doctorId가 존재하면 중복, 존재하지 않으면 중복 아님)
        boolean isDuplicateId = (doctors != null && !doctors.isEmpty());
		
		// 응답값 => Map => JSON String
		// 성공이면서 중복 : {"code":200, "is_duplicate_id":true}
		// 성공이면서 중복아님 : {"code":200, "is_duplicate_id":false}
		Map<String, Object> result = new HashMap<>();
		result.put("code", 200);
		result.put("is_duplicate_id", isDuplicateId);
		
		return result;
		
		
	}
	
}
