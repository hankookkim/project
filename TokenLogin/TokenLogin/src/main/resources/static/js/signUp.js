$(document).ready(()=>{
    // 페이지가 완전히 로드된 후 실행되도록 설정하는 jQuery 이벤트
    // 즉, HTML이 모두 로드된 후에 버튼 클릭 이벤트가 정상적으로 작동하도록 보장

    $('#signup').click((event)=>{
        // #signup 버튼이 클릭될 때 실행되는 이벤트 핸들러
        // 사용자가 회원가입 버튼을 클릭하면 내부 코드가 실행됨

        event.preventDefault();
        // 폼 기본 제출 방지 (페이지 새로고침을 막음)
        // AJAX 요청을 통해 서버와 비동기 통신을 하기 때문에, 페이지가 새로고침되지 않도록 방지

        let userId=$('#user_id').val();  //<input id="user_id"> 요소에서 값을 가져옴
        let password=$('#password').val(); //<input id="password"> 요소에서 값을 가져옴
        let userName=$('#user_name').val(); //<input id="user-name"> 요소에서 값을 가져옴
        let role=$('#role').val(); //<select id="role"> 또는 <input>에서 값을 가져옴

        let formData={
            userId: userId,
            password: password,
            userName: userName,
            role: role
        }
        // 입력받은 데이터를 하나의 객체(formData)로 구성
        // 이 데이터를 JSON 형식으로 변환하여 서버에 전송할 예정

        console.log('formData::', formData);
        // 디버깅용 로그
        // formData 객체가 제대로 생성되었는지 브라우저 개발자 도구(Console)에서 확인 가능

        $.ajax({
            type:'POST',   //HTTP POST 요청을 사용 (데이터 생성)
            url: '/join',  //회원가입을 처리하는 서버 API 엔드포인트
            data: JSON.stringify(formData),  //JavaScript 객체 → JSON 문자열 변환 후 전송
            contentType: 'application/json; charset=utf-8',  //서버가 JSON 데이터를 받을 수 있도록 지정
            dataType:'json',  //서버 응답을 JSON 형식으로 받도록 지정
            success:(response)=>{
                alert('회원가입이 성공했습니다. \n 로그인해주세요.');
                if(response.successed){
                    window.location.href = "/member/login";

                    // 서버에서 응답을 성공적으로 받으면 실행됨
                    // 회원가입 성공 메시지(alert) 를 띄움
                    // 응답 객체(response)에서 successed 값이 true라면, 로그인 페이지(/member/login)로 이동
                }
            },
            error: (error) => {
                console.log('오류발생 : ', error);
                alert('회원가입 중 오류가 발생했습니다.');
                // AJAX 요청이 실패하면 실행됨
                // 브라우저 콘솔에 오류 메시지 출력
                // 사용자에게 회원가입 오류 발생 메시지를 표시
            }
        });

    });

});


// 요약 (흐름 정리)
// 회원가입 버튼('#signup') 클릭 시 이벤트 발생
// event.preventDefault(); → 기본 폼 제출 방지
// 입력 필드 값 가져오기 ($('#user_id').val(), $('#password').val() 등)
// JSON 데이터(formData) 생성
// AJAX 요청($.ajax)을 통해 서버에 POST 전송
// 응답 성공 시 로그인 페이지(/member/login)로 이동
// 응답 실패 시 오류 메시지 출력 및 사용자에게 알림
