package com.backend.patient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.backend.doctor.bo.DoctorsBO;
import com.backend.doctor.domain.Doctors;
import com.backend.patient.bo.ReserversBO;
import com.backend.patient.entity.ReserversEntity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
@Controller
@RequestMapping("/patient")
public class PatientController {
	
	// 어노테이션(Annotation)
	@Autowired // DI(Dependency Injection) : 필드를 사용한 의존성 주입
	private DoctorsBO doctorsBO;
	
	@Autowired
	private ReserversBO reserversBO;

	
	/**
	 * 환자(고객) 회원가입 화면
	 * @return
	 */
	@GetMapping("/sign-up-view")
	// http:localhost/patient/sign-up-view
	public String signUpView() {
		return "patient/patientSignUp";
	}
	
	
	// 환자(고객) 로그인 화면
	@GetMapping("/sign-in-view")
	// http:localhost/patient/sign-in-view
	public String signInView() {
		return "patient/patientSignIn";
	}
	
	
	// 예약 페이지 화면
	@GetMapping("/{id}/reserve-view")
	// http:localhost/patient/{doctors.id}/reserve-view
	// URL 중간에 parameter가 삽입되어 @RequestParam 대신 @PathVariable 사용
	public String reserveCreateView(@PathVariable("id") int id, Model model) {

		// 특정 의사 데이터 추출
		Doctors doctor = doctorsBO.getDoctorsById(id);
		
		// Model에 데이터 삽입
		model.addAttribute("doctorId", doctor.getId());
		
		// 달력에 금일, 최소/최대 예약 날짜 설정을 
		// Thymeleaf 문법으로 구현하기 위해 Model에 값을 할당 
	    LocalDateTime now = LocalDateTime.now();
	    LocalDateTime tomorrow = now.plusDays(1);
	    LocalDateTime nextMonth = now.plusMonths(1);
        
        DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        model.addAttribute("currentDate", now.format(dateformat)); // 오늘 날짜 00:00
        model.addAttribute("minDate", tomorrow.format(dateformat)); // 내일 날짜 00:00
        model.addAttribute("maxDate", nextMonth.format(dateformat)); // 한 달 뒤 날짜 23:59
		
		return "patient/reserveCreate";
	}
	
	
	// 예약 목록 페이지 화면
	@GetMapping("/reserve-list-view")
	// localhost/patient/reserve-list-view
	public String reserveListView(Model model, HttpSession session,
			// 비필수 파라미터 불러오기2 : 기본값 설정 (추천)
	        @RequestParam(name = "page", defaultValue = "0") int page,
	        @RequestParam(name = "size", defaultValue = "3") int size,
	        HttpServletRequest request) { // HttpServletRequest : 전체 url을 보기위한 패키지
		
	    // 전체 URL logging
	    String fullURL = request.getRequestURL().toString(); // 기본 URL
	    String queryString = request.getQueryString(); // 쿼리 파라미터
	    if (queryString != null) {
	        fullURL += "?" + queryString; // URL + 쿼리 파라미터
	    }
	    log.info("!!!!! 전체 URL : {} !!!!!", fullURL);
	    log.info("!!!!! 페이징에 사용될 파라미터 - page: {}, size: {} !!!!!", page, size);
	    
		
		// 로그인 여부 확인(권한 검사) - breakpoint
		Integer customerId = (Integer) session.getAttribute("customerId");
		if(customerId == null) {
			// 로그인 페이지로 이동
			return "redirect:/patient/sign-in-view";
		}
		
		// List<ReserversEntity> reserveList = reserversBO.getreserveListBycustomerId(customerId);
		// DB SELECT => 본인(로그인한 사람)이 쓴 글을 session을 통해 수령 - breakpoint
		// customerId와 일치하는 row 데이터를 페이지 번호, 페이지 크기를 설정 후 SELECT
		List<ReserversEntity> reserveList = reserversBO.getreserveListBycustomerId(customerId, page, size);
		// 전체 페이지 수 계산 = {로그인한 환자의 예약 목록 수 / 3(페이지에 보여줄 목록 수)}
		int totalPages = reserversBO.getTotalPagesByCustomerId(customerId, size);
		
		
		// Model에 데이터 삽입 - breakpoint
		model.addAttribute("reserveList", reserveList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
		
		
		return "patient/reserveList";
	}
	
	
	// 예약 상세 페이지 화면
	// /patient/{reservers.id}/reserve-detail
	// 확인을 위한 임시 @GetMapping : 의사의 고유 id 값을 받아서 해당 의사 예약 페이지로 이동할 것
	@GetMapping("/reserve-detail-view")
	// localhost/patient/reserve-detail-view
	public String reserveDetailView() {
		return "patient/reserveDetail";
	}
	
	
	// 로그아웃 기능 구현
	// PatientRestController에서 생성한 session 초기화
	@GetMapping("/sign-out")
	public String patientSignOut(HttpSession session) {
		// session 내용 초기화
		session.removeAttribute("customerId");
		session.removeAttribute("customerLoginId");
		session.removeAttribute("customerName");
		
		// 로그인 페이지로 이동(redirect)
		return "redirect:/patient/sign-in-view";
	}
	
	
}
