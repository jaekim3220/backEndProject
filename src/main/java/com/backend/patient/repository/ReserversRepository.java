package com.backend.patient.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.patient.entity.ReserversEntity;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper, XML) <--> DB영역 
*/

// Repository영역(Mapper, XML) 개념

public interface ReserversRepository extends JpaRepository<ReserversEntity, Integer> {
	
	
	// 메서드 생성
	
	// input : customerId(int)
	// output : List<ReserversEntity>
	// @GetMapping("/reserve-list-view")
	List<ReserversEntity> findByCustomerId(int customerId);
	
}
