package com.backend.patient.bo;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.common.FileManagerService;
import com.backend.doctor.domain.Reservings;
import com.backend.patient.entity.PatientReservingsEntity;
import com.backend.patient.entity.ReserversEntity;
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
	
	
	// input : 
	// int id, int customerId, Integer customerId, String customerLoginId, String customerName, 
	// String title, String description, String visitDate, MultipartFile file	
    // output : X(void)
	// @PutMapping("/update")
    public void updateToReservings(int id, Integer customerId, String customerLoginId, 
    		String customerName, int doctorNum, String title, String description, 
    		String visitDate, MultipartFile file) {
    	
    	
    	// DB SELECT(기존 데이터 확인) - breakpoint
    	PatientReservingsEntity patientReservings = patientReservingsRepository.findByIdAndCustomerId(id, customerId);
    	log.info("!!!!! `reservings` DB SELECT 결과 :  {} !!!!!", patientReservings);
    	
        if (patientReservings == null) {
        	log.info("!!!!! [글 수정] patientReservings is null. id : {}, customerId : {} !!!!!", id, customerId);
            return; // 해당 ID와 Customer ID에 대한 예약이 없을 경우
        }
    	
		// 파일 존재 시 삭제 후 새 이미지 업로드 - breakpoint
		/*
		기존 글에 이미지가 부재
		- 파일 업로드 => 성공 시 DB 저장
				=> 실패 시 DB 저장 X

		기존 글에 이미지가 존재
		- 파일 업로드 => 성공 시 기존 이미지 제거 후 DB 저장
				=> 실패 시 기존 이미지 그대로, DB 저장 X
		*/
        String imagePath = null;
        if(file != null) {  // 새로 업로드 할 이미지가 존재
        	// 새로 업로드할 이미지 주소
        	imagePath = fileManagerService.uploadFile(file, customerLoginId);
        	log.info("!!!!! imagePath : {} !!!!!", imagePath);
        	
        	// 새로운 이미지 업로드 성공 && 기존 이미지가 존재 시 삭제
        	if(imagePath != null && patientReservings.getImagePath() != null) {
        		// 기존에 존재하는 이미지 폴더, 파일 제거(서버)
        		fileManagerService.deleteFolderFile(patientReservings.getImagePath());
        	}
        }
        
        
        // 필드 값 업데이트 - breakpoint
        PatientReservingsEntity updateReservingsEntity = patientReservings.toBuilder()
        		.doctorNumber(doctorNum)
        		.customerId(customerId)
        		.customerName(customerName)
        		.title(title)
        		.description(description)
        		.visitDate(visitDate)
        		.imagePath(imagePath != null ? imagePath : patientReservings.getImagePath()) // 새 이미지 파일 : 기존 이미지 파일
        		.build();
        		
        
        // DB Update - breakpoint
        patientReservingsRepository.save(updateReservingsEntity);
        log.info("!!!!! 업데이트한 `reservings` 테이블 예약 정보 : {} !!!!!", updateReservingsEntity);
    }
	
    
    // input : id(reservings.id), customerId
    // output : X
	// @DeleteMapping("/delete")
    public void deleteReservingsByIdCustomerId(int id, int customerId) {
    	
    	// DB SELECT - breakpoint
    	PatientReservingsEntity reservingsEntity =  patientReservingsRepository.findByIdAndCustomerId(id, customerId);
    	log.info("!!!!! 삭제 대상 데이터 : {} !!!!!", reservingsEntity);
    	if(reservingsEntity == null) {
    		log.info("!!!!! 삭제 대상 데이터 부재 id : {}, customerId : {}!!!!!", id, customerId);
    		return;
    	}
    	
    	
    	// DB DELETE - breakpoint
    	patientReservingsRepository.deleteById(id);
    	
    	
    	// 기존글에 이미지가 있다면 서버에서 폴더/파일 삭제 - breakpoint
    	if(reservingsEntity.getImagePath() != null) {
    		fileManagerService.deleteFolderFile(reservingsEntity.getImagePath());
    	}
    	
    }
    
}
