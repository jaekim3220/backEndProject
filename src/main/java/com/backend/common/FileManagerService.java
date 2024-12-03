package com.backend.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

// 이미지 파일용

@Component // Spring Bean 등록
@Slf4j
public class FileManagerService {
	
	
	// 실제 업로드 된 이미지가 저장될 서버 경로
	public static final String FILE_UPLOAD_PATH = "D:\\JAVA\\0_backend_project\\workspace\\images/";
	
	
	// 메서드 생성
	// input : MultipartFile, customerLoginId => customerLoginId로 폴더, 파일 생성
	// output : String(이미지 경로)
	public String uploadFile(MultipartFile file, String customerLoginId) {
		
		// 폴더(directory) + 이미지 저장 경로 생성 - breakpoint
		// 예 : aaaa_17237482234/sun.png
		// 이미지 저장 경로(폴더) 생성 설정
		String directoryName = customerLoginId + "_" + System.currentTimeMillis(); // 밀리 초까지 계산 - aaaa_17237482234
		String filePath = FILE_UPLOAD_PATH + directoryName + "/";
		
		
		// 이미지 저장 폴더 생성 - breakpoint
		File directory = new File(filePath);
		if(directory.mkdir() == false) { // 파일 생성 실패
			// 폴더 생성 시 실패할 경우 경로를 null로 리턴(에러를 방지해 나머지 데이터가 DB에 들어가도록 설정)
			return null;
		}
		
		
		// 파일 업로드 - breakpoint
		try { // 이미지 파일이 있을 때
			byte[] bytes = file.getBytes();
			// ★★★★★ 한글이 들어간 이미지는 업로드 불가 => 영문자 이미지 사용 ★★★★★
			Path path = Paths.get(filePath + file.getOriginalFilename()); // 어느 경로에 어느 이름으로 넣을 것인지 지정
			Files.write(path, bytes); // 설정한 path에 bytes 데이터(파일) 추가
	        log.info("!!!!! 파일 업로드 성공 : {} !!!!!", path.toString());
		} catch (IOException e) {
	        log.error("!!!!! 파일 업로드 실패 : !!!!!", e);
			return null; // 이미지 업로드 시 실패하면 결로를 null로 return해 DB INSERT를 정상적으로 진행
		}
		
		
		// 파일 업로드에 성공하면 이미지 url path 리턴(폴더 생성 + 폴더에 이미지 업로드)
		// => 주소는 위와 같이 될 것이라고 설정(예언)
		// => `/images/aaaa_17237482234/sun.png`
		// 이미지가 저장된 경로
		return "/images/" + directoryName + "/" + file.getOriginalFilename();
		
	}
	
	
	
	// 삭제 메서드 생성
	// 삭제할 이미지 위치(주소) 추출 및 삭제 기능 구현
	// input : imagePath(이미지 폴더, 파일 주소)
	// output : X
	public void deleteFolderFile(String imagePath) {
		// 저장 폴더 - D:\\JAVA\\0_backend_project\\workspace\\images/
		// DB 저장 URL - /images/cccccc_1732882920080/whooper-swans-8640045_1280.jpg
		// 삭제할 이미지 경로 - D:\\JAVA\\0_backend_project\\workspace\\images//images/cccccc_1732882920080/whooper-swans-8640045_1280.jpg
		// images가 중복
		
		// 이미지 폴더, 파일 주소(경로)
		Path path = Paths.get(FILE_UPLOAD_PATH + imagePath.replace("/images/", ""));
		log.info("!!!!! 삭제할 이미지 경로 : {} !!!!!", path);
		Path folderPath = path.getParent();
		log.info("!!!!! 삭제할 폴더 경로 : {} !!!!!", folderPath);
		
		
		// 삭제할 이미지(폴더, 파일) 존재 확인
		if(Files.exists(path)) {
			// 서버에서 이미지 폴더, 파일 삭제
			try {
				Files.delete(path);
				Files.delete(folderPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error("!!!!! 파일 삭제 실패 : {} !!!!!", e);
			}
		}
		
		// 폴더 삭제
		if(Files.exists(path)) {
			// 서버에서 이미지 폴더, 파일 삭제
			try {
				Files.delete(path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error("!!!!! 파일 삭제 실패 : {} !!!!!", e);
			}
		}
		
		
	}
	
	
}
