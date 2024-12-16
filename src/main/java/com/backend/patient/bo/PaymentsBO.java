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

	
	// 생성자를 사용한 DI(Dependency Injection)
	private final PaymentsRepository paymentsRepository;
	private final PatientDeleteBO patientDeleteBO;
	
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
	
	

    /**
     * 결제 취소와 관련된 모든 작업을 처리 (결제 상태 업데이트 + 예약 정보 삭제).
     * 
     * @param id 결제 ID
     * @param customerId 환자 ID
     * @param isCanceled 취소 상태 (예: "canceled")
     * @return 성공 여부 (1: 성공, 0: 실패)
     */
	// input : int id, int customerId, String isCanceled
	// output : X
	// @PostMapping("/payments-cancel")
	public int cancelPayment(int id, int customerId) {
		
		try {
			// 기존 데이터 조회 - breakpoint 
			PaymentsEntity existingPayment = paymentsRepository.findByIdAndCustomerId(id, customerId);
			if (existingPayment == null) {
				// 데이터가 존재하지 않으면 업데이트 실패
				log.warn("결제 정보가 존재하지 않습니다. ID: {}, CustomerId: {}", id, customerId);
				return 0;
			}
			
			// 결제 취소 상태 업데이트 - breakpoint
			existingPayment.setIsCanceled("결제취소");
			log.info("existingPayment 데이터 : {}", existingPayment);
			
			// Save 할 updatePayments 데이터 - breakpoint
			PaymentsEntity updatedPayment = paymentsRepository.save(existingPayment);
			log.info("Update 할 row 데이터 : {}", updatedPayment);
			
			
			// 예약 정보 삭제 - breakpoint
			// `reservers`, `reservings`
			int deleteDataBase = patientDeleteBO.patientDeleteBO(id, customerId);
			if (deleteDataBase == 0) {
				log.warn("@@@@@ 예약 정보 삭제 실패. ID : {}, CustomerId : {} @@@@@", id, customerId);
				log.warn("@@@@@ 예약 정보 삭제 실패. deleteDataBase : {} @@@@@", deleteDataBase);
				throw new IllegalStateException("예약 정보 삭제 실패");
			}
			
			return 1; // 모든 작업 성공
		} catch (Exception e) {
			log.error("결제 취소 처리 중 오류 발생. 롤백 실행: {}", e.getMessage());
            throw e; // 트랜잭션 롤백을 유도
		}
		
	} // @PostMapping("/payments-cancel") 종료
	
	
}
