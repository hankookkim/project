package com.example.tokenlogin.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {

    private final String UPLOADED_FOLDER=System.getProperty("user.home")+ File.separator+ "Desktop" + File.separator
            + "java_2" + File.separator + "springboot" + File.separator + "uploads";

//    UPLOADED_FOLDER는 파일 저장 경로를 지정
//     System.getProperty("user.home") → 현재 사용자 홈 디렉토리를 가져옵니다.
//    파일이 데스크톱에 저장되므로, 배포 환경에서는 다른 경로로 변경해야 합니다.
//    보통 application.properties 또는 application.yml에 경로를 설정하는 것이 좋습니다.




    public String fileUpLoad(MultipartFile file) {     //multipartFile을 받아서 서버에 저장하는 메서드

        try{
            byte[] bytes = file.getBytes(); //MultipartFile을 바이트 배열로 변환
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());//저장할 파일의 경로(Path 객체) 생성

            Files.write(path, bytes);//해당 경로에 바이트 데이터를 저장 (파일 생성됨)

          return UPLOADED_FOLDER + file.getOriginalFilename(); //저장된 파일의 경로를 반환 (클라이언트에게 제공 가능)

        }catch (Exception e){
            throw new RuntimeException(e); //예외 발생 시 런타임 예외로 변환하여 던짐
        }
    }



    public Resource downloadFile(String fileName) {
        try {
            Path path = Paths.get(UPLOADED_FOLDER + fileName).normalize(); //파일 경로 설정,normalize()는 파일 경로(Path)를 표준화(정규화)하는 메서드
            UrlResource resource = new UrlResource(path.toUri()); //URL 리소스 생성

            if (!resource.exists() || !resource.isReadable()) { //파일 존재 여부 확인
                throw new RuntimeException("파일을 찾을 수 없거나 읽을 수 없습니다");

            }
            return resource; //파일 리소스 반환


        } catch (MalformedURLException e) { //잘못된 경로 예외 처리
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(String filePath) { //서버에서 특정 파일을 삭제하는 기능
        try{
            if (!filePath.trim().isEmpty()) {  //파일 경로가 빈 값이 아닐 때만 실행
                Path path = Paths.get(filePath);// 파일 경로(Path) 객체 생성
                Files.deleteIfExists(path);  // 파일이 존재하면 삭제

            }
        }catch (Exception e){
            e.printStackTrace();  // 예외 발생 시 콘솔에 출력
        }
    }
}
