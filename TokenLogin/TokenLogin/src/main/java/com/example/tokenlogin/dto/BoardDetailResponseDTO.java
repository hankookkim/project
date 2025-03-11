package com.example.tokenlogin.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BoardDetailResponseDTO {
//    BoardDetailResponseDTO는 게시글의 상세 정보를 담기 위한 DTO(Data Transfer Object) 클래스입니다.
//    이 클래스는 게시글의 제목, 내용, 사용자 ID, 파일 경로, 작성 시간 등 게시글의 세부 정보를 전송하는 용도로
//    사용됩니다. DTO는 주로 컨트롤러나 서비스 계층에서 데이터를 전달할 때 유용합니다.

    private String title;
    private String content;
    private String userId;
    private String filePath; // 파일 경로 (첨부파일이 있다면 해당 경로)
    private LocalDateTime created; // 게시글 작성 시간
}
