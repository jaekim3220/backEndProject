package com.backend.patient;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper, XML) <--> DB영역 
*/

// View영역

/*
<Response 방법 - 서버 기준>
@Controller + return String => ViewResolver => HTML 파일 렌더링(Model) => HTML
@Controller + @ResponseBody return String => HTTPMessageConverter => HTML
@Controller + @ResponseBody return 객체(map, list) => HTTPMessageConverter => jackson => JSON
@RestController return map => JSON
Model은 HTML일 경우 사용(@ResponseBody일 경우 Model 사용 불가)
*/

@RestController
@RequestMapping("/patient")
public class PatientRestController {

	
	// 아이디 중복 확인
	// 단순한 select문으로 JPA(Object Relational Mapping) 사용
	@GetMapping("/is-duplicate-id")
	public Map<String, Object> isDuplicateId() {
		
		// DB SELECT breakpoint
		
		
		// 중복인 경우/아닌 경우 구분 (Console 창의 SQL문에서 입력 값 return 확인)
		
		
		// 응답 값 breakpoint
		// 응답값 => Map => JSON String
		// {"code":200, "is_duplicate_id":true}
		Map<String, Object> result = new HashMap<>();
		result.put("code", 200);
		result.put("result", "성공");
		
		return result;
	}
	
}
