$(document).ready(() => { //HTML 문서가 완전히 로드된 후 실행되도록 설정
    checkToken(); //사용자가 로그인된 상태인지 확인
    setupAjax(); //AJAX 요청을 보낼 때 기본 설정 적용
    getUserInfo().then((userInfo) => {
        $('#hiddenUserId').val(userInfo.userId);
        $('#hiddenUserName').val(userInfo.userName);
        // 현재 로그인한 사용자 정보 가져오기
        // 가져온 사용자 정보를 숨겨진 입력 필드 (hiddenUserId, hiddenUserName)에 저장
        // 게시글 수정 시 사용자 정보를 서버로 전송하기 위해 필요

        loadBoardDetail();
        //userInfo를 가져온 후 게시글 상세 정보 로드
    }).catch((error) => { //사용자 정보를 불러오지 못했을 때 오류 로그 출력
        console.error('board edit user info error : ', error)

        //게시글 수정 페이지를 로드할 때 실행되는 초기화 코드
    });


});

let loadBoardDetail = () => {//게시글의 상세 정보를 가져와 화면에 표시하는 역할
    let hId = $('#hiddenId').val();  //hId는 현재 보고 있는 게시글의 ID
    let hUserId = $('#hiddenUserId').val(); //hUserId는 로그인한 사용자의 ID

//     $('#hiddenId').val();를 사용해서 <input type="hidden">에서 값을 가져옴.
//     이 값들은 백엔드 API 요청에 사용됨.


    $.ajax({
        type: 'GET',
        url: '/api/board/' + hId,
        success: (response) => {
            console.log('loadBoard detail : ', response);
            $('#title').text(response.title);
            $('#content').text(response.content);
            $('#userId').text(response.userId);
            $('#created').text(response.created);
            //제목, 내용, 작성자, 작성일을 화면에 표시

            // $('#title').val(response.title);
            // $('#content').val(response.content);
            // $('#userId').val(response.userId);
            // $('#created').val(response.created);


//             $('#title').text(response.title); 대신 .val() 사용
//             .text() → <div>나 <span> 같은 텍스트 요소에 사용
//             .val() → <input>이나 <textarea> 같은 폼 입력 필드에 사용
//             기존 코드에서는 .text()를 사용했는데, 입력 필드에는 .val()이 맞음!


            if (hUserId !== response.userId) {
                $('#editBtn').prop('disabled', true);
                $('#deleteBtn').prop('disabled', true);
                // 현재 로그인한 사용자 (hUserId)와 게시글 작성자 (response.userId)를 비교
                // 다른 사람이 작성한 게시글이면 수정/삭제 버튼 숨김 (.hide())

            }

            // if (hUserId !== response.userId) {
            //     $('#editBtn').hide();
            //     $('#deleteBtn').hide();
            // }
            // 버튼을 비활성화(disabled)하면 보이긴 하지만 클릭이 안 됩니다.
            // 사용자에게 불필요한 UI 요소를 안 보여주려면 .hide();를 사용하는 게 더 좋습니다.






            if (response.filePath && response.filePath.length > 0) {
                let filePath = response.filePath;
                $('#hiddenFilePath').val(filePath);
                let fileName = filePath.substring(filePath.lastIndexOf('\\') + 1); // filePath가 존재하면 파일 다운로드 링크를 생성

                let fileElement = `
                            <li>
                                <a href="/api/board/file/download/${fileName}">${fileName}</a> <!-- 다운로드 링크 -->
                            </li>`;
                $('#fileList').append(fileElement);
            } else {
                $('#fileList').append('<li>첨부된 파일이 없습니다.</li>');
            }
        },
        error: (error) => {
            console.error('board detail error :: ', error);
        }
    });
}

let editArticle = () => {
    let hId = $('#hiddenId').val();
    window.location.href = '/update/' + hId;

    // hId는 현재 보고 있는 게시글의 ID
    // window.location.href를 사용해 해당 ID를 포함한 수정 페이지로 이동
    // /update/{hId} 경로가 게시글 수정 페이지의 URL

}

let deleteArticle = () => { //게시글을 삭제하는 역할
    let hId = $('#hiddenId').val();
    let filePath = $('#hiddenFilePath').val();

    $.ajax({
        type: 'DELETE',
        url: '/api/board/' + hId,
        data: JSON.stringify({filePath : filePath}),
        // JSON 형식으로 filePath 값을 같이 보냄
        // 백엔드에서 이 파일을 같이 삭제할 수도 있음
        contentType: 'application/json; charset=utf-8',
        success: () => {
            alert('정상적으로 삭제되었습니다.');
            window.location.href = '/';
        },
        error: (error) => {
            console.error('board detail delete error :: ', error);
        }
    });
}