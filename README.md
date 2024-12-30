# VIP 병동 예약
[전체 포트폴리오(Notion) URL](https://rift-asiago-ddb.notion.site/VIP-1626b455840180d68ac9c2257a018a84?pvs=4)

## 1. 프로젝트 개요

### 프로젝트 생성 이유
대학병원 웹페이지의 사진, 선택 창, 예약, 결제 등 다양한 기능을 접하고 제가 공부한 JAVA, Spring Boot, JQuery와 외부 API 기능을 실제로 구현하고 더 깊이 이해하고자 이 프로젝트를 시작하게 되었습니다.

### 기간
2024-11-16 ~ 2024-12-17
### 참여 인원
1명(김재현 - 본인)
### 역할
기획, URL 설계, DB 설계(MySQL), 마크업 생성(HTML5, JavaScript, CSS, Bootstrap), 기능 구현(JAVA, Spring Boot), 형상관리(Github, Source Tree), 테스트, 배포(AWS)

### 주요 기능
1. **FullCalendar API**와 DB를 연동하여 **events, eventClick, eventDidMount** 기능으로 환자와 의사의 데이터를 실시간으로 조회하고 수정할 수 있게 구현했습니다.
2. **PortOne API**와 DB를 연동하고, **UUID Class**로 생성한 **merchant_uid**(주문번호)를 통해 결제 기능을 구현했습니다.그리고 **RestTemplate** 클래스로 HTTP 통신을 진행, PortOne API 액세스 토큰을 발급 받아 결제 취소 기능을 구현했습니다.
3. 병원 웹 페이지에서 예약 서비스의 CRUD 기능을 구현했습니다.

## 2. 기술 스펙
**Language**
- JAVA , JavaScript , HTML5, CSS, SQL

**Framework & Libraries**
- **Backend :** Spring Boot, MyBatis, JPA(Java Persistence API), Lombok
- **Frontend :** Bootstrap, jQuery, FullCalendar API

**Tool**
- GitHub, Gradle, MySQL, AWS EC2

**ETC**
- Thymeleaf, PortOne API(결제 및 결제 취소 기능)

## 3. 기획서

### 1. 기획 공통 영역입니다.
![기획 공통 PNG](https://github.com/user-attachments/assets/d95fec92-dc44-46c5-baab-eec9f8ee3464)

### 2. 회원가입, 로그인, 로그인 후 화면 영역, 결제 기능 영역입니다.
![기획 공통 PNG](https://github.com/user-attachments/assets/46336261-be7f-448f-bee4-848f89bc49ba)

### 3. 예약 내역 업데이트, FullCalendar API 영역입니다.
![기획2 PNG](https://github.com/user-attachments/assets/18868507-b4e0-4fdb-9f72-9b9ccaed4b74)

### 4. 기획서(Figma) 전체 주소입니다.
[기획서(Figma) 원본 주소](https://www.figma.com/design/mzkJrSmCkVm33gUvMuXXXz/VIP-HOSPITAL?node-id=0-1&t=tTLhwN1UOiPZqcbX-1)

## 4. DB 설계

### ERD 다이어그램
![DbErd](https://github.com/user-attachments/assets/7c502e62-66b5-40af-87cc-f6b8f4d41b34)

## 5. 기능 명세서

### 1. FullCalendar

- **사용 기술**
    - **DTO(Data Transfer Object)**를 사용해 List 형식으로 가져온 데이터를 Map 형식으로 변환.
    - FullCalendar API의 **events, eventClick, customButtons** 기능을 사용한 CRUD 구현.
    - AJAX를 사용한 서버와의 비동기식 데이터 교환.
    - Bootstrap Modal을 활용한 사용자 인터페이스 구성.
- **구현 내용**
    - **DTO**를 사용해 DB에서 List 형식으로 가져온  의사의 일정(시작날짜, 종료날짜), 환자의 예약 날짜 데이터를 각각 FullCalendar 화면에 나타낼 수 있도록 Map 형식으로 변환했습니다.
  ```
  public List<Map<String, Object>> vacationsToFullCalendarFormat(List<Map<String, Object>> vacations) {
    return vacations.stream()
        .distinct() // 중복 제거
        .map(vacation -> { // 각각의 vacation 데이터를 변환
          Map<String, Object> doctorEvent = new HashMap<>(); // FullCalendar의 event
          doctorEvent .put("id", vacation.get("id")); // 일정 ID
          doctorEvent .put("title", vacation.get("title")); // 일정 제목
          doctorEvent .put("start", vacation.get("vacationStart")); // 시작 날짜
          doctorEvent .put("end", vacation.get("vacationEnd")); // 종료 날짜
          return doctorEvent ;
        }).collect(Collectors.toList()); // 변환한 데이터를 List로 반환
  }
  
  public List<Map<String, Object>> patientsToFullCalendarFormat(List<ReserversEntity> customerEvents) {
    return customerEvents.stream()
        .distinct() // 중복 제거
        .map(customerEvent -> { // 각각의 patientEvents 데이터를 변환
          Map<String, Object> patientEvent = new HashMap<>(); // FullCalendar의 event
          patientEvent.put("id", customerEvent.getId()); // 예약
          patientEvent.put("title", customerEvent.getTitle()); // 예약 제목
          patientEvent.put("visitDate", customerEvent.getVisitDate()); // 내원 날짜
          patientEvent.put("status", customerEvent.getStatus()); // 예약 상태
          patientEvent.put("description", customerEvent.getDescription()); // 현재 상태
          return patientEvent;
        }).collect(Collectors.toList()); // 변환한 데이터를 List로 반환
  }
  ```
    
    - FullCalendar의 **events** 옵션을 활용하여 서버로부터 일정을 동적으로 가져와 달력에 표시했습니다. 서버로부터 받은 데이터를 필요한 속성으로 변환한 후, **successCallback**을 통해 FullCalendar에 전달하고 연동된 DB의 테이블에 저장하는 기능을 구현했습니다.
    - **eventClick** 이벤트를 통해 특정 일정을 클릭하면 해당 일정의 세부 정보가 **Bootstrap Modal**에 표시되어 수정 및 삭제 작업이 편리하도록 기능을 구현했습니다.
    - **customButtons** 옵션으로 사용자 정의 버튼을 추가하고, 이를 통해 **Bootstrap Modal**과 연동해 개인 일정 추가 및 수정 기능을 구현했습니다.
    - **유효성 검사**를 통해 잘못된 데이터 입력을 방지하고, 예약 상태(예: 예약 대기, 예약 확정, 예약 불가)를 표시 및 변경할 수 있는 기능을 구현했습니다.
    - **AJAX**를 사용한 **비동기 통신**으로 일정 데이터의 추가, 수정, 삭제 시 화면 전환 없이 작업이 가능하도록 구현했습니다.
  ```
  Map으로 받아온 데이터를 가져오는에 성공했을 때 
  각각의 일정을 FullCalendar의 Event로 변환해 화면에 표시
  $.ajax({
      success: function(response) {
          // 서버 응답 데이터를 FullCalendar 형식으로 변환
          var doctorEvent = response.map(function(doctorEvent) {
              return {
                  id: doctorEvent.id, // 일정 id
                  title: doctorEvent.title, // 일정 제목
                  start: doctorEvent.start, // 시작 날짜 (response에서 start로 전달)
                  end: moment(doctorEvent.end).add(1, 'days').format('YYYY-MM-DD'),
                  allDay: true // allDay 속성 추가
              };
          });
          var patientEvent = response.map(function(patientEvent) {
              return {
                  id: patientEvent.id, // 일정 id
                  title: patientEvent.title, // 일정 제목
                  start: patientEvent.visitDate, // 시작 날짜 (response에서 start로 전달)
                  extendedProps: {
                      status: patientEvent.status, // extendedProps에 예약 상태 추가
                      description: patientEvent.description, // extendedProps에 상태 설명 추가
                  }
              };
          });
          var allEvents = doctorEvent.concat(patientEvent); // 모든 event를 하나로 결합
          successCallback(allEvents); // 변환된 데이터를 FullCalendar에 전달
      },
  });
  ```
    
### 2. PortOne API

- **사용 기술 :** PortOne API, UUID Class, RestTemplate Class, AJAX, JavaScript
- **구현 내용**
    - **UUID Class**를 사용해 고유성이 높고 표준화된 포맷 형태를 가진 **merchant_uid(가맹점 고유 주문 번호)**를 생성했습니다.
    - 결제가 성공적으로 완료되면, **AJAX 요청**으로 서버에 결제 데이터를 전송하고, 데이터베이스에 저장되도록 구현했습니다. 이를 통해 결제 내역의 관리와 추적이 가능하도록 기능을 구현했습니다.
    - **RestTemplate**을 사용해 **HTTP 통신을 진행**해 PortOne API 서버에 접속, PortOne API의 결제 취소 기능 구현에 필요한 액세스 **토큰을 발급** 받고 저장하는 기능을 구현했습니다.
    - 결제가 완료된 후에 예약 화면의 **예약하기 버튼**이 활성화 되도록 설정해 잘못된 예약 데이터가 DB에 저장되는 것을 방지했습니다.
![결제창](https://github.com/user-attachments/assets/2e8c98e6-d77c-4673-af63-f2aa5db4d05f)


### 3. 트랜잭션 관리를 통한 데이터 안정성

- **사용 기술 :** Java Proxy(Transactional-Rollback)
- **구현 내용**
    - **Java 프록시**의 트랜잭션 롤백 기능을 구현해 여러 트랜잭션이 동시에 실행될 때 데이터의 정확성과 원자성을 보장하여 데이터 불일치를 방지 했습니다.

```java
1.
환자의 `reservers` 테이블, 의사의 `reservings` 테이블에 
동일한 데이터를 Java Proxy로 INSERT
		try {
			// 환자 `reservers` 테이블
			reserversBO.addPatientReserve(customerId, customerLoginId, doctorNum, title, description, visitDate, file);
			// 의사 `reservings` 테이블
			patientReservingsBO.addToReservings(doctorNum, customerId, customerName, title, description, visitDate, file, customerLoginId);
			return 1;
		} catch (Exception e) {
			log.error("!!!!! 오류 발생, 롤백 : {}", e.getMessage());
			throw e;
		}
	
2.
의사가 FullCalendar에서 예약한 환자의 예약 상태(확정/불가/대기)를 
변경할 경우 Java Proxy를 사용해 `reservers`, `reservings` 테이블 모두 업데이트
	try {
			// 업데이트 1 : 환자의 예약 상태(reservers)
			int patientUpdateResult = patientReserversBO.updateReserversByIdStatus(id, status);
			if(patientUpdateResult == 0) {
				throw new RuntimeException("@@@@@달력에서 `reservers` 데이터 업데이트 실패@@@@@");
			}
			// 업데이트 2 : 의사의 예약 상태(reservings)
			int doctorUpdateResult = doctorsReservingsBO.updateReservingsByIdStatus(id, status);
			if(doctorUpdateResult == 0) {
				throw new RuntimeException("@@@@@달력에서 `reservings` 데이터 업데이트 실패@@@@@");
			}
			return 1;
		} catch (Exception e) {
			log.error("Transaction 실패 : {}", e.getMessage());
			throw e;
		}
```

## 6. 문제 해결 과정(Trouble Shooting)

1. JAVA Proxy를 사용한 Transactional 처리
    >예약 시스템 구현 과정에서 환자가 예약하거나 변경한 내역이 환자 DB 테이블에만 저장되고 의사의 DB 테이블에는 저장되지 않는 문제가 발생했습니다. 이로 인해 ID 값 불일치로 예약 목록을 정확히 처리할 수 없었습니다. 문제를 해결하기 위해 초기에는 별도의 Repository 쿼리를 작성해 각각의 DB에 INSERT를 수행했으나, 둘 중 하나의 쿼리가 실패할 경우 데이터 불일치 문제가 발생했습니다. 이를 해결하기 위해 **Java Proxy**를 활용한 **트랜잭션 롤백** 기능을 구현했습니다. 덕분에 여러 트랜잭션이 동시에 실행될 때 데이터의 정확성과 원자성을 보장하고, 실패 시 전체 작업을 원상태로 되돌리도록 처리했습니다.
    
2. DTO를 사용한 FullCalendar 데이터 변환
    >FullCalendar는 일정 데이터를 **Map<String, Object>** 형식으로 처리하기 때문에, DB에서 **List** 형식으로 가져온 데이터를 FullCalendar가 요구하는 형식으로 변환하는 작업이 필요했습니다. 이때, 데이터의 무결성을 유지하면서 각 항목의 이름과 값을 정확히 매핑하기 위해 **DTO(Data Transfer Object)**를 사용해 데이터 변환을 진행했습니다. DTO를 사용해 데이터를 정확하고 직관적으로 정의해 FullCalendar의 events 요소의 필수 필드인 id, title, start, end와 같은 정보를 정확히 매핑할 수 있었습니다. 그리고 DTO를 사용한 데이터 변환 과정에서 중복된 데이터를 처리하고 데이터의 구조를 명확하게 정의해 코드의 가독성과 유지 보수성이 향상해 서버와 클라이언트 간의 데이터 교환이 일관되게 구현할 수 있었습니다. 이를 통해 FullCalendar의 events API와의 호환성을 높여 예약 및 의사 일정 데이터를 서버에서 클라이언트로 효율적으로 전달할 수 있었습니다.
    
3. **UUID Class**를 사용한 merchant_uid(가맹점 고유 주문 번호) 생성, RestTemplate를 사용한 ****PortOne API의 **액세스 토큰** 발급
    >PortOne API의 결제 및 결제 취소 기능을 구현하기 위해 **merchant_uid**와 **액세스 토큰**을 발급받는 과정이 필요했습니다. **merchant_uid**는 고유 식별자가 필요했기 때문에, **SHA-256** 방식 대신 중복 가능성이 극히 낮고 고유성이 보장되는 값을 생성하는 **UUID Class**를 사용하여 고유하고 안전한 주문 번호를 생성했습니다. 이를 통해 주문 번호에 추가적인 의미나 규칙을 부여할 필요 없이, 빠르고 안전하게 결제를 처리할 수 있었습니다.
    **RestTemplate**을 사용하여 **PortOne API**와의 HTTP 통신을 처리했습니다. **액세스 토큰** 발급 과정에서는 PortOne에서 제공하는 **secret key**와 **imp key**를 활용해 인증을 진행하고, 이를 서버의 **yml 파일**에 안전하게 저장한 후 **gitignore**로 등록하여 보안성을 높였습니다. 이후 **액세스 토큰**을 사용하여 결제 취소 기능을 안전하고 효율적으로 구현할 수 있었습니다.다.
    
4. 회원가입 비밀번호 Hashing, Salt 처리
    >회원가입 시 비밀번호 보안을 강화하기 위해 **SHA-2**알고리즘을 사용해 **단방향 해싱**을 구현하고 
    **레인보우 테이블** 공격에 노출되는 것을 방지하기 위해 **Salt**를 추가로 생성했습니다. DB 저장 알고리즘 초기에는 해싱된 비밀번호와 Salt 값을 각각 별도의 Column에 저장해 두 개의 Column을 관리해야 했습니다. 하지만 디버깅 과정에서 Salt는 고정된 길이(byte[16])로 생성된다는 점을 발견했습니다. 이후 Salt와 해싱된 비밀번호 값을 결합한 문자열을 하나의 Column에 저장해 데이터베이스 구조를 단순화하고 단일 Column에서 데이터를 처리해 쿼리의 복잡성을 줄이고 성능을 향상했습니다. 그리고 Salt와 해싱된 비밀번호를 분리하는 알고리즘이 서버 내부에서만 동작하도록 구현해 외부로 노출되는 민감한 데이터를 최소화하며, 보안성과 안정성을 강화했습니다.
    
5. SQL 예약어(명령어) 문제 해결
    >SQL 예약어(명령어)인 **condition**을 데이터베이스의 Column 이름으로 사용해 Repository를 통한 DB INSERT 작업이 실패하는 문제가 발생했습니다. 해당 문제를 해결하기 위해 다양한 기술 블로그와 MySQL 공식 문서를 참고해 예약어의 특성과 해결 방안을 학습했습니다. 문제 해결을 위해 백틱(`)을 사용하는 방식을 생각했지만 SQL 명령어와 혼동될 가능성을 방지하고 보다 직관적이고 객관적인 명령어를 생성하기 위해 **condition**을 **status**로 변경했습니다. 이를 통해 SQL 예약어와의 충돌 문제를 해결하고 Repository를 사용하는 데이터 작업이 정상적으로 수행되도록 문제를 해결했습니다. 문제 해결 과정에서 예약어 사용 시의 제약과 적절한 네이밍 규칙을 학습하며, 데이터베이스 설계와 관련된 역량을 강화할 수 있었습니다.
    
6. JPA를 사용한 환자의 예약 페이지 목록 기능 구현
    >**JPA**를 활용해 환자의 예약 목록을 페이징 처리하는 기능을 구현하는 과정에서 대량의 데이터를 효율적으로 관리하기 위해 **Spring Data JPA**의 페이징 메커니즘을 학습하고 적용했습니다. **PageRequest 클래스**를 사용해 페이지 번호, 항목 수, 정렬 조건을 설정 후 Repository 메서드에 **Pageable** 객체를 전달해 **HttpRequest**와 **HttpSession**을 사용해 사용자의 로그인 여부를 확인 후 로그인한 사용자의 예약 데이터를 페이지 단위로 조회할 수 있도록 구현했습니다. 조회된 결과는 **Page** 객체로 반환해 현재 페이지의 데이터와 전체 페이지 수를 계산해 View에 전달함으로써 페이징 UI를 구현했습니다. 이를 통해 JPA의 페이징 기능을 적용하며 관련 개념을 익히고 실무적인 데이터 처리 능력을 강화할 수 있었습니다.
    
7. Mybatis(XML, Mapper, BO)를 사용한 의사 예약 목록 페이징
    >**MyBatis**의  **동적 SQL**을 활용해 의사의 예약 목록을 페이징 처리해 이전/다음 페이지 데이터를 조회하는 기능을 구현했습니다. **Repository(XML, Mapper)영역**에서 방문 날짜를 기준으로 데이터 탐색 방향(prev 또는 next)에 따라 쿼리를 동적으로 변경하고, **Service(BO)**에서는 조건에 따라 데이터 정렬 및 페이징 로직을 처리했습니다. 그리고 첫 번째 페이지와 마지막 페이지 여부를 확인하기 위해 전체 데이터의 첫 번째 또는 마지막 날짜를 조회하는 쿼리를 추가해 UI의 버튼 활성화 상태를 제어하는 기능을 추가했습니다. 이를 통해 MyBatis의 동적 쿼리와 Java 기반 데이터 처리 방식을 이해하고 페이징 기능을 구현했습니다.

## 7. 개발 후 느낀점
>프로젝트를 진행하며 새로운 기술과 개념을 배우고 이를 실제 코드로 구현하는 과정에서 많은 것을 배우고 느꼈습니다. 특히, 문제를 해결하기 위해 다양한 자료를 찾아보고, 기술과 관련된 문서와 선례를 학습하며 스스로 고민하고 도전한 끝에 기능이 정상적으로 동작했을 때 큰 성취감을 느꼈습니다.
>특히, 트랜잭션 관리, 데이터 변환, 비밀번호 암호화, API 통신, 그리고 데이터베이스 설계와 같은 다양한 영역에서 발생한 문제들을 해결하며 기술적인 성장을 넘어 문제를 분석하고 구조적인 해결책을 탐색하고 설계하는 능력을 키울 수 있었습니다.
>그리고 문제를 해결하기 위해 여러 가지 방법을 고민하고 비교하는 과정에서 사용자의 편의성, 시스템의 안정성, 유지보수성을 고려하며 코드를 작성하는 경험을 했습니다. 이를 통해 단순히 작동하는 코드 작성에서 벗어나, 더 나은 품질의 소프트웨어를 개발하려는 책임감 있는 자세를 배양할 수 있었습니다.
>마지막으로, 프로젝트를 통해 개발자로서의 기본기를 다지며, 새로운 기술과 문제에 도전하는 과정에서 더 나은 결과물을 만들어 나가겠다는 자신감과 동기를 확고히 할 수 있었습니다.
