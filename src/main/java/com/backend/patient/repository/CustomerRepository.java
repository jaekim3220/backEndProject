package com.backend.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.patient.entity.CustomerEntity;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper, XML) <--> DB영역 
*/

// Repository영역(Mapper, XML) 개념

public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer> {

}
