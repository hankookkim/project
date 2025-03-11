package com.example.tokenlogin.dto;

import com.example.tokenlogin.model.Article;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BoardListResponseDTO {
//    게시글 목록을 사용자에게 반환하기 위한 DTO 클래스입니다.
//    이 클래스는 여러 개의 게시글과 마지막 페이지 여부를 담는 데 사용

    List<Article> article; // 게시글 목록 (Article 객체 리스트)
//    Article 객체들을 포함하는 리스트입니다. 게시판에 올라온 여러 게시글들을 담고 있습니다.

    boolean last; // 마지막 페이지 여부
//    last: boolean 타입의 필드로, 페이지가 마지막 페이지인지 여부를 나타냅니다.
//    페이지네이션 처리에서 주로 사용됩니다.
}


//페이지네이션 처리: last 필드를 사용하여 클라이언트가 마지막 페이지인지 확인할 수 있습니다.
//이를 통해 클라이언트는 다음 페이지가 더 이상 없을 때 추가적인 요청을 보내지 않도록 할 수 있습니다.
//유연한 데이터 전달: article 리스트는 게시글 목록을 담고 있으며, 클라이언트는 이 리스트를 받아서
//페이지에 표시할 수 있습니다.
//보안과 캡슐화: 엔티티 클래스인 Article을 직접 반환하지 않고, DTO를 사용하여 클라이언트에 전달할 수 있습니다.
//이렇게 하면 필요 없는 데이터를 노출하지 않아 보안에 유리하고, 구조적으로도 더 안전합니다.