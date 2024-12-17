package com.backend.patient;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.common.EncryptUtils;
import com.backend.common.HashingSaltPassword;
import com.backend.patient.bo.CustomerBO;
import com.backend.patient.bo.PatientDeleteBO;
import com.backend.patient.bo.PatientInsertBO;
import com.backend.patient.bo.PatientReservingsBO;
import com.backend.patient.bo.PatientUpdateBO;
import com.backend.patient.bo.PaymentsBO;
import com.backend.patient.bo.ReserversBO;
import com.backend.patient.entity.CustomerEntity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/*
DB연동 : View영역 <--> Controller영역(Domain) <--> Service(BO)영역 <--> Repository영역(Mapper, XML) <--> DB영역 
*/

// View영역

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
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientRestController {

	// 어노테이션(Annotation)
	@Autowired // DI(Dependency Injection) : 필드를 사용한 의존성 주입
	private CustomerBO customerBO;
	
	@Autowired
	private ReserversBO reserversBO;
	
	
	// 생성자를 사용한 의존성 주입
	private final PatientReservingsBO patientReservingsBO;
	private final PatientUpdateBO patientUpdateBO;
	private final PatientInsertBO patientInsertBO;
	private final PatientDeleteBO patientDeleteBO;
	private final HashingSaltPassword hashingSaltPassword;
	private final PaymentsBO paymentsBO;
	
	
	// 아이디 중복 확인
	// 단순한 select문으로 JPA(Object Relational Mapping) 사용
	@GetMapping("/is-duplicate-id")
	public Map<String, Object> isDuplicateId(
			// 필수 파라미터 불러오기1 : value, required 생략 (추천) - null이 아닌 column
			@RequestParam("customerId") String customerId) {
		
		
		// DB SELECT - breakpoint
		CustomerEntity customer = customerBO.getCustomerEntityByCustomerId(customerId);
		
		
		// 중복인 경우/아닌 경우 구분 (Console 창의 SQL문에서 입력 값 return 확인) - breakpoint
		boolean isDuplicateId = false; // false = 중복 아님
		if(customer != null) { // 기존 값이 존재 = 중복
			isDuplicateId = true;
		}
		
		
		// 응답 값 - breakpoint
		// 응답값 => Map => JSON String
		// {"code":200, "is_duplicate_id":true}
		Map<String, Object> result = new HashMap<>();
		result.put("code", 200);
		result.put("is_duplicate_id", isDuplicateId);
		
		return result;
	}
	
	
	// form 태그(post)를 사용한 회원가입 기능
	@PostMapping("/sign-up")
	public Map<String, Object> patientSignUp(
			// 필수 파라미터 불러오기1 : value, required 생략 (추천) - null이 아닌 column
			@RequestParam("customerId") String customerId,
			@RequestParam("password") String password,
			@RequestParam("name") String name,
			@RequestParam("birthDate") String birthDate,
			@RequestParam("email") String email) {
		
		
		// parameter(비밀번호) 암호화 - breakPoint
		String combinedPassword = hashingSaltPassword.hashingSaltPassword(password);
		log.info("@@@ 생성된 combinedPassword : {} @@@", combinedPassword);
		
		// DB INSERT - breakPoint
		CustomerEntity customer = customerBO.addCustomer(customerId, combinedPassword, name, birthDate, email);
		
		
		// Response(JSON String) - breakPoint
		// {"code" : 200, "result" : "회원가입 성공"}
		Map<String, Object> result = new HashMap<>();
		if(customer != null) {
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
	public Map<String, Object> patientSignIn(
			// 필수 파라미터 불러오기1 : value, required 생략 (추천) - null이 아닌 column
			@RequestParam("customerId") String customerId,
			@RequestParam("password") String password,
			HttpServletRequest request) { // Salt는 보안 이슈로 @RequestParam으로 추출 불가
		
		/*
		Salt 사용 방법 - BO에서
		클라이언트에서 customerId와 password를 송신
		서버에서 customerId로 해당 사용자의 salt 값을 DB에서 수령
		서버는 수령한 password와 DB에서 가져온 salt를 사용해 비밀번호를 해싱하고, DB에 저장된 해시값과 비교하여 로그인 여부를 결정
		*/
		
		
		// DB SELECT - breakPoint
		CustomerEntity customer = customerBO.getCustomerEntityByCustomerIdPassword(customerId, password);
        
        
		// Response(JSON String) - breakPoint
		// {"code" : 200, "result" : "로그인 성공"}
		Map<String, Object> result = new HashMap<>();
	    
		if (customer != null) { // 로그인 성공
	        HttpSession session = request.getSession();
	        session.setAttribute("customerId", customer.getId());
	        session.setAttribute("customerLoginId", customer.getCustomerId());
	        session.setAttribute("customerName", customer.getName());

	        result.put("code", 200);
	        result.put("result", "로그인 성공");
	    } else { // 로그인 실패
	        result.put("code", 403);
	        result.put("error_message", "로그인 실패 : 아이디 또는 비밀번호를 확인하세요.");
	    }
	    return result;
		
	}
	
	
	/**
	 * 예약하기 기능
	 * @param doctorNum
	 * @param title
	 * @param description
	 * @param visitDate
	 * @param file
	 * @param session
	 * @return
	 */
	@PostMapping("/reserving")
	public Map<String, Object> patientCreateReserve(
			// 필수 파라미터 불러오기1 : value, required 생략 (추천) - null이 아닌 column
			@RequestParam("doctorNum") int doctorNum,
			@RequestParam("title") String title,
			@RequestParam("description") String description,
			@RequestParam("visitDate") String visitDate,
			// 비필수 파라미터 불러오기2 : 기본값 설정 (추천)
			@RequestParam(value = "file", required = false) MultipartFile file,
			HttpSession session) {
		
		
		// 고객 고유 번호, 로그인 아이디 추출(비로그인 방지) - breakpoint
		// session => customerId(DB), customerLoginId(BD)
		// session에 담을 변수(parameter)가 기억나지 않을 경우 @PostMapping("/sign-in") 참고
		int customerId = (int) session.getAttribute("customerId"); // customer.id
		String customerLoginId = (String) session.getAttribute("customerLoginId"); // customer.customerId
		String customerName = (String) session.getAttribute("customerName"); // customer.name
		
		
		// DB INSERT (Entity 사용), 성공한 행 수 - breakpoint
		// `reservings` 테이블
		int rowCount = patientInsertBO.patientInsertBO(customerId, customerLoginId, customerName, doctorNum, title, description, visitDate, file);
		
		
		// Response(응답값) - breakpoint
		// Dictionary 형태
		// Ajax의 응답은 String => JQuery의 함수가 JSON임을 알면
		// => Dictionary 형식으로 변경
		// "{"code" : 200, "result" : "예약 신청 성공"}"
		Map<String, Object> result = new HashMap<>();
		if(rowCount > 0) {
			result.put("code", 200);
			result.put("result", "예약 신청 성공");
		} else {
			result.put("code", 500);
			result.put("error_message", "예약 신청에 실패했습니다. 관리자에게 문의하세요.");
		}
		
		return result;
	}
	
	
	/**
	 * 예약 수정
	 * @param id
	 * @param doctorNum
	 * @param title
	 * @param description
	 * @param visitDate
	 * @param file
	 * @param session
	 * @return
	 */
	@PutMapping("/update")
	public Map<String, Object> reservesUpdate(
			// 필수 파라미터 불러오기1 : value, required 생략 (추천) - null이 아닌 column
			@RequestParam("id") int id,
			@RequestParam("doctorNum") int doctorNum,
			@RequestParam("title") String title,
			@RequestParam("description") String description,
			@RequestParam("visitDate") String visitDate,
			// 비필수 파라미터 불러오기2 : 기본값 설정 value, required 입력 (추천)
			@RequestParam(value = "file", required = false) MultipartFile file,
			HttpSession session) {
		/* id, customerId, doctorNum, title, description, visitDate, imagePath */
		
		// 고객 로그인 아이디 추출(이미지 업로드용) - breakpoint
		// session에 담을 변수(parameter)가 기억나지 않을 경우 @PostMapping("/sign-in") 참고
		Integer customerId = (Integer) session.getAttribute("customerId"); // customer.id (@RequestParam 대신 session 사용)
		String customerLoginId = (String) session.getAttribute("customerLoginId"); // customer.customerId
		String customerName = (String) session.getAttribute("customerName"); // customer.name
		log.info("!!!!! customerId : {}, customerLoginId : {} !!!!!", customerId, customerLoginId);
		
		Map<String, Object> result = new HashMap<>();
		if(customerId == null || customerLoginId == null) {
			result.put("code", 403);
			result.put("error_message", "로그인 후 사용 가능합니다.");
		}
		
		// DB Update + 파일 업로드(옵션) - breakpoint
		// `reservings` 테이블
		patientUpdateBO.patientUpdateBO(id, customerId, customerLoginId, customerName, doctorNum, title, description, visitDate, file);
		
		
		// Response(응답값) - breakpoint
		// Dictionary 형태
		// Ajax의 응답은 String => JQuery의 함수가 JSON임을 알면
		// => Dictionary 형식으로 변경
		// "{"code" : 200, "result" : "예약 수정 성공"}"
		result.put("code", 200);
		result.put("result", "예약 수정 성공");
		
		return result;
	}
	
	
	/**
	 * 예약 목록 삭제
	 * @param id
	 * @param session
	 * @return
	 */
	@DeleteMapping("/delete")
	public Map<String, Object> reservesDelete(
			// 필수 파라미터 불러오기1 : value, required 생략 (추천) - null이 아닌 column
			@RequestParam("id") int id,
			HttpSession session) {

		// 고객 로그인 아이디 추출(로그인/비로그인 구분) - breakpoint
		// session에 담을 변수(parameter)가 기억나지 않을 경우 @PostMapping("/sign-in") 참고
		Integer customerId = (Integer) session.getAttribute("customerId");
		Map<String, Object> result = new HashMap<>();
		if(customerId == null) {
			result.put("code", 403);
			result.put("error_message", "로그인 후 예약 목록 삭제가 가능합니다.");
		}
		
		
		// DB DELETE + 파일 업로드(삭제) - breakpoint
		patientDeleteBO.patientDeleteBO(id, customerId);
		
		
		
		// Response(응답값) - breakpoint
		// Dictionary 형태
		// Ajax의 응답은 String => JQuery의 함수가 JSON임을 알면
		// => Dictionary 형식으로 변경
		// "{"code" : 200, "result" : "예약 내역 삭제 성공"}"
		result.put("code", 200);
		result.put("result", "예약 내역 삭제 성공");
		
		return result;
		
	}
	
	
	// 결제 내역 DB INSERT
	@PostMapping("/reserve-payment")
	public Map<String, Object> reservePayment(
			// 필수 파라미터 불러오기1 : value, required 생략 (추천) - null이 아닌 column
			@RequestParam("doctorNum") int doctorNum,
			@RequestParam("customerId") int customerId,
			@RequestParam("amount") int amount,
			@RequestParam("merchantUid") String merchantUid,
			@RequestParam("impUid") String impUid,
			@RequestParam("customerName") String customerName,
			@RequestParam("customerEmail") String customerEmail,
			@RequestParam("customerPostcode") String customerPostcode,
			HttpSession session) {
		
		// session을 사용해 환자 고유 번호 추출 - breakpoint
		Integer buyerNum = (Integer) session.getAttribute("customerId");
		
		
		// DB INSERT - breakpoint
		int rowCount = paymentsBO.addPaymentsEntity(doctorNum, customerId, amount, merchantUid, impUid, customerName, customerEmail, customerPostcode);
		
		Map<String, Object> result = new HashMap<>();
		if(rowCount > 0) {
			result.put("code", 200);
			result.put("result", "결제 내역을 DB에 성공적으로 저장했습니다.");			
		} else {
			result.put("code", 500);
			result.put("error_message", "결제 내역을 DB에 저장하지 못 했습니다.");
		}
		
		return result;
	}
	
	
	// 결제 내역 취소 => payments Update
	@PostMapping("/payments-cancel")
	public Map<String, Object> paymentsCancel(
			@RequestBody Map<String, Object> request,
			HttpSession session) {
		
		// session을 사용해 환자 고유 번호 추출(@PostMapping("/sign-in") 참고) - breakpoint 
		Integer customerId = (Integer) session.getAttribute("customerId");
		
		Map<String, Object> result = new HashMap<>();
		if(customerId == null) {
			result.put("code", 403);
			result.put("error_message", "로그인 후 사용 가능한 기능입니다. 로그인 해주세요.");
			return result;
		}
		
		try {
			// 요청 본문에서 id 값 추출
			// {id=29, merchant_uid=20241216-ec7dc5c68d614b778250a69c1d6fac44, cancel_request_amount=100, reason=날짜 변경}
			Integer id = Integer.parseInt((String) request.get("id"));
			String cancelReason = (String) request.get("cancelReason");
			
			// DB UPDATE  - breakpoint
			// payments.isCanceled column을 'canceled'로 update
			int rowCount = paymentsBO.cancelPayment(id, customerId, cancelReason);

			// Response(응답 값) - breakpoint
			if(rowCount > 0) {
				result.put("code", 200);
				result.put("result", "결제 내역 삭제 성공.");			
			} else {
				result.put("code", 500);
				result.put("result", "결제 내역 삭제 실패. 관리자한테 문의하세요.");
			}
		} catch (NumberFormatException e) {
			result.put("code", 400);
	        result.put("error_message", "잘못된 데이터 형식입니다. ID를 찾지 못했습니다.");
		} catch (Exception e) {
	        result.put("code", 500);
	        result.put("error_message", "서버 오류가 발생했습니다. 관리자에게 문의해주세요.");
	    }
		
		
		return result;
		
	}
	
	
	
}
