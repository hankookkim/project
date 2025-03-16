### **회원가입, 로그인**
![login](https://github.com/user-attachments/assets/a67acc32-7234-4441-aa22-7fddd39422b8)



### **1. 회원가입 과정**

회원가입 과정은 사용자가 자신의 정보를 제공하고, 이를 시스템에 저장하여 인증을 위한 토큰을 발급받을 수 있도록 합니다.

### **1.1. 사용자 정보 입력 (회원가입)**

- 사용자는 **아이디**, **비밀번호**, **이름** 등의 정보를 입력하여 회원가입을 시도합니다.
- 클라이언트는 이러한 정보를 **POST** 요청으로 서버에 보냅니다.

### **1.2. 비밀번호 암호화**

- 서버에서 받은 **비밀번호**는 암호화하여 저장해야 합니다.
- Spring Security와 같은 라이브러리를 사용하여 **BCrypt** 방식으로 비밀번호를 암호화합니다.
- 이렇게 암호화된 비밀번호는 **데이터베이스**에 저장됩니다.

### **1.3. 사용자 정보 저장**

- **Member** 객체를 생성하여, 사용자가 입력한 정보를 저장합니다.
- 데이터베이스에 **Member** 객체를 저장하여 사용자 정보를 관리합니다.

### **1.4. 성공적인 회원가입 응답**

- 회원가입이 성공하면, 서버는 성공 메시지를 클라이언트에게 반환합니다. 예를 들어:
    - `회원가입 성공`
    - HTTP 상태 코드 **201 CREATED**



### **2. 로그인 과정**

로그인 과정은 사용자가 입력한 **아이디**와 **비밀번호**로 인증을 받고, 인증된 사용자에게 **Access Token**과 **Refresh Token**을 발급하는 과정입니다.

### **2.1. 사용자 아이디와 비밀번호 입력**

- 사용자는 로그인 화면에서 **아이디**와 **비밀번호**를 입력합니다.
- 클라이언트는 이 정보를 **POST** 요청으로 서버에 전달합니다.

### **2.2. 아이디와 비밀번호 검증**

- 서버는 받은 **아이디**를 **데이터베이스**에서 검색하여, 해당 사용자가 존재하는지 확인합니다.
- 사용자가 존재하면, 입력된 **비밀번호**와 **데이터베이스에 저장된 비밀번호**를 비교하여 일치하는지 확인합니다.
    - **비밀번호** 비교는 **BCrypt** 라이브러리를 통해 암호화된 비밀번호를 비교하는 방식으로 처리됩니다.

### **2.3. 토큰 발급**

- **아이디**와 **비밀번호**가 일치하면, 사용자는 인증에 성공하고, 서버는 **Access Token**과 **Refresh Token**을 발급합니다.
    - **Access Token**은 사용자가 인증된 요청을 보낼 수 있도록 합니다. 일반적으로 **짧은 유효 기간** (예: 1시간)을 설정합니다.
    - **Refresh Token**은 Access Token이 만료되었을 때, 새로운 Access Token을 발급받기 위해 사용됩니다. **긴 유효 기간** (예: 7일)으로 설정됩니다.

### **2.4. 토큰 반환**

- 발급된 **Access Token**은 **Authorization** 헤더에 포함되어 클라이언트에게 반환됩니다.
- **Refresh Token**은 **쿠키**에 저장되어, 클라이언트가 이후 요청에서 이를 사용할 수 있도록 합니다.



### **3. 토큰을 사용한 인증과 권한 관리**

### **3.1. 인증된 요청**

- 클라이언트는 **Access Token**을 HTTP 요청의 **Authorization** 헤더에 포함시켜 서버로 보냅니다.
- 서버는 **Access Token**을 검증하여 요청이 인증된 사용자의 것인지 확인합니다.

### **3.2. Access Token 만료 시**

- **Access Token**이 만료되면, 클라이언트는 **Refresh Token**을 사용하여 새로운 **Access Token**을 발급받을 수 있습니다.
    - `/refresh-token` API를 호출하여 **Refresh Token**을 서버로 보내면, 서버는 해당 **Refresh Token**을 검증하고, 유효하면 새로운 **Access Token**과 **Refresh Token**을 발급합니다.
    - 새로운 **Access Token**은 **Authorization** 헤더에 포함되어 응답으로 반환되고, 새로운 **Refresh Token**은 **쿠키**에 저장됩니다.



### **4. 로그아웃 과정**

### **4.1. 로그아웃 요청**

- 사용자가 로그아웃을 요청하면, 클라이언트는 **POST** 방식으로 `/logout` API에 요청을 보냅니다.

### **4.2. 쿠키와 세션 삭제**

- 서버는 클라이언트에게 전달된 **Refresh Token**을 **쿠키**에서 삭제합니다.
- 클라이언트는 이후 로그인을 다시 해야만 새로운 **Access Token**과 **Refresh Token**을 발급받을 수 있습니다.



### **5. 회원가입, 로그인, 로그아웃 API 흐름**

### **5.1. 회원가입**
- 클라이언트는 회원가입 정보를 **POST** 방식으로 서버에 전송합니다.
- 서버는 받은 정보를 암호화하여 **데이터베이스**에 저장하고, 성공 응답을 보냅니다.
### **5.2. 로그인**
- 클라이언트는 **아이디**와 **비밀번호**를 **POST** 방식으로 서버에 보냅니다.
- 서버는 **아이디**와 **비밀번호**를 검증하고, 유효하면 **Access Token**과 **Refresh Token**을 발급하여 클라이언트에게 반환합니다.
### **5.3. 인증된 요청**
- 클라이언트는 **Access Token**을 **Authorization** 헤더에 포함시켜 API 요청을 보냅니다.
- 서버는 **Access Token**을 검증하고, 인증된 요청인지 확인한 후 응답을 반환합니다.
### **5.4. Refresh Token을 이용한 Access Token 갱신**
- **Access Token**이 만료되면, 클라이언트는 **Refresh Token**을 서버로 보내어 새로운 **Access Token**을 요청합니다.
- 서버는 **Refresh Token**을 검증하고, 새로운 **Access Token**과 **Refresh Token**을 발급하여 클라이언트에게 반환합니다.
### **5.5. 로그아웃**
- 클라이언트는 로그아웃을 요청하고, 서버는 **Refresh Token**을 쿠키에서 삭제하여 사용자가 인증되지 않은 상태로 만듭니다.

---

### **요약**

- **회원가입**: 사용자가 정보를 제공하고, 서버는 이를 암호화하여 데이터베이스에 저장합니다.
- **로그인**: 사용자가 **아이디**와 **비밀번호**로 인증을 받으며, **Access Token**과 **Refresh Token**을 발급받습니다.
- **Access Token**과 **Refresh Token**은 인증을 유지하는 핵심 요소로, **Access Token**은 짧은 유효 기간을, **Refresh Token**은 긴 유효 기간을 가집니다.
- **Refresh Token**을 사용하여 만료된 **Access Token**을 갱신할 수 있습니다.
- **로그아웃** 시에는 **Refresh Token**이 쿠키에서 삭제되어, 사용자는 인증 상태를 유지할 수 없습니다.

---

### **게시글 저장 **

![게시판](https://github.com/user-attachments/assets/3a0aa1f4-1772-49e8-ab99-77e8256aef66)


----
### **1. 게시글 저장 과정**

### **1.1. 사용자 게시글 작성**

- 사용자는 게시판에서 **제목**과 **내용**을 입력하고, 게시글을 저장하려면 **저장** 버튼을 클릭합니다.
- 사용자가 입력한 **제목**과 **내용**은 클라이언트에서 변수에 저장됩니다. 추가적으로, 현재 로그인한 **사용자 정보**(작성자 정보)도 함께 저장됩니다. 이는 게시글에 작성자 정보를 포함시키기 위해 필요합니다.

### **1.2. 클라이언트에서 AJAX 요청 보내기**

- 클라이언트는 **AJAX** 요청을 사용하여 작성한 게시글을 서버로 전송합니다.
- 게시글 데이터는 **JSON 형식**으로 변환되어 **POST** 방식으로 서버의 게시글 저장 엔드포인트로 전송됩니다.
- 요청 시 **Authorization 헤더**에 **JWT 토큰**을 포함시켜, 서버가 사용자를 인증할 수 있도록 합니다.
- 클라이언트는 서버로부터 성공 응답을 받으면, **게시글 저장 성공** 메시지를 표시하고, 게시판 목록 페이지로 리디렉션합니다.

### **1.3. 서버에서 요청 처리**

- 서버는 클라이언트로부터 받은 게시글 정보를 처리합니다. 게시글 정보는 `@RequestBody`를 사용하여 해당 **Dto** 객체로 변환됩니다.
- 서버는 **JWT 토큰**을 통해 현재 로그인한 사용자의 정보를 추출하여, 게시글의 작성자 정보로 설정합니다.
- 서버는 게시글 저장을 담당하는 **BoardService**를 호출하여 게시글을 데이터베이스에 저장합니다.

### **1.4. 서비스에서 게시글 저장**

- **BoardService**는 게시글 데이터를 **BoardMapper**로 전달하여 데이터베이스에 삽입합니다.
- **INSERT INTO board** SQL 쿼리로 게시글 정보를 **Article** 테이블에 저장합니다.
- 게시글이 성공적으로 저장되면 서버는 **성공 응답**을 클라이언트로 반환합니다.

### **1.5. 클라이언트 응답 처리**

- 클라이언트는 서버로부터 받은 **성공 응답**을 처리하고, 사용자에게 게시글이 성공적으로 저장되었음을 알립니다.
- 게시글 저장 후, 클라이언트는 사용자를 **게시판 목록 페이지**로 리디렉션하여 새로 작성된 게시글을 확인할 수 있게 합니다.

### **1.6. 결과**

- 사용자가 게시글을 작성하고 **저장** 버튼을 클릭한 후, 서버에 게시글 정보가 저장됩니다.
- 저장된 게시글은 **Article** 테이블에 기록되며, 사용자는 게시판 목록에서 새로 작성된 게시글을 확인할 수 있습니다.

---
### **게시글 수정 **

![게시판 수정](https://github.com/user-attachments/assets/3be671d0-bc0e-4461-9a10-6e49dbcd09dc)


---


### **2. 게시글 수정 과정**

### **2.1. 게시글 수정 요청**

- 사용자는 기존에 작성한 게시글을 수정하려면 게시글의 **수정** 버튼을 클릭합니다.
- 수정 화면이 열리면, 기존 게시글의 **제목**과 **내용**이 입력 필드에 자동으로 채워집니다. 사용자는 이를 수정한 후, **수정 완료** 버튼을 클릭합니다.
- 사용자가 수정한 **제목**과 **내용**은 클라이언트에서 변수에 저장되고, 게시글을 수정할 수 있도록 서버에 요청을 보냅니다.

### **2.2. 클라이언트에서 AJAX 요청 보내기**

- 클라이언트는 **AJAX** 요청을 사용하여 수정된 게시글 정보를 서버로 전송합니다.
- 게시글 수정 요청은 **PUT** 방식으로 보내며, **게시글 ID**와 수정된 **제목**과 **내용**을 함께 전송합니다.
- 요청 시 **Authorization 헤더**에 **JWT 토큰**을 포함시켜, 서버가 사용자를 인증할 수 있도록 합니다.
- 클라이언트는 서버로부터 성공 응답을 받으면, **게시글 수정 성공** 메시지를 표시하고, 게시판 목록 페이지나 수정된 게시글 페이지로 리디렉션합니다.

### **2.3. 서버에서 수정 요청 처리**

- 서버는 클라이언트로부터 받은 **게시글 ID**와 **수정된 제목, 내용**을 처리합니다.
- 서버는 **JWT 토큰**을 통해 사용자의 인증 정보를 확인하고, 수정하려는 게시글의 **작성자**가 요청한 **사용자**와 일치하는지 확인합니다.
- 작성자가 맞으면, 서버는 **BoardService**를 호출하여 게시글의 제목과 내용을 업데이트합니다.

### **2.4. 서비스에서 게시글 수정**

- **BoardService**는 수정된 게시글 정보를 **BoardMapper**로 전달하여 **Article** 테이블에 있는 기존 게시글을 수정합니다.
- **MySQL**를 사용하여 게시글을 수정합니다.
- 서버는 수정이 완료되면 **성공 응답**을 클라이언트로 반환합니다.

### **2.5. 클라이언트 응답 처리**

- 클라이언트는 서버로부터 받은 **성공 응답**을 처리하고, 사용자에게 게시글이 성공적으로 수정되었음을 알립니다.
- 게시글 수정 후, 클라이언트는 사용자를 **게시판 목록 페이지**나 수정된 **게시글 페이지**로 리디렉션합니다.

### **2.6. 게시글 수정 실패 처리**

- 서버는 요청한 사용자가 **게시글 작성자**가 아닐 경우, **403 Forbidden** 응답을 반환하여 수정 권한이 없음을 알립니다.
- 클라이언트는 이를 처리하여 사용자에게 **수정 권한이 없음을** 알리고, 해당 게시글 수정이 불가능하다는 메시지를 표시합니다.

### **2.7. 결과**

- 사용자가 **수정 완료** 버튼을 클릭하여 수정한 게시글 정보는 서버에 성공적으로 반영되고, 데이터베이스에서 **Article** 테이블에 업데이트됩니다.
- 수정된 게시글은 **게시판 목록** 또는 **게시글 상세 페이지**에서 확인할 수 있습니다.

