package com.backend.doctor.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.GetMapping;

import com.backend.doctor.domain.DoctorsReservings;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper, XML) <--> DB영역 
*/

// Repository영역(Mapper, XML)

@Mapper // mybatis 라이브러리
public interface DoctorsReservingsMapper {

	
	// input : 의사 고유 번호(doctors.id) => doctorId
	// output : List<Map<String, Object>>
	// @GetMapping("/today-plan-view")
	public List<DoctorsReservings> selectReservingsByDoctorId(
			@Param("doctorId") Integer doctorId,
			@Param("standardVisitDate") String standardVisitDate,
	        @Param("direction") String direction,
	        @Param("limit") int limit);
	
	/* 페이징 버튼 말소 기능을 위한 visitDate(방문 날짜) 추출 메서드 */
	// 로그인한 의사 번호와 동일한 데이터만 추출
	// 데이터 정렬
	public String selectReservingsdBydoctorNumberAsSort(
			@Param("doctorId") int doctorId,
			@Param("sort") String sort);
	
	
	// input : int id, Integer doctorNumber
	// output : DoctorsReservings or null(단건)
	// @GetMapping("/patient-status-view")
	public DoctorsReservings selectByIdAndDoctorNumber(
			@Param("id") int id, 
			@Param("doctorNumber") Integer doctorNumber);
	
	
	// input : 
	// int id, int doctorNumber, int customerId, String memo, String status, String treatment
	// output : int
	// @PostMapping("/statusUpdate")
	public int updateReservingsByIdDoctorNumberDoctorId(
			@Param("id") int id, @Param("doctorId") int doctorId,
			@Param("customerId") int customerId, @Param("memo") String memo,
			@Param("status") String status, @Param("treatment") String treatment);
	
	
	// input : int id, String status
	// output : X
	// @PostMapping("/calendar-patient-update")
	public int updateReservingsByIdAndStatus(@Param("id") int id, @Param("status") String status);
	
	
}
