package com.example.tokenlogin.controller;


import com.example.tokenlogin.dto.BoardDeleteRequestDTO;
import com.example.tokenlogin.dto.BoardDetailResponseDTO;
import com.example.tokenlogin.dto.BoardListResponseDTO;
import com.example.tokenlogin.model.Article;
import com.example.tokenlogin.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardApiController {

    private final BoardService boardService;

//    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public BoardListResponseDTO getBoards( //게시글 목록을 조회하는 컨트롤러 메서드
            @RequestParam(name="page",defaultValue = "1")int page,  //page: 요청한 페이지 번호 (기본값 1)
            @RequestParam(name="size",defaultValue="10")int size   //size: 한 페이지당 가져올 게시글 개수 (기본값 10)
    ){
        List<Article> articles= boardService.getBoardArticles(page, size);
//        boardService.getBoardArticles(page, size) 호출
//        지정한 페이지(page)와 크기(size)에 맞는 게시글 목록을 가져옴

        int totalArticleCnt= boardService.getTotalArticleCnt();
//        boardService.getTotalArticleCnt() 호출하여 전체 게시글 개수를 가져옴
//        페이징 처리를 위해 사용됨

        boolean last=(page*size) >=totalArticleCnt;
//        현재 페이지가 마지막 페이지인지 확인
//        (page * size) >= totalArticleCnt
//        현재 페이지에서 조회한 게시글 수가 전체 게시글 수보다 크거나 같다면 마지막 페이지(last = true)
//        더 많은 게시글이 남아 있다면 false

        return BoardListResponseDTO.builder() //BoardListResponseDTO를 생성하여 반환
                .articles(articles)//articles: 현재 페이지의 게시글 목록
                .last(last)//last: 현재 페이지가 마지막 페이지인지 여부 (true / false)
                .build();


//        게시글 목록을 페이지 단위로 조회하는 API
//        page, size 요청 파라미터를 이용해 페이징 처리
//        boardService.getBoardArticles(page, size) → 지정된 개수만큼 게시글 조회
//        boardService.getTotalArticleCnt() → 전체 게시글 개수 조회
//        현재 페이지가 마지막 페이지인지 여부 계산 (last)
//        최종적으로 BoardListResponseDTO를 반환하여 게시글 목록 및 페이징 정보 제공

    }

    @GetMapping("{id}")
    public BoardDetailResponseDTO getBoardDetail(@PathVariable long id){ //특정 게시글의 상세 정보를 조회하는 API
//        매개변수:
//        @PathVariable long id → 조회할 게시글의 ID
//        반환 타입:
//        BoardDetailResponseDTO → 게시글 상세 정보를 담은 DTO


        return boardService
                .getBoardDetail(id)
                //boardService.getBoardDetail(id)를 호출 id에 해당하는 게시글 정보를 가져옴
                //데이터는 일반적으로 Article 엔티티 객체로 반환될 가능성이 높음

                .toBoardDetailResponseDTO();
        //Article 객체를 BoardDetailResponseDTO로 변환
        //toBoardDetailResponseDTO()는 DTO 변환 메서드로 예상됨
    }
    @PostMapping
    public void saveArticle(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("hiddenUserId") String userId,
            @RequestParam("file") MultipartFile file
    ) {
        boardService.saveArticle(userId, title, content,  file);
    }



    @PutMapping
    public void updateArticle(    // 게시글을 수정하는 API
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("hiddenUserId") String userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("hiddenId")Long id,
            @RequestParam("hiddenFileFlag")Boolean fileChanged,
            @RequestParam("hiddenFilePath")String filePath
    ){
        boardService.updateArticle(id, title, content, file, fileChanged, filePath);
//        boardService.updateArticle(...)이 호출되며:
//        게시글이 수정되며, 파일이 변경되었을 경우 파일이 업데이트되거나, 기존 파일이 그대로 유지됨
//        파일이 변경된 경우 기존 파일을 삭제하고 새로운 파일을 저장
//        fileChanged가 true라면 기존 파일을 삭제하고 새로운 파일을 업로드

    }



    @GetMapping("/file/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {//파일 다운로드를 처리하는 API
        Resource resource = boardService.downloadFile(fileName);

//        boardService.downloadFile(fileName)를 호출하여 파일을 다운로드
//        Resource 객체는 Spring에서 파일을 다룰 때 사용하는 추상화된 인터페이스로, 파일 시스템에서 로드한 파일을 이 객체로 감쌈




        String encoded = URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8);
//        파일명이 한글이 포함될 수 있기 때문에 URL 인코딩을 통해 파일명이 깨지지 않도록 처리
//        UTF-8로 인코딩하여 한글 파일명이 올바르게 표시될 수 있도록 함

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)//contentType(MediaType.APPLICATION_OCTET_STREAM)로 파일 데이터를 전송하는 MIME 타입 지정
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                //HttpHeaders.CONTENT_DISPOSITION 헤더에 attachment를 설정하여 파일 다운로드를 유도
                //filename*=UTF-8''와 인코딩된 파일명을 함께 설정하여 파일 이름을 깨지지 않게 처리
                .body(resource); //body(resource)로 파일 데이터를 응답 본문에 담아 반환


//        파일 다운로드 처리 API로 클라이언트가 지정한 fileName에 해당하는 파일을 다운로드
//        boardService.downloadFile(fileName)로 파일을 로드하고 Resource로 반환
//        파일명을 URL 인코딩하여 한글 파일명도 문제없이 처리
//        ResponseEntity를 사용하여 파일을 다운로드할 수 있도록 HTTP 응답을 설정
    }

    @DeleteMapping("/{id}")
    public void deleteArticle(@PathVariable long id, @RequestBody BoardDeleteRequestDTO requestDTO) {
        boardService.deleteBoardById(id, requestDTO);

//        deleteBoardById 메서드 내에서 아래와 같은 작업이 이루어질 가능성이 있습니다:
//
//        파일 삭제: requestDTO를 통해 전달된 파일 경로를 확인하고 파일을 삭제
//        예: fileService.deleteFile(requestDTO.getFilePath())
//        게시글 삭제: 해당 id의 게시글을 데이터베이스에서 삭제
//        예: boardMapper.deleteArticle(id) (MyBatis를 사용할 경우)
    }




}
