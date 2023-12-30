<div align="">

# ChatGPT가 탑재된 컨테이너 기반 협업용 Web IDE

> 구름톤 트레이닝 풀스택 개발자 과정 <br>
> 개발 기간 : 2023.12.01 ~ 2023.12.28

## 배포 주소
> 백엔드 서버 : https://www.thegreatide.site <br>
> 프론트엔드 서버 : https://the-greate-ide.vercel.app
> 
## 팀 소개

<table width="500" align="center">
<tbody>
<tr>
<th>Pictures</th>
<td width="100" align="center">
<a href="https://github.com/sbslc2000">
<img src="https://avatars.githubusercontent.com/u/60257970?v=4" width="60" height="60">
</a>
</td>
<td width="100" align="center">
<a href="https://github.com/leebongseung">
<img src="https://avatars.githubusercontent.com/u/101985441?v=4" width="60" height="60">
</a>
</td>
<td width="100" align="center">
<a href="https://github.com/HJunng">
<img src="https://avatars.githubusercontent.com/u/56528404?v=4" width="60" height="60">
</a>
</td>
<td width="100" align="center">
<a href="https://github.com/charmingGyu">
<img src="https://avatars.githubusercontent.com/u/133394457?v=4" width="60" height="60">
</a>
</td>
</tr>
<tr>
<th>Name</th>
<td width="100" align="center">서범석</td>
<td width="100" align="center">이봉승</td>
<td width="100" align="center">임현정</td>
<td width="100" align="center">차민규</td>

</tr>
<tr>
<th>Role</th>
<td width="150" align="center">
컨테이너 관리 및 오케스트레이션 지원<br>
</td>
<td width="150" align="center">
STOMP 웹소켓 
모듈 개발, 
채팅 서비스 제공<br>
</td>
<td width="150" align="center">
소셜 로그인 및 인증 인가, HTTPS 적용 및 서버 환경 설정
<br>
</td>
<td width="150" align="center">
프로젝트 파일 및 디렉터리 관리 API 개발, ChatGPT API 개발
<br>
</td>
</tr>
<tr>
<th>GitHub</th>
<td width="100" align="center">
<a href="https://github.com/sbslc2000">
<img src="http://img.shields.io/badge/sbslc2000-green?style=social&logo=github"/>
</a>
</td>
<td width="100" align="center">
<a href="https://github.com/leebongseung">
<img src="http://img.shields.io/badge/leebongseung-green?style=social&logo=github"/>
</a>
</td>
<td width="100" align="center">
<a href="https://github.com/HJunng">
<img src="http://img.shields.io/badge/HJunng-green?style=social&logo=github"/>
</a>
</td>
<td width="100" align="center">
<a href="https://github.com/charmingGyu">
<img src="http://img.shields.io/badge/charmingGyu-green?style=social&logo=github"/>
</a>
</td>

</tr>
</tbody>
</table>

## 프로젝트 소개

### 🔍 프로젝트 컨셉
![](https://i.imgur.com/QCd2sfQ.png)

### 🔍 아키텍처
![](https://i.imgur.com/6LPYDSF.png)

### 🔍 기술 스택
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">

<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">

<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/NGINX-009639?style=for-the-badge&logo=NGINX&logoColor=white">
<img src="https://img.shields.io/badge/hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white">

<img src="https://img.shields.io/badge/spring security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white">
<img src="https://img.shields.io/badge/jwt-000000?style=for-the-badge&logo=JSONWebTokens&logoColor=white">
<img src="https://img.shields.io/badge/Amazon ECS-FF9900?style=for-the-badge&logo=amazonecs&logoColor=white">
<img src="https://img.shields.io/badge/Amazon RDS-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white">
<img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white">


## 주요 기능
### ✨ 소셜 로그인 
소셜 로그인을 통해 사용자는 번거로운 회원가입 / 로그인 과정을 거치지 않고 서비스를 이용할 수 있습니다. Spring Security, JWT, OAuth2를 사용했으며, 통신간 암호화를 위해 HTTPS를 적용했으며 리프레시 토큰을 1회성으로 사용하게 만드는 Rotation 기술을 도입했습니다.
### ✨ 프로젝트 단위의 개발 환경 제공
사용자는 여러 프로젝트를 만들고, 소스코드를 작성하며 실행시킬 수 있습니다. 모든 내용은 저장되며, 다른 사용자를 초대해 함께 소스코드를 수정할 수 있습니다. 각 프로젝트 개발환경은 ECS를 통해 컨테이너로 관리됩니다.
### ✨ 다양한 언어 지원 (Python, Java, CPP)
프로젝트 생성 시점에 사용할 언어를 선택할 수 있습니다. 선택한 언어를 통해 컨테이너 이미지를 생성하여 사용자에게 개발 환경을 제공합니다.
### ✨ 채팅
IDE의 프로젝트에 참여해있는 사용자간에 텍스트를 주고 받을 수 있는 기능입니다. 사용자의 입/퇴장시에 '@@@님이 입장 | 퇴장 했습니다.' 라는 문구가 보여집니다. 구독 - 발행 모델을 적극적으로 사용하기 위해 STOMP를 사용했습니다. 
### ✨ 터미널
터미널을 통해 프로젝트 파일에 대한 조작을 할 수 있으며 프로그램을 수행시킬 수 있습니다. ECS Exec을 사용했습니다.
### ✨ ChatGPT를 통한 코드 리뷰 및 질문
현재 작성중인 소스코드에 대한 코드 리뷰를 간편하게 받을 수 있으며, 질문이 있는 경우 내장되어있는 ChatGPT 기능을 통해 결과를 받아 볼 수 있습니다.

## 데모 영상 

### ✨ 프로젝트 생성
![](https://i.imgur.com/PiEwntJ.gif)

### ✨ 프로젝트 입장
![](https://i.imgur.com/u8PjkEN.gif)

### ✨동시 편집
![](https://i.imgur.com/4VSu0vS.gif)

### ✨ 파일트리
![](https://i.imgur.com/ZvOswWB.gif)

### ✨ 채팅
![](https://i.imgur.com/Hu4KZQv.gif)

### ✨ 터미널
![](https://i.imgur.com/jeUx55A.gif)

### 챗지피티
![](https://i.imgur.com/6D1kcfn.gif)


</div>
