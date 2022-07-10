# **꿀벌 프로젝트**


## **:bulb: The purpose of a project**
    꿀벌 커뮤니티 내 채팅, 음성 서비스
  
<br>

---

## **Stack**
    Java : 11
    SpringBoot : 2.6.3
    JPA : 2.6.1
    Redis : 6.2.6
    Mysql : 5.7
    Docker : 20.10.12
    AWS EC2
    OpenVidu

<img src="https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=MySQL&logoColor=white"/> <img src="https://img.shields.io/badge/Redis-DC382D?style=flat&logo=Redis&logoColor=white"/> <img src="https://img.shields.io/badge/Java-007396?style=flat&logo=Java&logoColor=white"/> <img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=flat&logo=Spring-Boot&logoColor=white"/> <img src="https://img.shields.io/badge/Docker-2496ED?style=flat&logo=Docker&logoColor=white"/> <img src="https://img.shields.io/badge/JPA-FF3621?style=flat&logo=Databricks&logoColor=white"/> 
<img src="https://img.shields.io/badge/Nginx-009639?style=flat&logo=Nginx&logoColor=white"/> <img src="https://img.shields.io/badge/OpenVidu-white?style=flat&logo=WebRTC&logoColor=yellow"/>
<br>

---

## **Service Architecture**
<img width="1680" alt="image" src="https://user-images.githubusercontent.com/76645095/178147070-63abae91-2d77-42a8-857c-2d41a0ce8747.png">


---

<br>

## **주요기능**

> ## **Web Server**
- ### **Architecture**

<img width="991" alt="image" src="https://user-images.githubusercontent.com/76645095/177087679-067e2c36-45ca-40ef-aced-3ebc64bd9d7d.png">

```
가장 앞단에 위치한 Nginx 는 Reverse Proxy 역할로, 소켓 및 HTTP 요청을 로드밸런싱 해준다. 

proxy 서버로는 소켓 통신을 사용하는 chat-service 서버와 음성 채팅 세션을 관리하는 voice-service 서버를 두었다.
```

```
OpenVidu는 내장 NginX를 가지고 있다. 
기존의 NginX의 포트와 OpenVidu의 포트에서 충돌이 발생했고, 기존 NginX의 포트를 81로 변경했다.
OpenVidu NginX를 custom했고, Main NginX의 설정과 하나의 파일로 관리했다.
```

1. HTTP Request 를 올바른 서비스로 매핑한다.

2. web-socket 통신을 위해 http header를 upgrade 한다.

<br>

> ## **Auth-Service**
- ### **Architecture**

<img width="974" alt="image" src="https://user-images.githubusercontent.com/76645095/177088134-a15cafaa-85eb-4a9c-ac3f-c5a02a31677d.png">

```
Auth-Service는 외부에서 직접 접근할 수없고, chat, voice service에서 request를 보내 사용자가 인증되었는지 응답한다.
Auth-Service는 초기에 각 서비스에 모놀리식으로 포함되어 있었다. 하지만, 서비스가 추가되거나 jwt token claim이 변경될 수있다. 
모놀리식으로 구축하면 모든 서비스의 인증 로직을 변경해야 한다. 이러한 이유로 인증 서비스를 분리하게 되었다.
```

```
인증 서비스는 가장 많은 트래픽을 받는다. 모든 서비스가 사용자 인증을 수행하기 때문이다. 
사용자 인증이 필요할때 마다 외부 DB에 접근하는 것은 사용자가 서비스의 지연을 느끼게 만든다.
또한 실시간성이 중요한 채팅에서도 매번 사용자를 인증하기 때문에 reqeust 처리 시간을 줄일 필요성을 느꼈다.

redis를 캐시로 사용하는 방법을 사용했다. 최근에 해당 사용자가 인증을 받았는데 또 인증받기 위해 DB에 접근하는 시간을 줄이기 위해서다. 
Auth-Service는 사용자 인증 요청이 들어오면 가장 먼저 redis 캐시를 확인한다. 만약 캐시에 유저 정보가 없으면 DB에 접근하고 response를 보낸다.
```

1. 사용자 인증 서비스를 독립적으로 분리했다.

2. request를 처리하는 시간을 줄이기 위해 redis를 캐시로 활용했다.


> ## **Chat-Service**
- ### **Architecture**

<img width="985" alt="image" src="https://user-images.githubusercontent.com/76645095/177088960-8532abd7-57ac-4852-aad7-3eafb909559d.png">

```
소켓 통신을 위한 서브 프로토콜로 STOMP를 사용했다. 
STOMP를 이용한 메시지 전달은 redis Publish/Subscribe 기능을 활용했고, 채널별로 message를 구별하기위해 redis에서 ChannelTopic을 관리했다.
```

```
채널은 수명을 가지고 있다. 수명은 사용자의 point를 활용해 증가시킬 수있다.
수명이 다하면 자동으로 채널이 닫힌다. 이를 구현하기 위해 redis의 TTL을 custom 했다.
```

```
모든 사용자는 채팅 로그를 요청할 수있다.
한번에 모든 채팅 로그를 불러오는 것은 비효율적이다. 
무한 스크롤을 구현하여 사용자가 request를 보내면 특정 개수만큼 채팅 로그를 전송했다.
```

1. redis 자료구조를 이해하고 pub/sub system과 STOMP를 사용했다.

2. redis의 TTL을 custom 했다.

3. 무한 스크롤을 구현해 채팅 로그를 N개씩 보내도록 구현했다.


> ## **Voice-Service**
- ### **Architecture**

<img width="992" alt="image" src="https://user-images.githubusercontent.com/76645095/177089724-36818b19-c7c7-4a5a-ad50-ca32b8562be3.png">

```
OpenVidu 서버와 통신을 담당하는 서비스이다. 
```

```
OpenVidu에 세션을 생성하고 세션별 사용자의 토큰을 발급받는다.
세션에 이미 참여한 사용자를 저장하기 위해 세션 저장소로 redis를 활용했다.
redis를 사용한 이유는 token 발급 서버가 여러대 증설되었을때, 데이터 일광성을 유지하기 위해 외부 저장소를 사용하기로 결정했다.
```

1. redis를 외부 session 저장소로 사용했다.

2. OpenVidu 라이브러리를 뜯어보고 사용했다.
