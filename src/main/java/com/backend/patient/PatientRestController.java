package com.backend.patient;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.common.EncryptUtils;
import com.backend.patient.bo.CustomerBO;
import com.backend.patient.entity.CustomerEntity;
import com.backend.patient.repository.CustomerRepository;

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

	// 어노테이션(Annotation)
	@Autowired // DI(Dependency Injection) : 필드를 사용한 의존성 주입
	private CustomerBO customerBO;
	
	// 아이디 중복 확인
	// 단순한 select문으로 JPA(Object Relational Mapping) 사용
	@GetMapping("/is-duplicate-id")
	public Map<String, Object> isDuplicateId(
			// 필수 파라미터 불러오기1 : value, required 생략 (추천) - null이 아닌 column
			@RequestParam("customerId") String customerId) {
		
		
		// DB SELECT - breakpoint
		CustomerEntity customer = customerBO.getCustomerEntityByCustomerId(customerId);
		
		
		// 중복인 경우/아닌 경우 구분 (Console 창의 SQL문에서 입력 값 return 확인) - breakpoint
		boolean isDuplicateId = false; // false = 중복 아님
		if(customer != null) { // 기존 값이 존재 = 중복
			isDuplicateId = true;
		}
		
		
		// 응답 값 - breakpoint
		// 응답값 => Map => JSON String
		// {"code":200, "is_duplicate_id":true}
		Map<String, Object> result = new HashMap<>();
		result.put("code", 200);
		result.put("is_duplicate_id", isDuplicateId);
		
		return result;
	}
	
	
	// form 태그(post)를 사용한 회원가입 기능
	@PostMapping("/sign-up")
	public Map<String, Object> patientSignUp(
			// 필수 파라미터 불러오기1 : value, required 생략 (추천) - null이 아닌 column
			@RequestParam("customerId") String customerId,
			@RequestParam("password") String password,
			@RequestParam("name") String name,
			@RequestParam("birthDate") String birthDate,
			@RequestParam("email") String email) {
		
		
		// parameter(비밀번호) 암호화 - breakPoint
		
		// 1. Salt(난수) 생성
		String salt = EncryptUtils.generateSalt();
		
		// 2. 비밀번호 해싱
		String hashedPassword = EncryptUtils.hashingSHA2(password, salt);
		
		// DB INSERT - breakPoint
		CustomerEntity customer = customerBO.addCustomer(customerId, hashedPassword, salt, name, birthDate, email);
		
		
		// Response(JSON String) - breakPoint
		// {"code" : 200, "result" : "회원가입 성공"}
		Map<String, Object> result = new HashMap<>();
		if(customer != null) {
			result.put("code", 200);
			result.put("result", "성공");
		} else {
			result.put("code", 500);
			result.put("error_message", "회원가입에 실패했습니다.");
		}
		return result;
		
	}
	
}
