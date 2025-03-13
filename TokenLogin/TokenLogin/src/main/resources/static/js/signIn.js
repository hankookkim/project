$(document).ready(()=>{
    // 문서(DOM)가 완전히 로드된 후 실행되는 코드 블록입니다.
    //  jQuery의 ready 함수는 HTML 문서가 로드되고 DOM이 완전히 구축되었을 때 실행됩니다.


    $('#signin').click(()=>{ //#signin이라는 ID를 가진 버튼(로그인 버튼)을 클릭하면 실행되는 이벤트 리스너입니다.

        let userId =$('#user_id').val(); //입력 필드 #user_id의 값을 가져와 userId 변수에 저장합니다.
        let password= $('#password').val();  //입력 필드 #password의 값을 가져와 password 변수에 저장합니다.

        let formData={
            // 입력받은 아이디와 비밀번호 값을 formDate라는 객체에 저장합니다.
            // 이 객체는 백엔드 서버에 전달될 로그인 정보입니다.

            userName: userId,
            password: password
        }
        $.ajax({    //jQuery의 $.ajax()를 이용해 서버에 로그인 요청을 보냅니다.
            type:"POST",  //HTTP POST 요청을 사용하여 데이터를 서버에 보냅니다.
            url:'/login',  //로그인 요청을 처리할 서버의 엔드포인트(/login)로 요청을 보냅니다.
            data: JSON.stringify(formData), //formDate 객체를 JSON 문자열로 변환하여 전송합니다.
            contentType: 'application/json; charset=utf-8', //서버에 JSON 형식으로 데이터를 보낸다고 명시합니다.
            dataType: 'json',  //서버에서 JSON 형식의 응답을 받을 것임을 설정합니다.
            success: (response)=>{
                alert('로그인이 성공했습니다');
                console.log(response);
                localStorage.setItem('accessToken',response.token);
                // 응답에서 받은 token 값을 localStorage에 저장합니다.
                // 이 토큰은 이후의 API 요청에서 인증을 위한 JWT 토큰 등으로 사용될 수 있습니다

                window.location.href='/'
                // 로그인 성공 후 사용자를 홈페이지(/)로 이동시킵니다.
            },
            error: (error)=>{
                console.log('오류발생:',error);
                alert('로그인 중 오류가 발생했습니다');
            }
        });
    });
});