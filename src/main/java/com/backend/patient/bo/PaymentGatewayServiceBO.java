package com.backend.patient.bo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PortOne API를 사용해 Access Token 발급 및 결제 취소 요청을 처리하는 서비스 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentGatewayServiceBO {

	
	/*
	 * api.yml에 저장한 PortOne API 연동 데이터를 사용해
	 * Access Token 발급과 결제 취소 API 호출 기능을 구현
	 * ReserversPayBO 참고
	 */
	
	// RestTemplate : HTTP 요청을 보낼 때 사용하는 객체
	private final RestTemplate restTemplate = new RestTemplate();
	
	// Access Token과 만료 시간
	private String accessToken; // 발급받은 Access Token
	private long tokenExpiredAt; // Access Token의 만료 시간 (Unix Time 형식)
	
	// api.yml에서 설정, application.yml에 import한 PortOne 연동 데이터 값을 가져옴
	// ReserversPayBO 참고
    @Value("${portone.api-url}")
    private String apiUrl; // PortOne API 기본 URL

    @Value("${portone.api-key}")
    private String apiKey; // PortOne API 키


    @Value("${portone.api-secret-key}")
    private String apiSecretKey; // PortOne API 시크릿 키
    
    @Value("${portone.imp-key}")
    private String impKey; // PortOne imp key
	
    
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 직렬화를 위한 ObjectMapper

    /**
     * Access Token 요청에 사용될 DTO 클래스
     */
//    @Data
//    private static class AccessTokenRequest {
//        private String imp_key;
//        private String imp_secret;
//
//        public AccessTokenRequest(String impKey, String impSecret) {
//            this.imp_key = impKey;
//            this.imp_secret = impSecret;
//        }
//    }
    
    /**
     * Access Token 발급 메서드
     * PortOne API로부터 Access Token을 받아와 저장
     * Access Token은 만료 시간이 정해져있음
     * "access_token의 만료기한은 발행 시간 부터 30분입니다. 
     * 만료된 토큰으로 API 요청을 하면 401 Unauthorized 응답을 받습니다."
     */
    public String getAccessToken() {
    	// 현재 시간이 Access Token 만료 시간 이전이면, 기존 Token은 유효함
//    	long currentTime = System.currentTimeMillis() / 1000; // 현재 시간 (초 단위)
//    	if (accessToken != null && currentTime < tokenExpiredAt) {
//            log.info("기존 Access Token 사용 가능");
//            return accessToken;
//        }
    	
    	
    	// token 발급 시작
    	try { 
    		// PortOne Access Token 발급 API URL
    		String url = apiUrl + "/users/getToken";
            log.info("##### url : {} #####", url);
            
//            // 요청 DTO 생성
//            AccessTokenRequest tokenRequest = new AccessTokenRequest(apiKey, apiSecretKey);
//            log.info("##### tokenRequest : {} #####", tokenRequest);
    		
    		// 요청 본문에 필요한 데이터 생성, 저장
    		Map<String, String> requestBody = new HashMap<>();
    		requestBody.put("imp_key", apiKey); // API 키
    		requestBody.put("imp_secret", apiSecretKey); // 시크릿 키
    		
    		// HTTP 요청 헤더 설정 (JSON 형식)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            log.info("##### HTTP 요청 헤더 : {} #####", headers);
            
            // 요청 본문을 JSON으로 직렬화
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
            log.info("##### HttpEntity 객체 : {} #####", request);
            
//            // 요청 데이터와 헤더를 합쳐 HttpEntity 객체 생성
//            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
//            log.info("##### HttpEntity 객체 : {} #####", request);
            
            // RestTemplate을 사용해 POST 요청
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            // HTTP 응답이 성공일 경우 Access Token 저장
            if (response.getStatusCode() == HttpStatus.OK) {
                // 응답 데이터에서 Access Token과 만료 시간 추출
            	Map<String, Object> responseBody = response.getBody();
                Map<String, Object> responseData = (Map<String, Object>) responseBody.get("response");
                
                this.accessToken = (String) responseData.get("access_token"); // Access Token
                this.tokenExpiredAt = ((Number) responseData.get("expired_at")).longValue(); // 만료 시간

                log.info("##### Access Token 발급 성공 : {} #####", accessToken);
                log.info("##### Access Token 만료 시간 : {} #####", tokenExpiredAt);
                return accessToken; // Access Token return
            } else {
                log.error("Access Token 발급 실패 : {}", response.getBody());
                throw new IllegalStateException("Access Token 발급에 실패했습니다.");
            } 
            
		} catch (Exception e) {
            log.error("Access Token 요청 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("Access Token 요청 실패", e);
		}
    }
    
    
    /**
     * 결제 취소 메서드
     * PortOne API를 통해 특정 주문 번호(merchant_uid)에 대한 결제를 취소합니다.
     *
     * @param merchantUid 주문 번호
     * @param reason      결제 취소 사유
     * @return 결제 취소 성공 여부 (true/false)
     */
	public boolean cancelPayment(String merchantUid, String cancelReason) {
        try {
            // PortOne 결제 취소 API URL
        	String url = apiUrl + "/payments/cancel";
        	log.info("##### PortOne 결제 취소 API URL : {} #####", url);
        	
        	// Access Token 발급
            String token = getAccessToken();
            log.info("##### Access Token : {} #####", token);

            // 결제 취소 요청을 위한 데이터
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("merchant_uid", merchantUid); // 주문 번호
            requestBody.put("cancelReason", cancelReason); // 결제 취소 사유
            
            // HTTP 요청 헤더 설정 (JSON 형식 + Access Token)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token); // Authorization 헤더에 Access Token 추가
            
            // 요청 데이터와 헤더를 합쳐 HttpEntity 객체 생성
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            // RestTemplate을 사용해 POST 요청을 보냄 - 에러발생 구간
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            
            // HTTP 응답이 성공일 경우 결제 취소 성공
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("결제 취소 성공. MerchantUid : {}", merchantUid);
                return true;
            } else {
                log.warn("결제 취소 실패. 응답 : {}", response.getBody());
                return false;
            }
        } catch (Exception e) {
            log.error("결제 취소 요청 중 오류 발생 : {}", e.getMessage());
            return false;
        }
    }
}
