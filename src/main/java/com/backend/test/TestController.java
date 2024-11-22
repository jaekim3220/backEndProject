package com.backend.test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

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
}
