package com.backend.doctor.bo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import com.backend.common.EncryptUtils;
import com.backend.doctor.domain.Doctors;
import com.backend.doctor.mapper.DoctorsMapper;
import com.backend.patient.PatientRestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper) <--> DB영역 
*/

// Service(BO)영역

@Slf4j
@Service // Spring Bean 등록
@RequiredArgsConstructor
public class DoctorsBO {


	// 생성자를 사용한 의존성 주입
	private final DoctorsMapper doctorsMapper;
	
	
	// input : doctorId
	// output : Doctors or null (단건)
	// @GetMapping("/is-duplicate-id") - 아이디 중복 확인
    public Map<String, Object> getDoctorsByDoctorId(String doctorId) {
    	
    	Map<String, Object> doctors = doctorsMapper.selectDoctorsByDoctorId(doctorId);
    	
    	// null 방지
    	return doctors != null ? doctors : new HashMap<>(); // null 방지 처리
    }
    
    
	// input : 7 parameters
	// output : Doctors 객체
	// @PostMapping("/sign-up") - 회원가입
	public boolean addDoctorsSignUp(String doctorId, String combinedPassword,String name, 
			String birthDate, String email, Integer department) {
		
		// DB INSERT 성공 시 1(영향을 받은 행 수) 반환
		int isExist = doctorsMapper.insertDoctorsSignUp(doctorId, combinedPassword, name, birthDate, email, department);
		
		// DB INSERT 성공 시 1 => true
		return isExist > 0 ? true : false;
		
	}
	
	
	// input : doctorId, password
	// output : Doctors(Map 형식) or null (단건)
	// @PostMapping("/sign-in") - 로그인
	public Map<String, Object> getDoctorsByDoctorIdAndPassword(String doctorId, String password) {
		
		/*
		Salt 사용 방법 - BO에서
		클라이언트에서 doctorId와 password를 송신
		서버에서 doctorId로 해당 사용자의 salt 값을 DB에서 수령
		서버는 수령한 password와 DB에서 가져온 salt를 사용해 비밀번호를 해싱하고, DB에 저장된 해시값과 비교하여 로그인 여부를 결정
		*/
		
		// DB 조회 - breakpoint (입력한 로그인 아이디랑 동일한 row 추출) - breakpoint
		Map<String, Object> doctors = doctorsMapper.selectDoctorsByDoctorId(doctorId);
		
        // doctorId가 존재하지 않으면 null 반환
        if (doctors == null || doctors.isEmpty()) {
            return null;
        }
        
	    // 저장된 combinedPassword(Salt + HashedPassword) 가져오기 - breakpoint 
	    String combinedPassword = (String) doctors.get("password");
	    log.info("##### 로그인 combinedPassword : {} #####", combinedPassword);
	    
	    // Salt와 HashedPassword 분리
	    String salt = combinedPassword.substring(0, 24); // Salt 길이 (Base64 기준 16bytes)
	    log.info("##### 로그인 salt : {} #####", salt);
	    String installedHashedPassword = combinedPassword.substring(24);
	    log.info("##### 로그인 hashedPassword : {} #####", installedHashedPassword);
	    
	    // 입력된 비밀번호와 Salt로 해싱
	    String inputHashedPassword = EncryptUtils.hashingSHA2(password, salt);
	    log.info("##### 로그인 inputHashedPassword : {} #####", inputHashedPassword);

	    
	    // 해싱된 비밀번호 비교
	    if (installedHashedPassword.equals(inputHashedPassword)) {
	        return doctors; // 비밀번호가 일치하면 CustomerEntity 반환
	    } else {
	        return null; // 비밀번호가 일치하지 않으면 null 반환
	    }
		
	}
	
	
	// input : department
	// output : 부서별 의사 목록
	// @GetMapping("") : localhost/hospital - 부서별 의사 목록
	public List<Doctors> getDoctorsByDepartment(Integer department) {
		return doctorsMapper.selectDoctorsByDepartment(department);
	}
	
	
	// input : doctors.id
	// output : doctors.id에 맞는 의사 데이터
	// @GetMapping("/{id}/doctor-reserve-view") : localhost//{doctors.id}/doctor-reserve-view
	public Doctors getDoctorsById(int id) {
		return doctorsMapper.selectDoctorsById(id);
	}
	
	
}
