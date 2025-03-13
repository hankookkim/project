package com.example.tokenlogin.service;


import com.example.tokenlogin.dto.BoardDeleteRequestDTO;
import com.example.tokenlogin.mapper.BoardMapper;
import com.example.tokenlogin.model.Article;
import com.example.tokenlogin.model.Paging;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService { //게시글 관련 서비스 로직을 담당하며, 데이터베이스와 파일 시스템을 연동하여 CRUD 작업을 수행

    private final BoardMapper boardMapper;
    private final FileService fileService;

    public List<Article> getBoardArticles(int page, int size){ //게시글 목록을 가져오는 서비스 메서드.
        int offset = (page - 1) * size;    // page와 size를 이용하여 페이징 처리된 게시글을 반환함.
        return boardMapper.getArticles(
                Paging.builder()
                        .offset(offset)
                        .size(size)
                        .build()

//         데이터베이스에서 페이징 처리를 위한 offset 계산.
//         예를 들어, size=10인 경우:
//        page=1 → offset = (1-1) * 10 = 0 (첫 번째 페이지)
//        page=2 → offset = (2-1) * 10 = 10 (두 번째 페이지)
//        page=3 → offset = (3-1) * 10 = 20 (세 번째 페이지)
//        이렇게 하면 각 페이지에 해당하는 데이터만 가져올 수 있음.

//                SELECT * FROM articles
//                ORDER BY created_at DESC
//                LIMIT #{size} OFFSET #{offset};


        );
    }

    @Transactional //@Transactional을 사용하면 트랜잭션 단위로 데이터베이스 작업을 처리할 수 있음
    public void saveArticle(String userId, String title, String content, MultipartFile file) {
       String path = null; //path 변수는 파일의 저장 경로를 담을 변수 (기본값은 null)
        if (!file.isEmpty()) { // 파일이 있다면 fileService.fileUpLoad(file)을 호출하여 파일을 저장하고, 해당 파일의 저장 경로를 반환받아 path에 저장
            path = fileService.fileUpLoad(file);
        }

        boardMapper.saveArticle(
                Article.builder()
                        .title(title)
                        .content(content)
                        .userId(userId)
                        .filePath(path)// 파일 경로 저장
                        .build()
        );
//        파일이 없다면 path는 null이 되어 DB에 파일 경로 없이 게시글만 저장
    }

        public int getTotalArticleCnt() { //boardMapper.getArticlesCnt() 호출하여 전체 게시글 개수 반환.
            return boardMapper.getArticleCnt();
        }

        public Article getBoardDetail(long id) {//boardMapper.getArticle(id) 호출하여 특정 id의 게시글 정보 반환.
            return boardMapper.getArticleById(id);
        }


        public Resource downloadFile(String fileName) { //fileService.downloadFile(fileName) 호출하여 파일을
                                                        // 다운로드할 수 있는 Spring Resource 객체를 반환.
            return fileService.downloadFile(fileName);
        }



        public void updateArticle(Long id, String title, String content, MultipartFile file,Boolean fileChanged, String filePath) {

        String path= null;    //파일 저장 경로(path)를 초기화.

        if (!file.isEmpty()) { //파일이 있다면 fileService.fileUpLoad(file)을 호출하여 파일을 저장하고, 저장된 경로를 path에 저장
            path = fileService.fileUpLoad(file);
        }
        if (fileChanged){ //파일 변경 여부 (true: 기존 파일 삭제 후 새 파일 저장, false: 기존 파일 유지)
            fileService.deleteFile(filePath);
        }else {
            path=filePath;
        }
        boardMapper.updateArticle(
                Article.builder()
                        .id(id)
                        .title(title)
                        .content(content)
                        .filePath(path)
                        .build()


//                클라이언트가 게시글 수정 요청을 보냄
//                updateArticle(1L, "새 제목", "새 내용", 첨부파일, true, "old/path/file.jpg")
//                새로운 파일 업로드 여부 확인
//                새 파일이 있으면 fileService.fileUpLoad(file)을 통해 저장
//                파일 변경 여부 확인 (fileChanged 값)
//               true → 기존 파일 삭제 후 새 파일 저장
//               false → 기존 파일 유지
//               게시글 정보 업데이트 (boardMapper.updateArticle(...))
//               데이터베이스에서 해당 게시글을 업데이트


        );
        }


        public void deleteBoardById(long id, BoardDeleteRequestDTO  requestDTO) {
//            특정 id에 해당하는 게시글을 삭제하는 메서드
//            게시글과 함께 연결된 파일도 삭제
//            매개변수:
//            id: 삭제할 게시글의 ID
//            requestDTO: 게시글 삭제 요청 정보를 담고 있는 DTO 객체 (BoardDeleteRequestDTO)


            fileService.deleteFile(requestDTO.getFilePath());
//            게시글에 연결된 파일이 있다면 파일 삭제
//            requestDTO.getFilePath()를 통해 삭제할 파일의 경로를 가져옴
//            fileService.deleteFile(...)을 호출하여 파일 시스템에서 파일 제거

            boardMapper.deleteABoardById(id);
//            게시글 삭제 요청을 boardMapper를 통해 DB에 전달
//            id를 기준으로 해당 게시글을 데이터베이스에서 삭제



//            특정 게시글을 삭제할 때 연결된 파일도 함께 삭제
//            파일 삭제 후, 게시글을 데이터베이스에서 삭제
//            MyBatis boardMapper.deleteABoardById(id)를 호출하여 DB에서 해당 게시글 제거
//            파일이 존재하지 않거나 이미 삭제된 상태여도 문제없이 실행 가능



        }



    }












