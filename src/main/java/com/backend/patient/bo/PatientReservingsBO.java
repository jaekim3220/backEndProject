package com.backend.patient.bo;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.common.FileManagerService;
import com.backend.patient.entity.PatientReservingsEntity;
import com.backend.patient.repository.PatientReservingsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper, XML) <--> DB영역 
*/

// Service(BO)영역

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientReservingsBO {
	
	
	// 어노테이션(Annotation) - 생성자를 사용한 DI(Dependency Injection) 의존성 주입
	private final PatientReservingsRepository patientReservingsRepository;
	private final FileManagerService fileManagerService;
	
	
	// input : 
	// int customerId, String customerLoginId, String customerName, 
	// String title, String description, String visitDate, MultipartFile file	
	// output : int(성공한 행의 개수)
	// @PostMapping("/reserving")
	public int addToReservings(int doctorNumber, int customerId, String customerName, 
			String title, String description, String visitDate, MultipartFile file, String customerLoginId) {
		
		
		// 이미지 파일 저장 선택 - breakpoint
		String imagePath = null;
		if(file != null) { // 이미지 파일 존재
			imagePath = fileManagerService.uploadFile(file, customerLoginId);  // 파일이 있는 경우에만 파일 저장
		}
		
		// ReservingsEntity 객체 생성 - breakpoint
		PatientReservingsEntity reservings = PatientReservingsEntity.builder()
				.doctorNumber(customerId)
				.customerId(customerId)
				.customerName(customerName)
				.title(title)
				.description(description)
				.visitDate(visitDate)
				.imagePath(imagePath)
				.status("예약대기")
				.treatment("진료대기")
				.build();
		
		log.error("[!!!!! savedReservingsEntity : {} !!!!!] : ", reservings);
		
		// DB INSERT - breakpoint
        try {
        	PatientReservingsEntity savedReservingsEntity = patientReservingsRepository.save(reservings);
            return savedReservingsEntity != null ? 1 : 0; // 저장된 경우 1 반환, 실패 시 0 반환
        } catch (Exception e) {
            // e.printStackTrace();
            log.error("[!!!!!DB INSERT 실패!!!!!] : ", e);
            return 0;
        }
        
	}
	
	
}
