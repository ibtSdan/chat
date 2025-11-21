# Chat

## 프로젝트 소개
Chat 프로젝트는 **WebSocket(STOMP)**을 활용한 실시간 채팅 프로젝트 입니다.

사용자가 채팅방에 접속해 메시지를 주고받는 기본 기능을 구현했으며, 채팅방의 **최대 인원 제한 기능**을 적용해 정해진 인원을 넘으면 입장이 불가능하도록 처리했습니다.

프로젝트는 AWS EC2에 배포되었으며, GitHub 기반 **CI/CD 자동 배포 흐름**을 구성하여 push 시 자동으로 서버에 배포되도록 설정했습니다.

<br>


## 기술 스택
- Java 21
- Spring Boot
- Spring WebSocket + STOMP
- AWS EC2
- CI/CD (GitHub Actions)

<br>


## 주요 기능
- STOMP 기반 실시간 메시지 송수신
- 사용자 입장 및 퇴장 처리
- 채팅방 최대 인원 제한
- GitHub Actions 기반 자동 배포 파이프라인

<br>



## 프로젝트 구조
```
src
 └── main
     └── java
         ├── config        // STOMP/WebSocket 설정
         ├── controller    // 메시지 처리 엔드포인트
         ├── dto           // 메시지 데이터 구조
         └── websocket     // 실시간 채팅 핵심 로직
              ├── service
              ├── interceptor
              ├── state
              └── listener
```