package com.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.backend.common.FileManagerService;

import lombok.RequiredArgsConstructor;

@Configuration // Spring Bean 등록
@RequiredArgsConstructor // 생성자를 사용한 의존성 주입
public class WebMvcConfig implements WebMvcConfigurer {
	
	
	// 지정(예언)한 imagePath와 서버에 업로드 된 실제 imagePath와 매핑
	// Override : 상위 클래스가 가지고 있는 메서드를 하위 클래스가 재정의해서 사용
	// 즉, 상속받은 메서드의 내용을 변경
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry
		.addResourceHandler("/images/**") // path(주소) : http://localhost/images/aaaa_1730889245989/idPhoto.jpg
		.addResourceLocations("file:///" + FileManagerService.FILE_UPLOAD_PATH); // 실제 이미지 위치 : FileManagerService에서 생성한 위치
	}
	
	
}
