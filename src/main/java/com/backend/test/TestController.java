package com.backend.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.backend.customer.mapper.CustomerMapper;

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
public class TestController {
	
	
	// 어노테이션(Annotation)
	@Autowired // DI(Dependency Injection) : 의존성 주입
	private CustomerMapper customerMapper;
	

	@ResponseBody
	@GetMapping("/test1")
	// http:localhost:8080/test1
	public String test1() {
		return "테스트";
	}
	
	@ResponseBody
	@GetMapping("/test2")
	// http:localhost:8080/test2
	public String test2() {
		return "<h1>HTML 테스트</h1>";
	}
	
	@ResponseBody
	@GetMapping("/test3")
	// http:localhost:8080/test3
	public Map<String, Object> test3() {
		Map<String, Object> map = new HashMap<>();
		map.put("a", 111);
		map.put("b", 222);
		return map;
	}
	
	
	@GetMapping("/test4")
	// http:localhost/test4
	public String test4() {
		return "test/test";
	}
	
	
	@ResponseBody
	@GetMapping("/test5")
	// http:localhost/test5
	public List<Map<String, Object>> test5() {
		return customerMapper.selectCustomerList();
	}
}
