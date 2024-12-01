package com.backend.patient.bo;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
			int customerId, int page, int size) {
		
        // PageRequest의 Pageable 객체 생성 : 페이지 번호, 페이지 크기, 정렬 순서를 지정
		// page : 조회할 페이지 번호
		// size : 한 페이지당 조회할 항목의 수(Limit)
		// descending() : 내림차순 정렬(ORDER BY)
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        
        // DB SELECT - breakpoint
        // PageRequest의 Pageable 객체 조건으로 정렬한 customerId 값과 동일한 id 값이 있는 row 데이터
        Page<ReserversEntity> reserveList = reserversRepository.findByCustomerId(customerId, pageable);
		
//		// DB SELECT - breakpoint
//		List<ReserversEntity> reserveList = reserversRepository.findByCustomerId(customerId);
        
        if (reserveList.isEmpty()) { // 예약 목록이 없을 경우
            log.info("!!!!!예약 목록 없음!!!!! : {}", customerId);
        } else { // 예약 목록이 있을 경우
            log.info("!!!!!예약 목록 존재!!!!! : {}개", reserveList.getSize());
        }
        
        // 현재 페이지에 해당하는 데이터 반환
        return reserveList.getContent();
	}
    // 총 페이지 수 조회 메서드
    public int getTotalPagesByCustomerId(int customerId, int size) {
    	// 로그인한 고객의 예약 목록 계산
        long totalCount = reserversRepository.countByCustomerId(customerId);
        
        // 로그인한 고객의 예약 목록을
        // size(한 페이지당 조회할 항목의 수)를 사용해 페이지 계산
        int pages = (int) Math.ceil((double) totalCount / size);
        
        return pages;
    }
    
    
	// input : id(customer.id), customerId(reservers.id)
	// output : ReserversEntity(단건) or Null
	// @GetMapping("/reserve-detail-view")
    public ReserversEntity getReserversByIdCustomerId(int id, int customerId) {
    	
    	// DB SELECT - breakpoint
    	return reserversRepository.findByIdAndCustomerId(id, customerId);
    }
    
	
}
