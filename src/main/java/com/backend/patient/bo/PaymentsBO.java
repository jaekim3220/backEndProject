package com.backend.patient.bo;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.backend.common.FileManagerService;
import com.backend.patient.entity.PaymentsEntity;
import com.backend.patient.repository.PaymentsRepository;
import com.backend.patient.repository.ReserversRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper, XML) <--> DB영역 
*/

// Service(BO)영역

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentsBO {

	
	private final PaymentsRepository paymentsRepository;
	
	// input : 
	// int doctorNum, int customerId, int amount,
	// String merchantUid, String impUid, String customerName,
	// String customerEmail, String customerPostcode,
	// output : PaymentsEntity
	// @PostMapping("/reserve-payment")
	public int addPaymentsEntity(int doctorNum, int customerId, int amount,
			String merchantUid, String impUid, String customerName, 
			String customerEmail, String customerPostcode) {
		
		PaymentsEntity paymentsEntity = 
				PaymentsEntity.builder()
				.doctorNum(doctorNum)
				.customerId(customerId)
				.amount(amount)
				.merchantUid(merchantUid)
				.impUid(impUid)
				.customerName(customerName)
				.customerEmail(customerEmail)
				.customerPostcode(customerPostcode)
				.build();
				
		log.info("##### paymentsEntity : {} #####", paymentsEntity);
		
		PaymentsEntity savedPaymentsEntity = paymentsRepository.save(paymentsEntity);
		log.info("##### savedPaymentsEntity : {} #####", savedPaymentsEntity);
		
		return savedPaymentsEntity != null ? 1 : 0;
	}
	
	
	// input : int id, int customerId
	// output : PaymentsEntity
	// @GetMapping("/reserve-detail-view")
	public PaymentsEntity getPaymentsByIdAndCustomerId(int id, int customerId) {
		return paymentsRepository.findByIdAndCustomerId(id, customerId);
	}
	
	
	// input : int id, int customerId, String isCanceled
	// output : X
	// @PostMapping("/payments-cancel")
	public int updatePaymentsEntity(int id, int customerId, String isCanceled) {
		
		// 기존 데이터 조회 - breakpoint 
	    PaymentsEntity existingPayment = paymentsRepository.findByIdAndCustomerId(id, customerId);
	    if (existingPayment == null) {
	        // 데이터가 존재하지 않으면 업데이트 실패
	        log.warn("결제 정보가 존재하지 않습니다. ID: {}, CustomerId: {}", id, customerId);
	        return 0;
	    }
	    
	    // 변경할 필드 설정 - breakpoint
	    existingPayment.setIsCanceled(isCanceled);
	    log.info("Update 할 row 데이터 : {}", existingPayment);
		
		
		// Save 할 updatePayments 데이터
	    PaymentsEntity updatedPayment = paymentsRepository.save(existingPayment);
	    log.info("업데이트된 PaymentsEntity: {}", updatedPayment);
		
	    // 성공 여부 반환
		return updatedPayment != null ? 1 : 0;
	}
	
	
}
