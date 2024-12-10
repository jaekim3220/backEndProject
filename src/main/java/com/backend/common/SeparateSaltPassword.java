package com.backend.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component // Spring Bean 등록
public class SeparateSaltPassword {

	
	// DB에 저장된 combinedPassword에서 salt와 hashedPassword 분리 메서드
	public Map<String, Object> saparateSaltPassword(String password, String combinedPassword) {
	    // Salt와 HashedPassword 분리
	    String salt = combinedPassword.substring(0, 24); // Salt 길이 (Base64 기준 16bytes)
	    log.info("##### 로그인 salt : {} #####", salt);
	    String installedHashedPassword = combinedPassword.substring(24);
	    log.info("##### 로그인 hashedPassword : {} #####", installedHashedPassword);
	    
	    // 입력된 비밀번호와 Salt로 해싱
	    String inputHashedPassword = EncryptUtils.hashingSHA2(password, salt);
	    log.info("##### 로그인 inputHashedPassword : {} #####", inputHashedPassword);
		
	    
	    Map<String, Object> passwordMaps = new HashMap<>();
	    
	    passwordMaps.put("installedHashedPassword", installedHashedPassword);
	    passwordMaps.put("inputHashedPassword", inputHashedPassword);
	    
		return passwordMaps;
	}
}
