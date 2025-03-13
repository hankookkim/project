let selectedFile = null;

$(document).ready(() => {
    checkToken(); //로그인 상태를 확인하는 함수로, 로그인하지 않으면 사용자가 게시글을 수정할 수 없도록 토큰을 검사
    setupAjax(); //모든 AJAX 요청에 필요한 기본 설정을 하는 함수입니다. 예를 들어, 요청 헤더에 인증 토큰을 자동으로 포함시킬 수 있습니다.
    getUserInfo().then((userInfo) => {
        // 비동기적으로 서버에서 사용자 정보를 가져옵니다. 이 함수는 Promise를 반환하므로
        // then과 catch를 사용해 성공과 실패를 처리

        console.log('user info :: ', userInfo)
        $('#hiddenUserName').val(userInfo.userName);
        $('#hiddenUserId').val(userInfo.userId);
        $('#userId').val(userInfo.userId);
    }).catch((error) => {
        console.error('Error get user info : ', error)

        // 이 코드에서는 getUserInfo() 함수로 사용자 정보를 가져오고, 그 값을 숨겨진
        // 필드(hiddenUserName, hiddenUserId)와 userId 필드에 설정합니다.
        // 이 작업은 게시글 수정 시 사용자의 정보를 서버로 함께 전달하기 위함입니다.
    });
    loadBoardDetail();
    // 게시글 수정 페이지에 들어오면, loadBoardDetail() 함수가 실행되어
    // 해당 게시글의 상세 정보를 서버에서 가져옵니다.

    $('#hiddenFileFlag').val(false);
// *hiddenFileFlag**는 숨겨진 필드로, 파일이 선택되지 않았을 때 false 값을 설정합니다.
// 이 필드는 이후 파일이 선택되었을 때 true로 변경되어, 서버로 파일이 있는지 여부를 알려주는 역할을 합니다.
//  페이지가 처음 로드되었을 때는 hiddenFileFlag가 false로
// 설정되어 파일이 선택되지 않았음을 표시합니다.

    $('#file').on('change', (event) => {
        //#file 요소에 파일을 선택할 때마다 발생하는 change 이벤트를 처리합니다.
        // 이 이벤트는 사용자가 파일을 선택할 때마다 트리거됩니다.
        selectedFile = event.target.files[0];
        // 사용자가 선택한 파일을 selectedFile 변수에 저장합니다. event.target.files는 파일 입력 필드에서
        // 선택된 파일 목록을 나타내며, [0]을 사용하여 첫 번째 파일을 선택
        $('#hiddenFileFlag').val(true);
        // 파일이 선택되었으므로 hiddenFileFlag 값을 true로 설정합니다.
        // 이는 서버에 파일이 존재한다는 정보를 전달하는 역할을 합니다.

        updateFileList(); //실행


    });

    $('#submitBtn').on('click', (event) => { //게시글 수정을 처리하는 AJAX 요청을 보냅니다.
        event.preventDefault();

        let formData = new FormData($('#writeForm')[0]);
        // FormData 객체를 사용하여 #writeForm 폼의 데이터를 가져옵니다.
        // **FormData**는 폼 데이터를 쉽게 다룰 수 있게 도와주는 객체로,
        // 파일을 포함한 데이터를 전송할 때 유용합니다.
        // $('#writeForm')[0]: jQuery를 사용하여 #writeForm 폼을 선택하고,
        // **[0]**을 통해 원본 DOM 객체로 변환하여 FormData에 전달합니다.

        $.ajax({
            type: 'PUT', //PUT 요청은 리소스를 수정할 때 사용됩니다. 게시글을 수정하는 요청을 서버에 보냅니다.
            url: '/api/board', //요청을 보낼 URL입니다. 이 URL은 서버에서 게시글을 수정하는 API 엔드포인트입니다.
            data: formData, //폼 데이터를 서버에 전송합니다. 이 데이터는 FormData 객체 형태로 서버로 전송되며, 파일과 텍스트 데이터를 포함할 수 있습니다.
            contentType: false,
            // contentType을 false로 설정하면, **jQuery**가 자동으로 content-type 헤더를 설정하지 않도록 합니다.
            // FormData로 데이터를 전송할 때는 이 설정이 필요합니다.
            processData: false,
            // processData를 false로 설정하면, **jQuery**가 데이터를 자동으로 변환하지 않도록 합니다.
            // 이는 FormData 객체를 사용하고 있기 때문에 필수로 설정해야 합니다.
            success: () => {
                alert('게시글이 성공적으로 수정되었습니다!');
                window.location.href = '/'
                // 요청이 성공적으로 처리되면 실행되는 콜백 함수입니다. 게시글 수정이 성공했다는 알림을 띄우고,
                // 수정 후 메인 페이지로 리디렉션합니다.
            },
            error: (error) => {
                console.log('오류발생 : ', error);
                alert('게시글 수정 중 오류가 발생했습니다.');
                // 요청이 실패하면 실행되는 콜백 함수입니다. 서버 오류나 네트워크 오류가 발생할 경우,
                // 오류 메시지를 콘솔에 출력하고 사용자에게 알림을 띄웁니다.

            }
        });

    });

});

// 파일 목록 업데이트 함수 (파일 하나만)
let updateFileList = () => { //파일 목록을 업데이트하는 함수로, 사용자가 파일을 선택하거나 제거할 때마다 화면에 파일 목록을 갱신
    $('#fileList').empty(); // 기존 목록 비우기
    // #fileList: 파일 목록을 보여주는 HTML 요소를 선택합니다.
    // .empty(): #fileList에 있는 모든 자식 요소를 제거합니다.
    // 이를 통해, 이전에 선택된 파일 목록을 초기화합니다.

    if (selectedFile) {  //selectedFile: 사용자가 선택한 파일을 저장하는 변수입니다.
        $('#fileList').append(`
                    <li>
                        ${selectedFile.name} <button type="button" class="remove-btn">X</button>
                    </li>
<!--                    선택된 파일 이름과 "X" 버튼을 목록에 추가-->
                `);

        // X 버튼 클릭 시 파일 제거
        $('.remove-btn').on('click', function() {
            selectedFile = null; // 선택된 파일을 null로 초기화하여 파일을 제거
            $('#file').val(''); // 파일 선택 input 요소의 값을 초기화하여 파일을 제거
            updateFileList(); //파일 목록을 갱신하여 화면에서 파일이 제거된 상태로 업데이트
        });
    }
}

let loadBoardDetail = () => {
    let hId = $('#hiddenId').val();
    // 게시글 ID를 숨겨놓은 input 요소로부터 값을 가져옵니다. 이 값은 게시글 상세 페이지에서
    // 해당 게시글을 고유하게 식별하는 데 사용됩니다.




    // 게시판 상세 정보 조회 및 수정 페이지에서 기존의 게시글 정보를 불러오고,
    // 파일을 첨부하거나 삭제하는 기능을 구현한 것입니다. loadBoardDetail 함수는
    // 주어진 게시글 ID(hiddenId)를 통해 서버에서 해당 게시글의 정보를 받아와서
    // 화면에 표시합니다. 게시글에 첨부된 파일이 있으면, 파일 목록을 업데이트하고
    // 사용자가 파일을 삭제할 수 있도록 합니다.
    $.ajax({
        type: 'GET',
        url: '/api/board/' + hId,
        // GET 방식으로 서버에 요청을 보내 게시글 데이터를 가져옵니다.
        // 요청 URL은 /api/board/와 게시글 ID(hId)를 결합한 형태
        success: (response) => {
            console.log('loadBoard update : ', response);// 게시글의 제목, 내용, 사용자 ID를 폼에 채웁니다.
            $('#title').val(response.title);
            $('#content').val(response.content);
            $('#userId').val(response.userId);

            if (response.filePath && response.filePath.length > 0){
                // 응답(response)에 포함된 **filePath**가 존재하고, 길이가 0보다 크면,
                // 게시글에 파일이 첨부된 것입니다.{
                let filePath = response.filePath;
            $('#hiddenFilePath').val(filePath)
            let fileName = filePath.substring(filePath.lastIndexOf('\\') + 1); //  경로에서 파일명만 추출
            let fileElement = `
                    <li>
                        ${fileName} <button type="button" class="remove-btn">X</button>
                    </li>`;
            $('#fileList').append(fileElement);

            // X 버튼 클릭 시 파일 제거
            $('.remove-btn').on('click', function () {
                selectedFile = null; // 선택된 파일 제거,선택된 파일을 초기화
                $('#file').val(''); // 파일 input 초기화,화면에서 삭제된 파일이 보이지 않게 처리
                updateFileList(); // 파일 목록 갱신
            });
        }else {
            $('#fileList').append('<li>첨부된 파일이 없습니다.</li>')
}

},
    error: (error) => {
        console.error('board list error :: ', error);
    }
});
}

