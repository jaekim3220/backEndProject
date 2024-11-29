package com.backend.patient.bo;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.common.FileManagerService;
import com.backend.patient.entity.ReserversEntity;
import com.backend.patient.repository.ReserversRepository;

import lombok.RequiredArgsConstructor;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper, XML) <--> DB영역 
*/

// Service(BO)영역

@Service
@RequiredArgsConstructor
public class ReserversBO {

	
	// 어노테이션(Annotation) - 생성자를 사용한 DI(Dependency Injection) 의존성 주입
	private final ReserversRepository reserversRepository;
	private final FileManagerService fileManagerService;
	
	
	// input : 
	// output : int(성공한 행의 개수)
	// @PostMapping("/reserving")
	public int addPatientReserve(int customerId, int doctorNum, String title, 
			String description, String visitDate, MultipartFile file) {
		
		
		// 이미지 파일 저장 선택 - breakpoint
		String imagePath = null;
		if(file == null) { // 이미지 파일 존재
			imagePath = fileManagerService.uploadFile(file, customerId);  // 파일이 있는 경우에만 파일 저장
		}
		
		
		// ReserversEntity 객체 생성 및 DB INSERT - breakpoint
		ReserversEntity savedEntity = reserversRepository.save(
				ReserversEntity.builder()
				.customerId(customerId)
				.doctorNum(doctorNum)
				.title(title)
				.description(description)
				.visitDate(visitDate)
				.imagePath(imagePath)
				.condition(null)
				.build());
		
		// 저장된 Entity의 row(id) 반환 - breakpoint
		return savedEntity != null ? 1 : 0; // 저장된 경우 1 반환, 실패 시 0 반환
	}
	
}
