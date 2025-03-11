package com.example.tokenlogin.service;


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

    private List<Article> getBoardArticles(int page, int size){
        int offset = (page - 1) * size;
        return boardMapper.getArticles(
                Paging.builder()
                        .offset(offset)
                        .size(size)
                        .build()
        );
    }

    @Transactional
    public void saveArticle(String userId, String title, String content, MultipartFile file){
        String path=null;
        if (!file.isEmpty()) {// 업로드할 파일이 있을 경우
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

//        String path = null;
//
//        try {
//            if (!file.isEmpty()) {
//                path = fileService.fileUpLoad(file);
//            }
//
//            boardMapper.saveArticle(
//                    Article.builder()
//                            .title(title)
//                            .content(content)
//                            .userId(userId)
//                            .filePath(path)
//                            .build()
//            );
//        } catch (Exception e) {
//            // DB 저장 실패 시, 업로드된 파일 삭제
//            if (path != null) {
//                fileService.deleteFile(path);
//            }
//            throw new RuntimeException("게시글 저장 중 오류 발생", e);
//        }
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


    @Transactional
    public void updateArticle(long id, String title, String content, MultipartFile file) {//게시글 수정 및 파일 업로드/삭제를 관리하는 메서드
        Article existingArticle = boardMapper.getArticleById(id); //boardMapper.getArticleById(id)을 호출하여 기존 게시글 정보를 가져옴.
        String newPath = null;

        try {
            if (!file.isEmpty()) {// 새로운 파일이 업로드되었는지 확인
                newPath = fileService.fileUpLoad(file);
            }

            boardMapper.updateArticle(
                    Article.builder()
                            .id(id)
                            .title(title)
                            .content(content)
                            .filePath(newPath != null ? newPath : existingArticle.getFilePath()) // 새 파일 없으면 기존 경로 유지
                            .build()
            );

            // 기존 파일 삭제 (새 파일이 업로드된 경우에만)
            if (newPath != null && existingArticle.getFilePath() != null) {
                fileService.deleteFile(existingArticle.getFilePath());
            }

        } catch (Exception e) {
            if (newPath != null) {
                fileService.deleteFile(newPath); // 실패 시 새 파일 삭제
            }
            throw new RuntimeException("게시글 수정 중 오류 발생", e);
        }
    }












    @Transactional
    public void deleteArticle(long id) {
        Article article = boardMapper.getArticleById(id);

        if (article != null && article.getFilePath() != null) {
            fileService.deleteFile(article.getFilePath()); // 파일 삭제
        }

        boardMapper.deleteArticle(id); // DB에서 게시글 삭제
    }


    }
