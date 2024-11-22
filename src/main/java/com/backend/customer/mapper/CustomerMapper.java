package com.backend.customer.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper) <--> DB영역 
*/

@Mapper
public interface CustomerMapper {

	// input : X
	// output : List
	public List<Map<String, Object>> selectCustomerList();
}
