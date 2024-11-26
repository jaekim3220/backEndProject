package com.backend.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

// HASH(단방향 암호화) 저장소
// SHA256 + Salt
public class EncryptUtils {
	
	
	// hashing 기능 구현
	// input : 회원가입 비밀번호, salt
	// output : hashing된 비밀번호
	// SHA 알고리즘 생성
	public static String hashingSHA2(String password, String salt) { // input parameter
		
		try {
			// 사용할 알고리즘 설정 : SHA-256
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			
			// Salt와 원본 비밀번호 결합
			String saltedInput = password + salt;
			md.update(saltedInput.getBytes());
			
			// 해시 처리
			byte[] hashedBytes = md.digest();
			
			// Base64 인코딩으로 출력(바이너리 데이터를 텍스트 형식으로 인코딩)
			return Base64.getEncoder().encodeToString(hashedBytes);
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
            // SHA-256 알고리즘이 지원되지 않을 경우 예외 처리
			e.printStackTrace(); // 콘솔에 스택 트레이스를 출력
			return null; // 또는 예외에 적합한 기본값 반환
		}
	}
	
	
	// 난수 기반 Salt 생성 코드
	// => 독립적으로 Salt를 생성하고 해시를 수행해 개인정보 안정성을 높임
	public static String generateSalt() {
		try {
            // SecureRandom을 사용해 난수 생성
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            byte[] saltBytes = new byte[16];
            sr.nextBytes(saltBytes);

            // Base64로 Salt 반환
            return Base64.getEncoder().encodeToString(saltBytes);
        } catch (Exception e) {
            throw new RuntimeException("비밀번호의 Salt 생성 실패", e);
        }
    }
	
}
