package com.backend.test;

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
}
