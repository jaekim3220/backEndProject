package com.backend.common;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component // Spring Bean 등록
public class HashingSaltPassword {

	
	// 비밀번호를 해싱하고 Salt를 결합하여 반환하는 메서드
	public String hashingSaltPassword(String password) {
		// 1. Salt(난수) 생성
		String salt = EncryptUtils.generateSalt();
		log.info("### salt : {} ###", salt);
		
		// 2. 비밀번호 해싱
		String hashedPassword = EncryptUtils.hashingSHA2(password, salt);
		log.info("### hashedPassword : {} ###", hashedPassword);
		
		// 3. Salt + HashedPassword 결합
		String combinedPassword = salt + hashedPassword;
		log.info("### combinedPassword : {} ###", combinedPassword);
		
		return combinedPassword;
	}
}
