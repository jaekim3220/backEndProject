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
public class PatientReservers {
	
	
	// 속성 : field
	private int id;
	private int customerId;
	private int doctorNum;
	private String title;
	private String description;
	private String visitDate;
	private String imagePath;
	private String status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

}
