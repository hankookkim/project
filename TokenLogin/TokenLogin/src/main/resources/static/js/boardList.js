$(document).ready(() => {
    // HTML 문서가 완전히 로드된 후에 이 코드가 실행되도록 보장합니다.
    // 즉, DOM이 준비된 후에 사용자와 상호작용할 수 있도록 설정
    checkToken();
    setupAjax();
    // heckToken(): 로그인 상태나 인증을 확인하는 함수입니다.
    // 보통 사용자가 인증을 받았는지 여부를 확인하고, 필요시 로그인 페이지로 리디렉션하는 등의 작업을 합니다.
    // setupAjax(): Ajax 요청에 필요한 기본 설정을 합니다.
    // 예를 들어, 헤더에 인증 토큰을 추가하거나, 응답이 실패했을 때 자동으로 재시도하는 등의 설정을 할 수 있습니다.

    getUserInfo().then((userInfo) => {
        // getUserInfo(): 서버에서 사용자 정보를 비동기적으로 가져오는 함수입니다.
        // Promise를 반환하며, 사용자 정보를 성공적으로 가져오면 then 블록이 실행

        $('#welcome-message').text(userInfo.userName + '님 환영합니다!');
        //사용자 이름을 화면에 표시하여 환영 메시지를 출력
        $('#hiddenUserId').val(userInfo.userId);
        // 사용자 ID를 숨겨진 필드에 설정합니다. 이 값은 폼을 제출할 때 서버에 전달될 수 있습니다.
        $('#hiddenUserName').val(userInfo.userName);
        //사용자 이름을 숨겨진 필드에 설정

    }).catch((error) => {
        console.error('board list user info error : ', error)
    });
    getBoards();
    // 게시판 데이터를 서버에서 가져오는 함수입니다. 이 함수는 아마도 Ajax 요청을 통해
    // 게시판 목록을 받아오고, 받아온 데이터를 화면에 표시하는 작업을 할 것입니다.
});

let getBoards = () => {//게시글 목록을 페이지네이션하여 보여주는 기능
    let currentPage = 1;
    const pageSize = 10; // 한 페이지에 보여줄 게시글의 수

    loadBoard(currentPage, pageSize);// 초기 게시글 목록을 가져옴
    // 페이지가 처음 로드될 때 loadBoard(currentPage, pageSize) 함수가 호출됩니다.
    // 여기서 currentPage는 현재 페이지 번호, pageSize는 한 페이지에 보여줄 게시글의 수입니다.
    // 이 함수는 게시글을 서버에서 받아와서 해당 페이지에 맞는 게시글만 보여주는 역할을 합니다.

    // 다음 페이지 버튼 클릭 이벤트
    $('#nextPage').on('click', () => {
        currentPage++;// 페이지 번호 증가
        loadBoard(currentPage, pageSize); // 새로 고침된 게시글 목록을 가져옴
        // #nextPage 버튼이 클릭되면 currentPage가 증가하고, loadBoard(currentPage, pageSize)
        // 를 호출하여 새로운 페이지의 게시글 목록을 가져옵니다.
        // 즉, 다음 페이지로 이동하면서 게시글 목록을 갱신합니다.
    });
    // 이전 페이지 버튼 클릭 이벤트
    $('#prevPage').on('click', () => {
        if (currentPage > 1) {// 페이지가 1보다 클 때만 이전 페이지로 이동
            currentPage--;// 페이지 번호 감소
            loadBoard(currentPage, pageSize);// 새로 고침된 게시글 목록을 가져옴
            // #prevPage 버튼이 클릭되면 currentPage가 1 이상인 경우에만 페이지를 한 칸 감소시키고,
            // loadBoard(currentPage, pageSize)를 호출하여 이전 페이지의 게시글 목록을 가져옵니다.
            // 만약 현재 페이지가 1이라면, 이전 페이지 버튼이 눌려도 페이지가 변경되지 않습니다.
        }
    });
}

let loadBoard = (page, size) => {//게시글 목록을 불러와 화면에 표시하는 기능
    $.ajax({
        type: 'GET',
        url: '/api/board', //$.ajax를 이용해 /api/board 엔드포인트에 GET 요청을 보냅니다.
        data: { //page와 size를 매개변수로 전달하여 해당 페이지의 데이터를 요청
            page: page,
            size: size
        },
        success: (response) => {
            console.log('loadBoard : ', response);
            $('#boardContent').empty(); // 기존 게시글 내용 비우기
            if (response.articles.length <= 0) { //response.articles를 순회하며 게시글 목록을 <tr> 요소로 동적으로 생성
                // 게시글이 없는 경우 메시지 출력
                $('#boardContent').append(
                    `<tr>
                        <td colspan="4" style="text-align: center;">글이 존재하지 않습니다.</td>
                    </tr>`
                );
            } else {
                response.articles.forEach((article) => {
                    $('#boardContent').append(
                        `
                            <tr>
                                <td>${article.id}</td>
                                <td><a href="/detail?id=${article.id}">${article.title}</a></td>
                                <td>${article.userId}</td>
                                <td>${formatDate(article.created)}</td>
                            </tr>
                    `
                    );
                });
            }

            // 페이지 정보 업데이트
            $('#pageInfo').text(page);  //현재 페이지 번호를 업데이트
            // 이전/다음 버튼 상태 설정
            $('#prevPage').prop('disabled', page === 1);  //첫 페이지에서는 '이전' 버튼 비활성화.
            $('#nextPage').prop('disabled', response.last); //마지막 페이지에서는 '다음' 버튼 비활성화.
        },
        error: (error) => {
            console.error('board list error :: ', error);
        }
    });
}


const formatDate = (dateString) => {
    let date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
};








let logout = () => {
    // logout 함수는 사용자가 로그아웃할 때 실행되며, 백엔드에 로그아웃 요청을 보내고,
    // 로컬 스토리지에서 토큰을 제거한 후 로그인 페이지로 이동하는 역할
    $.ajax({
        type: 'POST',
        url: '/logout',
        success: () => {
            alert('로그아웃이 성공했습니다.');
            localStorage.removeItem('accessToken');
            sessionStorage.removeItem('accessToken');
            window.location.href = '/member/login'
        },
        error: (error) => {
            console.log('오류발생 : ', error);
            alert('로그아웃 중 오류가 발생했습니다.');
        }
    });
}