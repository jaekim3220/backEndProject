package com.backend.doctor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.backend.common.EncryptUtils;
import com.backend.common.HashingSaltPassword;
import com.backend.doctor.bo.DoctorUpdateBO;
import com.backend.doctor.bo.DoctorsBO;
import com.backend.doctor.bo.DoctorsVacationsBO;
import com.backend.doctor.bo.FullCalendarShowBO;
import com.backend.doctor.dto.DoctorsVacationsDTO;
import com.backend.doctor.dto.PatientEventsDTO;
import com.backend.patient.bo.ReserversBO;
import com.backend.patient.entity.ReserversEntity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper) <--> DB영역 
*/

/*
<Response 방법 - 서버 기준>
@Controller + return String => ViewResolver => HTML 파일 렌더링(Model) => HTML
@Controller + @ResponseBody return String => HTTPMessageConverter => HTML
@Controller + @ResponseBody return 객체(map, list) => HTTPMessageConverter => jackson => JSON
@RestController return map => JSON
Model은 HTML일 경우 사용(@ResponseBody일 경우 Model 사용 불가)
*/

@Slf4j
@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorRestController {

	
	// 생성자를 사용한 의존성 주입
	private final DoctorsBO doctorsBO;
	private final DoctorUpdateBO doctorUpdateBO;
	private final DoctorsVacationsBO doctorsVacationsBO;
	private final DoctorsVacationsDTO doctorsVacationsDTO;
	private final FullCalendarShowBO fullCalendarShowBO;
	private final ReserversBO reserversBO;
	private final PatientEventsDTO patientEventsDTO;
	private final HashingSaltPassword hashingSaltPassword;
	
    // 아이디 중복 확인
	@GetMapping("/is-duplicate-id")
	public Map<String, Object> isDuplicateId(
			// 필수 파라미터 불러오기1 : value, required 생략 (추천) - null이 아닌 column
			@RequestParam("doctorId") String doctorId) {
		
		
		// DB SELECT - breakpoint
		Map<String, Object> doctors = doctorsBO.getDoctorsByDoctorId(doctorId);

		
		// Response 응답값 - breakpoint
		// 중복인 경우/아닌 경우 구분 (Console 창의 SQL문에서 입력 값 return 확인) - breakpoint
        // 응답 값 처리 (doctorId가 존재하면 중복, 존재하지 않으면 중복 아님)
        boolean isDuplicateId = (doctors != null && !doctors.isEmpty());
		
		// 응답값 => Map => JSON String
		// 성공이면서 중복 : {"code":200, "is_duplicate_id":true}
		// 성공이면서 중복아님 : {"code":200, "is_duplicate_id":false}
		Map<String, Object> result = new HashMap<>();
		result.put("code", 200);
		result.put("is_duplicate_id", isDuplicateId);
		
		return result;
		
		
	}
	
	
	// form 태그(post)를 사용한 회원가입 기능
	@PostMapping("/sign-up")
	// localhost/doctor/sign-up
	public Map<String, Object> doctorSignUp(
			// 필수 파라미터 불러오기1 : value, required 생략 (추천) - null이 아닌 column
			@RequestParam("doctorId") String doctorId,
			@RequestParam("password") String password,
			@RequestParam("name") String name,
			@RequestParam("birthDate") String birthDate,
			@RequestParam("email") String email,
			@RequestParam("department") Integer department) {
		
		
		// parameter(password) 암호화 - breakPoint
		// SHA-2, Salt(난수) 알고리즘 결합
		String combinedPassword = hashingSaltPassword.hashingSaltPassword(password);
		log.info("@@@ 생성된 combinedPassword : {} @@@", combinedPassword);
		
		
		// DB INSERT - breakPoint
		// DoctorsBO에서 DB INSERT 성공 시 1(영향을 받은 행 수) 반환 => true 반환함
		boolean isExist = doctorsBO.addDoctorsSignUp(doctorId, combinedPassword, name, birthDate, email, department); 
		
		
		// Response(JSON String) - breakPoint
		Map<String, Object> result = new HashMap<>();
//		result.put("code", 200);
//		result.put("result", "회원가입 성공");
		if(isExist) {
			result.put("code", 200);
			result.put("result", "회원가입 성공");
		} else {
			result.put("code", 500);
			result.put("error_message", "회원가입에 실패했습니다.");
		}
		
		return result;
		
	}
	
	
	// form 태그(post)를 사용한 로그인 기능
	@PostMapping("/sign-in")
	// http:localhost/doctor/sign-in
	public Map<String, Object> doctorSignIn(
			// 필수 파라미터 불러오기1 : value, required 생략 (추천) - null이 아닌 column
			@RequestParam("doctorId") String doctorId,
			@RequestParam("password") String password,
			HttpServletRequest request) {
		
		/*
		Salt 사용 방법 - BO에서
		클라이언트에서 doctorId와 password를 송신
		서버에서 doctorId로 해당 사용자의 salt 값을 DB에서 수령
		서버는 수령한 password와 DB에서 가져온 salt를 사용해 비밀번호를 해싱하고, DB에 저장된 해시값과 비교하여 로그인 여부를 결정
		*/
		
		
		// DB SELECT - breakPoint
		Map<String, Object> doctor = doctorsBO.getDoctorsByDoctorIdAndPassword(doctorId, password);
		
		
		// Response(JSON String) - breakPoint
		// "{"code" : 200, "result" : "로그인 성공"}"
		Map<String, Object> result = new HashMap<>();
		
		if(doctor != null) { // 입력한 doctorId, password 존재 : 로그인 성공
			// 로그인 정보를 session에 저장
			HttpSession session = request.getSession();
	        session.setAttribute("doctorId", doctor.get("id"));
	        session.setAttribute("doctorLoginId", doctor.get("doctorId"));
	        session.setAttribute("doctorName", doctor.get("name"));

			result.put("code", 200);
			result.put("result", "로그인 성공");
		} else { // 로그인 실패
	        result.put("code", 403);
	        result.put("error_message", "로그인 실패 : 아이디 또는 비밀번호를 확인하세요.");
	    }
		
	    return result;
	    
	}
	
	
	// 환자 영역 `reservsers`, 의사 영역`reservings` update
	@PostMapping("/statusUpdate")
	public Map<String, Object> statusUpdate(
			// 필수 파라미터 불러오기1 : value, required 생략 (추천) - null이 아닌 column
			@RequestParam("id") int id,
			@RequestParam("customerId") int customerId,
			@RequestParam("status") String status,
			@RequestParam("treatment") String treatment,
			@RequestParam("memo") String memo,
			Model model, HttpSession session) {
		
		// 로그인 여부 검사 - breakpoint
		Integer doctorId = (Integer) session.getAttribute("doctorId");
		log.info("##### doctorId : {} #####", doctorId);
		
		Map<String, Object> result = new HashMap<>();
		if(doctorId == null) {
			result.put("code", 403);
			result.put("error_message", "로그인 후 사용이 가능합니다.");
			return result;
		}
		
		
		// DB Update - breakpoint
		// 의사의 `reservings` 테이블, 환자의 `reservers` 테이블 업데이트
		// int id, int doctorId, int customerId, String memo, String status, String treatment
		int output = doctorUpdateBO.doctorUpdateBO(id, doctorId, customerId, memo, status, treatment);
		
		
		// Response(응답)
		if(output > 0) {
			result.put("code", 200);
			result.put("result", "예약 현황 업데이트 성공");			
		} else {
			result.put("code", 500);
			result.put("result", "예약 현황 업데이트 실패");						
		}
		
		return result;
	}
	
	
	// 의사 일정 추가
	@PostMapping("/calendar-plan-insert")
	@ResponseBody // JSON 응답을 보냄
	// http:localhost/doctor/calendar-plan-insert
	public Map<String, Object> calandarPlanInsert(
			// 필수 파라미터 불러오기1 : value, required 생략 (추천) - null이 아닌 column
			@RequestParam("title") String title,
			@RequestParam("vacationStart") String vacationStart, 
			@RequestParam("vacationEnd") String vacationEnd,			
			HttpSession session) {
		// 로그인 여부 검사 - breakpoint
		Integer doctorNum = (Integer) session.getAttribute("doctorId");
		log.info("##### doctorNum : {} #####", doctorNum);
		
		Map<String, Object> result = new HashMap<>();
		if(doctorNum == null) {
			result.put("code", 403);
			result.put("error_message", "로그인 후 사용이 가능합니다.");
			return result;
		}
		
		
		// DB INSERT (Entity 사용), 성공한 행 수 - breakpoint
		// `vacations` 테이블
		int rowCount = doctorsVacationsBO.addDoctorsVacations(doctorNum, title, vacationStart, vacationEnd);
		
		
		// Response(응답값) - breakpoint
		// Dictionary 형태
		// Ajax의 응답은 String => JQuery의 함수가 JSON임을 알면
		// => Dictionary 형식으로 변경
		// "{"code" : 200, "result" : "일정 신청 성공"}"
		if(rowCount > 0) {
			result.put("code", 200);
			result.put("result", "일정 신청 성공");
		} else {
			result.put("code", 500);
			result.put("result", "일정 신청 실패");
		}
		
		return result;

	}
	
	
    @PostMapping("/calendar-plan-show")
    public List<Map<String, Object>> makeEvents(
    		// 비필수 파라미터 불러오기2 : 기본값 설정 value, required 입력 (추천) - URL에서 추출
    		@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "vacationStart", required = false) String vacationStart, 
			@RequestParam(value = "vacationEnd", required = false) String vacationEnd,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "description", required = false) String description,
			HttpSession session) {
		// 로그인 여부 검사 - breakpoint
        Integer doctorNum = (Integer) session.getAttribute("doctorId");
        if (doctorNum == null) {
            throw new RuntimeException("Unauthorized access");
        }

        
        // DB SELECT도 FullCalendarShowBO에서 진행
        
		
		// DTO를 사용해 변환한 `vacations` 데이터 load
        return fullCalendarShowBO.doctorsAndPatientsSchedule(doctorNum);
        
    }

    
	// 의사 일정 수정(업데이트)
	@PostMapping("/calendar-plan-update")
	public Map<String, Object> calandarPlanUpdate(
			// 필수 파라미터 불러오기1 : value, required 생략 (추천) - null이 아닌 column
			@RequestParam("id") int id,
			@RequestParam("title") String title,
			@RequestParam("vacationStart") String vacationStart, 
			@RequestParam("vacationEnd") String vacationEnd,		
			HttpSession session) {
		
		// doctorNum 추출
		Integer doctorNum = (Integer) session.getAttribute("doctorId");
		log.info("##### doctorNum : {} #####", doctorNum);

		Map<String, Object> result = new HashMap<>();
		if(doctorNum == null) {
			result.put("code", 403);
			result.put("error_message", "로그인 후 사용이 가능합니다.");
			return result;
		}
		
		
		// DB Update - breakpoint
		int rowCount = doctorsVacationsBO.updateDoctorsVacations(id, doctorNum, title, vacationStart, vacationEnd);
		
		
		// Response - breakpoint
		if(rowCount > 0) {
			result.put("code", 200);
			result.put("result", "일정 변경에 성공했습니다.");
		} else {
			result.put("code", 500);
			result.put("error_message", "일정 변경에 실패했습니다. 관리자한테 문의해주세요.");			
		}
		
		
		return result;
		
	}
	
	
	// 의사 일정 삭제(DELETE)
	@PostMapping("/calendar-plan-delete")
	public Map<String, Object> calandarPlanDelete(
			// 필수 파라미터 불러오기1 : value, required 생략 (추천) - null이 아닌 column
			@RequestParam("id") int id,		
			HttpSession session) {
		
		// doctorNum 추출
		Integer doctorNum = (Integer) session.getAttribute("doctorId");
		log.info("##### doctorNum : {} #####", doctorNum);

		Map<String, Object> result = new HashMap<>();
		if(doctorNum == null) {
			result.put("code", 403);
			result.put("error_message", "로그인 후 사용이 가능합니다.");
			return result;
		}
		
		
		// DB DELETE - breakpoint
		int rowCount = doctorsVacationsBO.removeDoctorsVacations(id, doctorNum);
		
		
		// Response - breakpoint
		if(rowCount > 0) {
			result.put("code", 200);
			result.put("result", "일정 변경에 성공했습니다.");
		} else {
			result.put("code", 500);
			result.put("error_message", "일정 변경에 실패했습니다. 관리자한테 문의해주세요.");			
		}
		
		
		return result;
		
	}
	
	
	// 달력에서 환자 데이터 업데이트
	@PostMapping("/calendar-patient-update")
	public Map<String, Object> calandarPatientUpdate(
			// 필수 파라미터 불러오기1 : value, required 생략 (추천) - null이 아닌 column
			@RequestParam("id") int id, 
			@RequestParam("status") String status,		
			HttpSession session) {
		
		// doctorNum 추출
		Integer doctorNum = (Integer) session.getAttribute("doctorId");
		log.info("##### doctorNum : {} #####", doctorNum);

		Map<String, Object> result = new HashMap<>();
		if(doctorNum == null) {
			result.put("code", 403);
			result.put("error_message", "로그인 후 사용이 가능합니다.");
			return result;
		}
		
		
		// DB Update - breakpoint
		int rowCount = doctorUpdateBO.updatePatientCalendar(id, status);
		
		
		// Response - breakpoint
		if(rowCount > 0) {
			result.put("code", 200);
			result.put("result", "환자 예약 상태 변경.");
		} else {
			result.put("code", 500);
			result.put("error_message", "환자 예약 상태 변경에 실패했습니다. 관리자한테 문의해주세요.");			
		}
		
		
		return result;
		
	}
	
}
