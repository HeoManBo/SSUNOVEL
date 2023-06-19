# Novel Forum [백엔드]

개요
---
- 오늘날에는 웹소설 연재 플랫폼이 굉장히 많아 어디에 어떤 작품이 올라왔는지 확인하기 어려운 상황입니다.

- 때문에, 어떤 플랫폼에 어떤 소설이 올라와 있는지 확인할 수 있는 사이트를 만들고자 합니다.

- 다만, 단순히 어디에 어떤 소설이 있는지 확인하는 것에서 그치지 않고, 나에게 맞는 소설을 추천해주는 기능을 비롯하여 다양한 기능을 가진 사이트를 만들어보고자 합니다.


목표
---
-	장르별로 소설을 나눠 인기 있는 작품 순서로 정렬해서 이용자에게 소설 정보를 제공하는 기능을 만들고자 합니다.
-	소설에 대한 리뷰 + 평점을 남길 수 있게 하여 해당 작품이 어떤 작품인지 확인할 수 있게 하고자 합니다. 또한, 작성한 리뷰를 바탕으로 추천 시스템을 개발하여 이용자별 맞춤 소설을 제공하고자 합니다.
-	소설이 어디에서 연재되고 있는지, 작가는 누구인지, 등의 정보를 제공하고자 합니다.
-	소설 각각에 대해 해당 소설과 유사한 소설 정보를 이용자에게 제공하고자 합니다.
-	추천하고 싶은 소설들을 모아놓은 보관함 기능과 이용자들 사이에서 소설 추천에 대해 의견을 주고받을 수 있는 커뮤니티를 제공하여, 이용자들 간의 소설 추천을 원활하게 할 수 있습니다


팀
---
숭실대학교 2023년 1학기 전공종합설계

프론트엔드

20170575 김현정

20180810 정지오

<br>
<br>
<br>

백엔드

20180736 김진수

20182607 류정훈


기술 스택
---
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">

<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">

<img src="https://img.shields.io/badge/swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=white">

<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">


시스템 구성도
---
![image](https://github.com/HeoManBo/SSUNOVEL/assets/88032932/8954e397-47b1-4535-b1f1-23e4791a2bc9)

-	웹 사이트에서 API 요청이 들어오면, Spring boot에서 추천을 제외한 내용을 처리합니다.
-	Spring boot에서는 RDS의 MySQL DB와 연결하여 API 요청에 필요한 정보를 가져옵니다.
-	일주일에 한번 씩 Spring boot에서는 @Scheduled를 이용해 FastAPI 서버로 크롤링 요청을 보냅니다.
-	FastAPI 서버는 요청을 받으면 그때부터 각 플랫폼에 대한 크롤링을 진행해 S3에 크롤링 결과 CSV 파일을 저장합니다.
-	FastAPI에서 S3로 파일을 저장하는데 성공했다면, Spring boot에서 S3에 있는 파일을 다운 받아 CSV 내용을 파싱해서 RDS의 MySQL DB에 넣습니다.
-	MySQL DB가 업데이트 되었다면, Spring boot에서 FastAPI로 줄거리 기반 유사 소설 리스트 생성 요청을 보냅니다.
-	FastAPI에서 요청을 받으면, Sentence-Bert를 사용해서 줄거리 기반의 소설 추천 리스트를 만들어 RDS의 MySQL DB에 저장합니다.
-	추천 서비스 사용시 MF알고리즘을 이용하여 예상 평점 10위에 드는 소설을 반환합니다. 



DB 설계도
---
![image](https://github.com/HeoManBo/SSUNOVEL/assets/88032932/cfbe6954-c4c6-43ab-befd-b8777c4fa819)


문서화
---
- Swagger를 사용해서 문서화를 진행했습니다.
- 코드 실행 후, http://localhost:8080/swagger-ui/index.html 에서 api 내용을 확인할 수 있습니다.
- API의 구조는 다음과 같습니다.
- ![image](https://github.com/HeoManBo/SSUNOVEL/assets/88032932/b10c1af8-47f0-45d6-8735-db6fe837cfeb)
- ![image](https://github.com/HeoManBo/SSUNOVEL/assets/88032932/03988a55-f0bb-4946-aeb4-efde6cb44989)

