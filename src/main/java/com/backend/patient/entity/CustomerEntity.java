package com.backend.patient.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper, XML) <--> DB영역 
*/

// Controller영역(Domain) 개념

@ToString
@AllArgsConstructor // 파라미터가 모두 있는 생성자
@NoArgsConstructor // 파라미터가 없는 생성자(기본)
@Builder // Setter 대용 + update를 위해 toBuilder 추가, 필드 수정 허용
@Getter // Getter
@Table(name = "customer") // 사용할 테이블 이름
@Entity // JPA 엔티티 객체(사용하려면 lombok이 필요) - jakarta
public class CustomerEntity {

	
	// 속성 : field
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // insert 이후 방금 들어간 id(PK) 값을 가져옴
	private int id;
	
	@Column(name = "customerId") // 카멜 케이스 설정
	private String customerId;
	
	private String password;
	
	private String salt;
	
	private String name;
	
	@Column(name = "birthDate") // 카멜 케이스 설정
	private String birthDate;
	
	private String email;
	
	@CreationTimestamp // 값이 null 이어도 insert 되는 시간으로 설정
	@Column(name = "createdAt") // 카멜 케이스 설정
	private LocalDateTime createdAt;
	
	@UpdateTimestamp // insert, update 일 경우 해당 시간으로 설정
	@Column(name = "updatedAt") // 카멜 케이스 설정
	private LocalDateTime updatedAt;
	
	
}
