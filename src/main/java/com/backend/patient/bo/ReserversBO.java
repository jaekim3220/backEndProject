package com.backend.patient.bo;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import com.backend.common.FileManagerService;
import com.backend.patient.entity.ReserversEntity;
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
public class ReserversBO {

	
	// 어노테이션(Annotation) - 생성자를 사용한 DI(Dependency Injection) 의존성 주입
	private final ReserversRepository reserversRepository;
	private final FileManagerService fileManagerService;
	
	
	// input : 
	// output : int(성공한 행의 개수)
	// @PostMapping("/reserving")
	public int addPatientReserve(int customerId, String customerLoginId, int doctorNum, String title, 
			String description, String visitDate, MultipartFile file) {
		
		
		// 이미지 파일 저장 선택 - breakpoint
		String imagePath = null;
		if(file != null) { // 이미지 파일 존재
			imagePath = fileManagerService.uploadFile(file, customerLoginId);  // 파일이 있는 경우에만 파일 저장
		}
		
		
        // ReserversEntity 객체 생성 - breakpoint
        ReserversEntity reserving = ReserversEntity.builder()
                .customerId(customerId)
                .doctorNum(doctorNum)
                .title(title)
                .description(description)
                .visitDate(visitDate)
                .imagePath(imagePath)
                .status("예약대기")
                .build();
        
        // DB INSERT - breakpoint
        try {
            ReserversEntity savedEntity = reserversRepository.save(reserving);
            return savedEntity != null ? 1 : 0; // 저장된 경우 1 반환, 실패 시 0 반환
        } catch (Exception e) {
            // e.printStackTrace();
            log.error("[!!!!!DB INSERT 실패!!!!!] : ", e);
            return 0;
        }
	}
	
	
	// input : customerId(int)
	// output : List<ReserversEntity>
	// @GetMapping("/reserve-list-view")
	public List<ReserversEntity> getreserveListBycustomerId(
			@Param("customerId") int customerId) {
		
		
		// DB SELECT - breakpoint
		List<ReserversEntity> reserveList = reserversRepository.findByCustomerId(customerId);
        
        if (reserveList.isEmpty()) {
            log.info("!!!!!예약 목록 없음!!!!! : {}", customerId);
        } else {
            log.info("!!!!!예약 목록 존재!!!!! : {}개", reserveList.size());
        }
        
        
        return reserveList;
	}
	
}
