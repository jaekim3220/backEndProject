package com.backend.customer.domain;

import java.time.LocalDateTime;

import lombok.Data;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper) <--> DB영역 
*/

@Data
public class Customer {
	private int id;
	private String customerId;
	private String password;
	private String name;
	private String birthDate;
	private String email;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
