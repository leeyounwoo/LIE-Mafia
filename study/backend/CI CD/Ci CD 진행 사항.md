# Ci/CD 진행 사항

Created: 2022년 1월 24일 오전 11:52
Last Edited Time: 2022년 1월 24일 오후 12:58

### 요약

- Jenkins in Docker
- Jenkins 과 git 연동
- Pipeline 생성
- 자동 빌드 진행
    - master branch에 push 발생시 자동으로 빌드 하게 설정
- 빌드 결과물을 docker Hub에 Push 진행
    - docker Container 안에 빌드 결과물 담기
- 빌드 결과를 Server에서 run
    - 추후 AWS EC2에서 run 할 수 있게 SSH 통신 이용으로 발전 예정

- 구성 모양

![Untitled](Ci%20CD%20%E1%84%8C%E1%85%B5%E1%86%AB%E1%84%92%E1%85%A2%E1%86%BC%20%E1%84%89%E1%85%A1%E1%84%92%E1%85%A1%E1%86%BC%208af60b2fc9b54d7e9e2633c193c37e0b/Untitled.png)

### Jenkins in Docker

- 개인용 Macos NAS 에서 진행 [2k2lc1p.iptime.org](http://2k2lc1p.iptime.org:8080/)에서 진행
- MacOs 인 관계로 MacOs Docker 10.5 사용
    
    도커에서 Jenkins run 하기 위해서 사용 명령어 
    
    ```java
    docker run -d \
       --name my-jenkins \
       -v /var/jenkins_home:~/.jenkins \
       -v /var/run/docker.sock:/var/run/docker.sock \
       -p 8080:8080 jenkins
    ```
    
- 8080번 포트에서 Jenkins을 띄우는 명령어를 사용해서
- docker run 해서 도커 안에서 Container로 띄움
- 이후 Pulgin 을 설치 하라는 것이 뜨는데
    - suggested plugins 라고 왼쪽에 있는 것을 설치!!
- 그 다음에 username과 password 설정 하는 창 에서 username과 password 설정
- [2k2lc1p.iptime.org](http://2k2lc1p.iptime.org:8080/):8080에 접속 해서 로그인 창 띄워지면 성공!
    
    ![Untitled](Ci%20CD%20%E1%84%8C%E1%85%B5%E1%86%AB%E1%84%92%E1%85%A2%E1%86%BC%20%E1%84%89%E1%85%A1%E1%84%92%E1%85%A1%E1%86%BC%208af60b2fc9b54d7e9e2633c193c37e0b/Untitled%201.png)
    

### Jenkins 접속 후 git 연동

- 이름과 비밀번호를 치고 들어와서
- Plugin Mange 들어가서 gitlab과 관련된 모든 것을 설치 하면 됨
- 그 다음 **System Configuration에 들어가서**

![Untitled](Ci%20CD%20%E1%84%8C%E1%85%B5%E1%86%AB%E1%84%92%E1%85%A2%E1%86%BC%20%E1%84%89%E1%85%A1%E1%84%92%E1%85%A1%E1%86%BC%208af60b2fc9b54d7e9e2633c193c37e0b/Untitled%202.png)

- gitLab Connection 진행
    - Connection name은 자유
    - Gitlab은 연동하려는 gitLab 주소 우리는 [https://lab.ssafty.com/](https://lab.ssafty.com/) 사용!
    - Credentails
        - 옆에 있는 Add를 눌려서 Jenkins 클릭 Kind에서 GitLab API token 선택
        - gitLab에 있는 user Settings에서 Access Tokens에 들어가서 Personal Access Tokens 발급 받기
            - 발급시 api 체크 하고 만료일을 최대한 길게 해서 발급 받기!!!
            - 주의
                
                 - Personal Access Token은 한번 발급 받으면 다시 확인이 불가능 하기 때문에 저장 해두기!!
                
        - 이 발급 받은 값으로 API Token을 넣어줌
- 아래에 있는 Test 버튼을 클릭해서 Succes 뜨면 연결이 완료

### Pipeline 생성

- 새로운 Item에서 Pipeline 클릭 해서 생성 하기
- Build Trigger에서 Build when a change is pushed to GitLab 부분 클릭

![Untitled](Ci%20CD%20%E1%84%8C%E1%85%B5%E1%86%AB%E1%84%92%E1%85%A2%E1%86%BC%20%E1%84%89%E1%85%A1%E1%84%92%E1%85%A1%E1%86%BC%208af60b2fc9b54d7e9e2633c193c37e0b/Untitled%203.png)

- web Hook 연동 진행
    - 고급을 눌려서 Secret token을 이용해서 Generate 을 통해서 복사
    
    ![Untitled](Ci%20CD%20%E1%84%8C%E1%85%B5%E1%86%AB%E1%84%92%E1%85%A2%E1%86%BC%20%E1%84%89%E1%85%A1%E1%84%92%E1%85%A1%E1%86%BC%208af60b2fc9b54d7e9e2633c193c37e0b/Untitled%204.png)
    
- git lab에서 연결을 진행 하려는 프로젝트로 이동해서

![Untitled](Ci%20CD%20%E1%84%8C%E1%85%B5%E1%86%AB%E1%84%92%E1%85%A2%E1%86%BC%20%E1%84%89%E1%85%A1%E1%84%92%E1%85%A1%E1%86%BC%208af60b2fc9b54d7e9e2633c193c37e0b/Untitled%205.png)

- URL 은 실행 중인 Jenkins URL/porject/Pipeline 명
    - 이런식으로 진행 함
    
    ![Untitled](Ci%20CD%20%E1%84%8C%E1%85%B5%E1%86%AB%E1%84%92%E1%85%A2%E1%86%BC%20%E1%84%89%E1%85%A1%E1%84%92%E1%85%A1%E1%86%BC%208af60b2fc9b54d7e9e2633c193c37e0b/Untitled%206.png)
    
- 이대로 Add WebHock을 더해서 눌려줌
- 여기서 Test 누르고 Push events 를 누르면

![Untitled](Ci%20CD%20%E1%84%8C%E1%85%B5%E1%86%AB%E1%84%92%E1%85%A2%E1%86%BC%20%E1%84%89%E1%85%A1%E1%84%92%E1%85%A1%E1%86%BC%208af60b2fc9b54d7e9e2633c193c37e0b/Untitled%207.png)

![Untitled](Ci%20CD%20%E1%84%8C%E1%85%B5%E1%86%AB%E1%84%92%E1%85%A2%E1%86%BC%20%E1%84%89%E1%85%A1%E1%84%92%E1%85%A1%E1%86%BC%208af60b2fc9b54d7e9e2633c193c37e0b/Untitled%208.png)

- 이런식으로 나오면 성공
- Pipleline Script로 진행 하기

```java
node{

    stage('init'){
        def dockerHome = tool 'myDocker'
        env.PATH = "${dockerHome}/bin:${env.PATH}"

    }

    stage('git clone'){

        git credentialsId: 'test_work', url: 'https://lab.ssafy.com/diakes/jenkins_test'
        // branch : master

    }

    stage('Mvn Package'){

        def mvnHome = tool name: 'mavenInit', type: 'maven'
        def mvnCMD = "${mvnHome}/bin/mvn"
        sh "${mvnCMD} clean package"

    }

    //after first build, add this step

    stage('clear previous docker image, containers'){

        sh 'docker stop backend'
        sh 'docker rm backend'
        sh 'docker rmi diakes/dockfile:1.0.0'

    }

    stage('build docker image'){

        sh 'docker build -t diakes/dockfile:1.0.0 .'

    }
    
    stage('PushDocker Image'){
    
        withCredentials([string(credentialsId: 'diakesPwds', variable: 'dockerHubPassword')]) {
            sh "docker login -u diakes -p ${dockerHubPassword}"
        }
        
        sh 'docker push diakes/dockfile:1.0.0'
    }
    

    stage('run container on server'){

        sh 'docker run -p 8082:8080 -d --name backend diakes/dockfile:1.0.0'

        // port forwarding 8082 port to 8080

    }
}
```

- pipeline 명령어 설명

  - 공식 사이트 : [https://www.jenkins.io/doc/book/pipeline/syntax/](https://www.jenkins.io/doc/book/pipeline/syntax/) 

  - 참고 사이트 : [https://jojoldu.tistory.com/356](https://jojoldu.tistory.com/356)

### 자동 빌드 진행

```java
stage('Mvn Package'){

        def mvnHome = tool name: 'mavenInit', type: 'maven'
        def mvnCMD = "${mvnHome}/bin/mvn"
        sh "${mvnCMD} clean package"

    }
```

이 부분에서 tool은 원하는 것을 선택 가능함.  type 을 통해 maven , gradle을 처리 

jenkins에 설치 된 maven, gradle을 통해서 빌드 진행 

- 현재 프로젝트에서 gradle을 사용 차후 build 부분은 gradle로 처리 함