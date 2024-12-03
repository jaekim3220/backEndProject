package com.backend.doctor.domain;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper) <--> DB영역 
*/

// Controller영역(Domain)

@Getter
@Setter
public class DoctorsReservings {
	
	// 속성 : feild
	private int id;
	private int doctorNumber; // 의사 고유 id(doctors.id)
	private int customerId; // 환자 고유 id(customer.id)
	private String customerName; // 환자 이름(customer.name)
	private String title; // 예약 제목
	private String description; // 예약 내용(증상 및 상태)
	private String visitDate; // 내원 날짜
	private String imagePath; // 저장한 이미지
	private String memo; // 의사가 적는 환자 상태(reservings.memo)
	private String treatment; // 진행 상태("진료 대기" = default, "진료 중", "진료 완료")
	private String status; // 고객 예약 상태("예약대기" = default, "예약확정", "예약불가")
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
}
