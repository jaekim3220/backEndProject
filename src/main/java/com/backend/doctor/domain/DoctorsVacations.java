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
public class DoctorsVacations {

	// 속성 : field
	private int id;
	private int doctorNum;
	private String title;
	private String vacationStart;
	private String vacationEnd;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
}
