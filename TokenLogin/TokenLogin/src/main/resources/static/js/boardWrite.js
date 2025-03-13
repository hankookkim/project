let selectedFile = null;
//selectedFile 변수를 null로 초기화,사용자가 파일을 선택하면 이 변수에 파일이 저장됨

$(document).ready(() => {  //웹 페이지가 로드될 때 실행되는 함수들을 정의
    checkToken();   //로그인된 사용자인지 확인하는 함수
    setupAjax();   //Ajax 요청 시 기본 설정을 적용하는 함수 (예: 토큰 포함 등)

    getUserInfo().then((userInfo) => {//성공하면(then) → userName, userId 값을 가져와 hidden input에 저장
        //getUserInfo()를 호출해 사용자 정보를 가져옴
        // 서버에서 사용자 정보를 가져오는 비동기 함수 (Promise 반환)
        console.log('user info :: ', userInfo)
        $('#hiddenUserName').val(userInfo.userName);
        $('#hiddenUserId').val(userInfo.userId);
        $('#userId').val(userInfo.userId);
        // userName, userId를 숨겨진 필드(hiddenUserName, hiddenUserId)에 저장
//         숨겨진 필드를 사용하는 이유?
//         HTML 입력값을 직접 수정할 수 없도록 하기 위해
//         보안상의 이유로 화면에서 직접 입력하지 않아도 서버에 값 전달 가능
    }).catch((error) => {
        //실패하면(catch) → 오류 메시지를 콘솔에 출력
        // 서버에서 userInfo 데이터를 가져오는 과정에서 문제가 생길 경우 실행
        console.error('Error get user info : ', error)
    });



    $('#file').on('change', (event) => {
        //파일 선택(input) 변화에 대한 이벤트 처리입니다. 사용자가 파일을 선택했을 때
        // 발생하는 이벤트를 처리하고, 선택한 파일을 화면에 표시하는 기능
        // HTML에서 id="file"인 파일 입력 요소(input)를 선택
        selectedFile = event.target.files[0];
        // 사용자에 의해 선택된 파일을 변수에 저장
        // 사용자가 선택한 첫 번째 파일을 참조합니다. 여러 파일을 선택할 수 있지만,
        // 여기서는 첫 번째 파일만 처리합니다.
        updateFileList();  // 화면에 파일 목록 업데이트
        // 화면에 파일 목록을 업데이트하는 함수입니다. 이 함수는 아래 코드에서 정의된 대로,
        // 사용자가 선택한 파일을 화면에 표시하고, 파일을 삭제할 수 있는 버튼을 제공

//         event.target.files[0]이란?
//         <input type="file">에서 선택한 파일의 정보를 가져오는 속성
//         파일을 여러 개 선택할 경우 files 배열을 사용 (현재는 1개 파일만 처리)
    });

    $('#submitBtn').on('click', (event) => {
        //폼 제출을 Ajax로 처리하여 페이지를 새로 고침하지 않고도 게시글 등록을 처리하는 코드
        // #submitBtn 버튼이 클릭되면 이 이벤트 핸들러가 실행
        event.preventDefault();  // 기본 폼 제출 동작 방지

        let formData = new FormData($('#writeForm')[0]);  // 폼 데이터를 객체로 변환

        // for (let [key, value] of formData.entries()) {
        //     if (value instanceof File) {
        //         console.log('Key:', key);
        //         console.log('Name:', value.name);
        //         console.log('Size:', value.size);
        //         console.log('Type:', value.type);
        //     } else {
        //         console.log(key + ': ' + value);
        //     }
        // }
        $.ajax({
            type: 'POST', //POST 방식으로 서버에 데이터를 전송합니다.
            url: '/api/board', //요청을 보낼 URL입니다. 이 URL로 게시글 데이터를 전송
            data: formData,//폼 데이터를 FormData 객체로 전송합니다. 이 객체는 파일도 함께 전송할 수 있기 때문에, 일반적으로 multipart/form-data로 전송
            contentType: false,
            // contentType을 **false**로 설정하여 jQuery가 자동으로 Content-Type을 설정하지 않도록 합니다.
            // FormData를 사용하면 자동으로 적절한 Content-Type이 설정
            processData: false,
            // jQuery가 데이터를 자동으로 처리하지 않도록 설정합니다.
            // FormData 객체는 이미 처리된 데이터이기 때문에 jQuery가 추가로 처리할 필요가 없습니다.
            success: () => {
                alert('게시글이 성공적으로 등록되었습니다!');
                window.location.href = '/'
            },
            error: (error) => {
                console.log('오류발생 : ', error);
                alert('게시글 등록 중 오류가 발생했습니다.');
            }
        });


    });

});

// 파일 목록 업데이트 함수 (파일 하나만)
let updateFileList = () => {
    $('#fileList').empty(); // 기존 목록 비우기

    if (selectedFile) {
        $('#fileList').append(`
                    <li>
                        ${selectedFile.name} <button type="button" class="remove-btn">X</button>
                    </li>
<!--                    선택된 파일을 제거하고, 파일 입력을 초기화합니다. 그리고 화면을
                        다시 갱신하여 파일 목록에서 제거된 파일이 표시되지 않도록 합니다.-->
                `);

        // X 버튼 클릭 시 파일 제거
        $('.remove-btn').on('click', function() {
            selectedFile = null; // 선택된 파일 제거
            $('#file').val(''); // 파일 input 초기화
            updateFileList(); // 화면 파일 목록 갱신
        });
    }
}