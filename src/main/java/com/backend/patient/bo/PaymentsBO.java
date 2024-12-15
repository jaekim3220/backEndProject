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
	
	
}
