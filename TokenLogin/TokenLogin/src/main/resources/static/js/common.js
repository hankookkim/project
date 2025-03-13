let handleTokenExpiration=()=>{
    //JWT 토큰이 만료되었을 때 새로운 refresh token을 통해 토큰을 갱신하는 기능을 구현한 코드

    $.ajax({
        type:'POST', //HTTP POST 요청으로 서버에 데이터를 전송
        url: '/refresh-token',
        // 서버의 /refresh-token 엔드포인트로 요청
        // 이 엔드포인트는 일반적으로 refresh token을 사용해 새로운 access token을 발급해주는 역할
        contentType: 'application/json; charset=utf-8',//서버에 전송하는 데이터가 JSON 형식임을 명시
        dataType: 'json', //서버 응답이 JSON 형식
        xhrFields: {
            withCredentials: true   // 자격 증명 정보 포함
            // **withCredentials: true**를 설정하면 쿠키나 세션 정보를 서버와 주고받을 수 있습니다.
            // 이를 사용하려면 서버도 CORS 정책을 적절히 설정해야 하며, 자격 증명 정보를 포함한 요청을 받을 수 있어야 합니다.
            //jQuery AJAX 요청에서 쿠키와 같은 자격 증명 정보를 포함시키기 위한 설정
            // 주로 CORS(Cross-Origin Resource Sharing) 환경에서 사용되며,
            // 요청을 보낼 때 쿠키나 인증 정보를 서버로 전달하고자 할 때 필요
            // 기본적으로, Cross-Origin 요청(다른 도메인으로의 요청)에 대해서는 쿠키나 인증 정보가 포함되지 않지만,
            // withCredentials: true를 설정하면 서버가 해당 요청을 허용할 수 있도록 할 수 있습니다.
        },
        success:(response)=>{
            localStorage.setItem('accessToken',response.token);//서버에서 받은 새로운 access token을 localStorage에 저장
        },
        error:()=>{
            alert('로그인이 필요합니다. 다시 로그인 해주세요.')
            window.location.href='member/login'
            // refresh token을 통해 새로운 access token을 발급받지 못했다면 사용자에게 알림을 띄웁니다.
            //     window.location.href = 'member/login': 사용자가 로그인 페이지로 리디렉션됩니다.
        }
    });
}

let checkToken=()=>{
    // 위 코드는 로그인된 상태를 확인하는 함수로, localStorage에서 accessToken을 가져와 유효한지 확인하고,
    //  만약 토큰이 없거나 빈 문자열일 경우 로그인 페이지로 리디렉션하는 기능을 합니다.

    let token = localStorage.getItem('accessToken');
    //localStorage에서 accessToken이라는 키로 저장된 값을 가져옵니다.

    if(token==null|| token.trim()===''){ //가져온 token이 null이거나 빈 문자열일 경우를 체크

        window.location.href='/member/login'
        // 토큰이 없거나 유효하지 않으면 사용자를 /member/login URL로 리디렉션합니다.
        // 이 부분은 사용자가 로그인이 되어 있지 않으면 로그인 페이지로 이동하게 합니다.
    }
}

let setupAjax=()=>{
    // AJAX 요청을 설정하는 함수입니다. $.ajaxSetup()을 사용하여 모든 AJAX 요청에 대해
    // 공통적으로 처리할 사항을 설정합니다. 특히, Authorization 헤더에 JWT 토큰을 자동으로
    // 추가하는 작업을 수행합니다.

    $.ajaxSetup({
        //jQuery에서 모든 AJAX 요청에 대해 공통적으로 설정할 내용을 정의하는 함수
        //한 번 설정하면 이후의 모든 AJAX 요청에 적용


        beforeSend: (xhr)=>{
            //beforeSend는 AJAX 요청이 실제로 보내지기 전에 실행되는 콜백 함수
            // xhr은 XMLHttpRequest 객체로, 요청 헤더를 설정할 수 있는 방법을 제공

            let token=localStorage.getItem('accessToken');
            // localStorage에서 accessToken 값을 가져옵니다.
            // 이 토큰은 서버에 인증을 요청할 때 필요합니다.

            if(token){
                xhr.setRequestHeader('Authorization','Bearer '+token)

                // 만약 accessToken이 존재한다면, 요청 헤더에 Authorization 헤더를 설정합니다.
                // Authorization: Bearer <token> 형식으로 헤더를 추가하여 서버에 인증 정보를 보냅니다.
                // Bearer는 JWT 토큰 인증 방식에서 사용되는 인증 유형입니다.

                // setAjax()를 호출하면, 이후 모든 AJAX 요청에 자동으로 Authorization 헤더가 추가됩니다.
                // 이 방식은 로그인 후에 JWT 토큰을 저장하고, 그 토큰을 서버에 전달해야 할 경우 매우 유용
            }
        }
    })
}

let getUserInfo = () => {
    // 사용자 정보를 서버에서 가져오는 AJAX 요청을 포함한 함수입니다.
    // 이 함수는 Promise를 사용하여 비동기 처리를 하며, 서버에서 응답을 받으면
    // 그 결과를 resolve()로 전달하고, 에러가 발생하면 reject()로 처리합니다.


    return new Promise((resolve, reject) => {
        // Promise를 반환하는 함수입니다. AJAX 요청이 비동기적으로 처리되므로,
        // Promise를 사용하여 성공과 실패 시의 처리를 정의합니다.
        // resolve()는 성공적인 응답이 왔을 때 호출되며, reject()는 에러가 발생했을 때 호출됩니다.

        $.ajax({
            type: 'GET', //HTTP GET 요청을 보냅니다.
            url: '/user/info', ///user/info 엔드포인트에서 사용자 정보를 가져옵니다.
            success: (response) => {
                resolve(response);
                // 요청이 성공하면 호출됩니다. 이때 서버에서 받은 응답(response)을 resolve()로 전달합니다.
            },
            error: (xhr) => {
                // 요청이 실패하면 호출됩니다. 실패한 이유를 xhr 객체로 받아옵니다.
                console.log('xhr :: ', xhr)

                if (xhr.status === 401) {
                    handleTokenExpiration()
                    // 만약 서버에서 401 Unauthorized 응답을 받았다면,
                    // 토큰 만료나 인증 실패를 의미합니다.
                    // 이 경우 handleTokenExpiration() 함수를 호출하여
                    // 토큰을 갱신하거나 로그인 페이지로 리디렉션할 수 있습니다.
                } else {
                    reject(xhr);
                    // 만약 다른 에러가 발생하면 reject()를 호출하여 에러를 처리합니다.
                }
            }
        })
    });
}

