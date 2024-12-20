package com.backend.patient.bo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.backend.common.EncryptUtils;
import com.backend.common.SeparateSaltPassword;
import com.backend.patient.entity.CustomerEntity;
import com.backend.patient.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper, XML) <--> DB영역 
*/

// Service(BO)영역

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerBO {
	
	
	// 어노테이션(Annotation)
	@Autowired // DI(Dependency Injection) : 필드를 사용한 의존성 주입
	private CustomerRepository customerRepository;
	
	@Autowired
	private SeparateSaltPassword separateSaltPassword;
	
	
	// input : customerId
	// output : CustomerEntity or null (단건)
	// @GetMapping("/is-duplicate-id") - 아이디 중복 확인
	public CustomerEntity getCustomerEntityByCustomerId(String customerId) {
		return customerRepository.findByCustomerId(customerId); // 메서드 생성
	}
	
	
	// input : 5개 + salt
	// output : CustomerEntity
	// @PostMapping("/sign-up")
	public CustomerEntity addCustomer(String customerId, String combinedPassword,
			String name, String birthDate, String email) {
		
		return customerRepository.save(
				CustomerEntity.builder()
				.customerId(customerId)
				.password(combinedPassword)
				.name(name)
				.birthDate(birthDate)
				.email(email)
				.build()); // Parameter를 Repository에 저장
	}
	
	
	// input : customerId, password(원본 비밀번호)
	// output : CustomerEntity
	// @PostMapping("/sign-in")
	public CustomerEntity getCustomerEntityByCustomerIdPassword(String customerId, String password) {
		
		/*
		Salt 사용 방법 - BO에서
		클라이언트에서 customerId와 password를 송신
		서버에서 customerId로 해당 사용자의 salt 값을 DB에서 수령
		서버는 수령한 password와 DB에서 가져온 salt를 사용해 비밀번호를 해싱하고, DB에 저장된 해시값과 비교하여 로그인 여부를 결정
		*/
		
		// DB 조회 - breakpoint (입력한 로그인 아이디랑 동일한 값)
		CustomerEntity customer = customerRepository.findByCustomerId(customerId);
	    if (customer == null) {
	        return null; // 일치하는 아이디, 비밀번호가 없으면 null 반환
	    }
	    
	    // 저장된 combinedPassword(Salt + HashedPassword) 가져오기
	    String combinedPassword = customer.getPassword();
	    log.info("combinedPassword : {}", combinedPassword);
	    
	    // Salt와 HashedPassword 분리
	    Map<String , Object> passwordMaps = separateSaltPassword.saparateSaltPassword(password, combinedPassword);
	    String installedHashedPassword = (String) passwordMaps.get("installedHashedPassword");
	    log.info("@@@@@ installedHashedPassword : {} @@@@@", installedHashedPassword);
	    String inputHashedPassword = (String) passwordMaps.get("inputHashedPassword");
	    log.info("@@@@@ inputHashedPassword : {} @@@@@", inputHashedPassword);
	    
		
	    // 해싱된 비밀번호 비교
	    if (installedHashedPassword.equals(inputHashedPassword)) {
	        return customer; // 비밀번호가 일치하면 CustomerEntity 반환
	    } else {
	        return null; // 비밀번호가 일치하지 않으면 null 반환
	    }
	}
	
}
