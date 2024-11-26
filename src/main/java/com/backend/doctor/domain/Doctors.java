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
public class Doctors {

	// 속성 : field
	private int id;
	private String doctorId;
	private String password;
	private String name;
	private String birthDate;
	private String email;
	private String department;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
