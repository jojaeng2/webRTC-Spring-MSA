# WebRTC v0.5 API Specifications and Usage

## Summary  
Version that allows socket connection without JWTtoken  

## WebSocket Stomp API Preview
|Method|URL|Purpose|
|:---:|:---:|:---:|
|ws|http://localhost:8080/ws-stomp|Stomp Endpoint URL|
|ws|http://localhost:8080/sub/chat/room/{channelId}|Subscription to a specific channel to receive messages.|
|ws|http://localhost:8080/pub/chat/room|Publish messages to specific channels|

## Request and Response

> http://localhost:8080/ws-stomp  
>> Request Body  
>> ```
>> null
>> ``` 
>> Response Body  
>> ```
>> null
>> ```  
  
> http://localhost:8080/sub/chat/room/{channelId}  
>> Request Body  
>> ```
>> null
>> ``` 
>> Response Body  
>> ```
>> null
>> ```  
  

> http://localhost:8080/pub/chat/room  
>  Situation ENTER  
>> Request Body  
>> ```JavaScript
>> {
>>     type : "ENTER",
>>     channelId : "channelId",
>>     senderName : "senderName",
>>     message : null 
>> }
>> ``` 
>> Response Body  
>> ```JavaScript
>> {
>>     type : "RENEWAL",
>>     channelId : "channelId",
>>     senderName : "[알림] ",
>>     message : "님이 입장했습니다.",
>>     userCount : userNum,
>>     users : []
>> }
>> ```  
>  Situation CHAT  
>> Request Body  
>> ```JavaScript
>> {
>>     type : "CHAT",
>>     channelId : "channelId",
>>     senderName : "senderName",
>>     message : "message" 
>> }
>> ``` 
>> Response Body  
>> ```JavaScript
>> {
>>     type : "CHAT",
>>     channelId : "channelId",
>>     senderName : "username",
>>     chatMessage : "message"
>> }
>> ```  
>  Situation EXIT  
>> Request Body  
>> ```JavaScript
>> {
>>     type : "EXIT",
>>     channelId : "channelId",
>>     senderName : "senderName",
>>     message : null 
>> }
>> ``` 
>> Response Body  
>> ```JavaScript
>> {
>>     type : "RENEWAL",
>>     channelId : "channelId",
>>     senderName : "[알림] ",
>>     message : "님이 퇴장했습니다.",
>>     userCount : userNum,
>>     users : []
>> }
>> ``` 
  
## Instruction for use  
    Order each time the spring server is switched off and on  
> Run the spring server.
>> <img width="1366" alt="image" src="https://user-images.githubusercontent.com/76645095/163328385-e9c1d348-7533-4872-9c01-2d305616a43c.png"> 
  
> Initialize the redis repository
>> ![image](https://user-images.githubusercontent.com/76645095/163328655-2bbee0bf-b6f0-4f40-b6d8-bff12e7db565.png)   
  
> Create Channel
>> Request URL
>> ```
>> http://localhost:8080/api/v1/webrtc/channel
>> ```
>> Request Body Example
>> ```JavaScript
>> {
>>    "channelName" : "E",
>>    "limitParticipants" : 15,
>>    "hashTags" : ["hello", "나도", "다니고싶다"]
>> }
>>```
>> Response Body Example
>> ```JavaScript
>> {
>>    "type": "SUCCESS",
>>    "channelName": "E",
>>    "limitParticipants": 15,
>>    "currentParticipants": 1,
>>    "timeToLive": 86400
>> }
>>```
> Check channelId output from spring server 
>> <img width="556" alt="image" src="https://user-images.githubusercontent.com/76645095/163330824-f6f1862d-e4d1-4d97-8a4b-bed7ebc5d776.png">
> Use channelId in React Code
>> ```JavaScript
>> import React, { useEffect, useState } from 'react'
>> import {over} from 'stompjs';
>> import SockJS from 'sockjs-client';
>> 
>> var stompClient =null;
>> const ChatRoom = () => {
>>    const [privateChats, setPrivateChats] = useState(new Map());     
>>    const [publicChats, setPublicChats] = useState([]); 
>>    const [tab,setTab] =useState("CHATROOM");
>>    const [userData, setUserData] = useState({
>>        username: '',
>>        receivername: '',
>>        connected: false,
>>        message: ''
>>      });
>>    useEffect(() => {
>>      console.log(userData);
>>    }, [userData]);
>>
>>    const connect =()=>{
>>        let Sock = new SockJS('http://localhost:8080/ws-stomp');
>>        stompClient = over(Sock);
>>        stompClient.connect({},onConnected, onError);
>>    }
>>
>>    const onConnected = () => {
>>        setUserData({...userData,"connected": true});
>>        stompClient.subscribe('/sub/chat/room/' + "bece0c1e-36aa-4b07-9121-27b94bb8e2e2", onMessageReceived);
>>        userJoin();
>>    }
>>
>>    const userJoin=()=>{
>>          var chatMessage = {
>>            type:"ENTER",
>>            channelId : "bece0c1e-36aa-4b07-9121-27b94bb8e2e2",
>>            senderName : "123",
>>            message : "message"
>>          };
>>          stompClient.send("/pub/chat/room", {}, JSON.stringify(chatMessage));
>>    }
>>
>>    const onMessageReceived = (payload)=>{
>>        var payloadData = JSON.parse(payload.body);
>>        console.log(payload)
>>        // switch(payloadData.status){
>>        //     case "JOIN":
>>        //         if(!privateChats.get(payloadData.senderName)){
>>        //             privateChats.set(payloadData.senderName,[]);
>>        //             setPrivateChats(new Map(privateChats));
>>        //         }
>>        //         break;
>>        //     case "MESSAGE":
>>        //         publicChats.push(payloadData);
>>        //         setPublicChats([...publicChats]);
>>        //         break;
>>        // }
>>    }
>>    
>>    const onError = (err) => {
>>        console.log(err);        
>>    }
>>
>>    const handleMessage =(event)=>{
>>        const {value}=event.target;
>>        setUserData({...userData,"message": value});
>>    }
>>    const sendValue=()=>{
>>            if (stompClient) {
>>              var chatMessage = {
>>                type: "CHAT",
>>                channelId: "bece0c1e-36aa-4b07-9121-27b94bb8e2e2",
>>                senderName: userData.senderName,
>>                message:userData.message
>>              };
>>              console.log(chatMessage);
>>              stompClient.send("/pub/chat/room", {}, JSON.stringify(chatMessage));
>>              setUserData({...userData,"message": ""});
>>            }
>>    }
>>
>>
>>    const registerUser=()=>{
>>        connect();
>>    }
>> ```
> React code execution
>> ![image](https://user-images.githubusercontent.com/76645095/163331943-a9535c50-4b3b-481b-aeb2-2b01157182a1.png)
> Put a temporary value in input and click connect button
> Open the Network tab in the Developer window to see if you receive messages from the server
>> ![image](https://user-images.githubusercontent.com/76645095/163332242-e9fcc83c-a2dd-4537-9dee-049695effbad.png)
