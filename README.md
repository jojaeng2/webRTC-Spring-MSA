# WebRTC v0.5 API Specifications and Usage

## Summary  
How to use the chat application with jwt access token in webrtc v0.5  

## WebSocket Stomp API Preview
|Method|URL|Purpose|
|:---:|:---:|:---:|
|ws|http://localhost:8080/ws-stomp|Stomp Endpoint URL|
|ws|http://localhost:8080/sub/chat/room/{channelId}|Subscription to a specific channel to receive messages.|
|ws|http://localhost:8080/pub/chat/room|Publish messages to specific channels|
|POST|http://localhost:8080/register|register user|
|POST|localhost:8080/api/v1/webrtc/authenticate|Be issued Jwt Access Token|

## Request and Response

> http://localhost:8080/ws-stomp  
>> Request Body  
>> ```JavaScript
>> {
>>      jwt : "jwt-token",
>>      username : "username"   
>> }
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
>>     chatMessage : "님이 입장했습니다.",
>>     currentParticipants : "userNum",
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
>>     chatMessage : "chatMessage",
>>     currentParticipants : "userNum",
>>     users : []
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
>>     chatMessage : "님이 퇴장했습니다.",
>>     currentParticipants : "userNum",
>>     users : []
>> }
>> ```   
> http://localhost:8080/register  
>> Request Body  
>> ```JavaScript
>> {
>>    "nickname" : "user",
>>    "password" : "password"
>> }
>> ``` 
>> Response Body  
>> ```JavaScript
>> {
>>    "id": "0df06625-ae43-44f1-a8b0-cf59d781acb0",
>>    "created_at": null,
>>    "updated_at": null,
>>    "email": null,
>>    "password": "null",
>>    "birthdate": null,
>>    "phone_number": null,
>>    "school": null,
>>    "company": null,
>>    "nickname": "user",
>>    "nickname_expire_at": null
>> }
>> ```  
>> http://localhost:8080/authenticate  
>> Request Body  
>> ```JavaScript
>> {
>>    "nickname" : "user",
>>    "password" : "password"
>> }
>> ``` 
>> Response Body  
>> ```JavaScript
>>{
>>    "jwttoken": "token"
>> }
>> ```    

  
## Instruction for use  
    Order each time the spring server is switched off and on  
> Run the spring server.
>> <img width="1366" alt="image" src="https://user-images.githubusercontent.com/76645095/163328385-e9c1d348-7533-4872-9c01-2d305616a43c.png"> 
  
> Initialize the redis repository
>> ![image](https://user-images.githubusercontent.com/76645095/163328655-2bbee0bf-b6f0-4f40-b6d8-bff12e7db565.png)   
  
> Create User
>> <img width="1015" alt="image" src="https://user-images.githubusercontent.com/76645095/165495648-1d7fa864-7e47-4995-bd11-50a320cd7adf.png"> 
>> 
>> Request URL
>> ```
>> http://localhost:8080/api/v1/webrtc/register
>> ```
>> Request Body Example
>> ```JavaScript
>> {
>>    "nickname" : "user",
>>    "password" : "user"
>> }
>>```
>> Response Body Example
>> ```JavaScript
>> {
>>    "id": "0df06625-ae43-44f1-a8b0-cf59d781acb0",
>>    "created_at": null,
>>    "updated_at": null,
>>    "email": null,
>>   "password": "$2a$10$rZc38bL1.6TLk.AYb6zp/OBq8quqsgyJdGgJkCyVVe7OFrbUIO9S.",
>>    "birthdate": null,
>>    "phone_number": null,
>>    "school": null,
>>    "company": null,
>>    "nickname": "user",
>>    "nickname_expire_at": null
>> }
>>```

> Issuing jwt access token  
>> ![image](https://user-images.githubusercontent.com/76645095/165495907-19020122-05bc-486f-9896-bd439550d640.png)
>> Request URL
>> ```
>> http://localhost:8080/api/v1/webrtc/authenticate
>> ```
>> Request Body Example
>> ```JavaScript
>> {
>>    "nickname" : "user",
>>    "password" : "user"
>> }
>> ```
>> Response Body Example
>> ```JavaScript
>> {
>>     "jwttoken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNjUxMDcyMjU2LCJpYXQiOjE2NTEwNTQyNTZ9.zNLPBDKKK2v5uDG3mQE_sNjMEGrgDzM2ds0-_Pm08PcnoI6z2RfDBqKql0bV7ra57ynLAPmrY8i8JC04OwIgKw"
>> }
>> ```
> Create a channel
>> <img width="1010" alt="image" src="https://user-images.githubusercontent.com/76645095/165496715-756df71d-a3c9-4aad-afa1-20d527cc934a.png">

>> Request URL
>> ```
>> localhost:8080/api/v1/webrtc/channel
>> ```
>> Request Body Example
>> ```JavaScript
>> {
>>    "channelName" : "E",
>>    "limitParticipants" : 15,
>>    "hashTags" : ["hello", "나도", "다니고싶다"]
>> }
>> ```
>> Insert issued token into request header
>> <img width="1011" alt="image" src="https://user-images.githubusercontent.com/76645095/165496994-e30084cb-51f7-4269-b079-1434fb46f9e4.png">  
>> Response Body Example
>> ```JavaScript
>> {
>>    "type": "SUCCESS",
>>    "channelName": "E",
>>    "limitParticipants": 15,
>>    "currentParticipants": 1,
>>    "timeToLive": 86400
>> }
>> ```
> Put jwtoken and channelId in front end
>> ```JavaScript
>>    const connect =()=>{
>>        let Sock = new SockJS('http://localhost:8080/ws-stomp');
>>        stompClient = over(Sock);
>>        stompClient.connect({jwt : "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNjUxMDcyODQ4LCJpYXQiOjE2NTEwNTQ4NDh9.-vUBaZXdJJvCLe95DeBBfZRcvX7V3Zv8QD3SUX_qx7VtwYrUS7Q9ILb28LMQPQF8FdZFAK1qfcW6AsnS5bDoiw", username : "user"}, onConnected, onError);
>>        console.log(stompClient);
>>    }
>>
>>    const onConnected = () => {
>>        setUserData({...userData,"connected": true});
>>        stompClient.subscribe('/sub/chat/room/' + "291e3ac0-d01a-4c2d-9a76-a64e3f9b0101", onMessageReceived);
>>        
>>      userJoin();
>>
>>    }
>>    const userJoin=()=>{
>>          var chatMessage = {
>>            type:"ENTER",
>>            channelId : "291e3ac0-d01a-4c2d-9a76-a64e3f9b0101",
>>            senderName : userData.username,
>>            message : "Enter" + userData.username,
>>          };
>>          stompClient.send("/pub/chat/room", {jwt : "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNjUxMDcyODQ4LCJpYXQiOjE2NTEwNTQ4NDh9.-vUBaZXdJJvCLe95DeBBfZRcvX7V3Zv8QD3SUX_qx7VtwYrUS7Q9ILb28LMQPQF8FdZFAK1qfcW6AsnS5bDoiw", username : "user"}, JSON.stringify(chatMessage));
>>    }
>>
>>    const onError = (err) => {
>>        console.log(err);
>>        
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
>>                channelId: "291e3ac0-d01a-4c2d-9a76-a64e3f9b0101",
>>                senderName: userData.username,
>>                message:userData.message
>>              };
>>              console.log(chatMessage);
>>              stompClient.send("/pub/chat/room", {jwt : "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNjUxMDY0MTY4LCJpYXQiOjE2NTEwNDYxNjh9.5OhDgCnkkeq685FjTeIhkZ-wBSP-vnIy1bLz-Pnb4v78DR5ZDiWab0WKbu6935gIYuUbrKCDO0LgJjf29C0Sjw", username : "user"}, JSON.stringify(chatMessage));
>>              setUserData({...userData,"message": ""});
>>            }
>>    }
>> }
>> ```
> put a username in input and click connect button
>> <img width="1054" alt="image" src="https://user-images.githubusercontent.com/76645095/165498564-99a35ef1-3d49-4675-a98b-1c8ba872dd76.png">

> Open the Network tab in the Developer window to see if you receive messages from the server
>> <img width="765" alt="image" src="https://user-images.githubusercontent.com/76645095/165498569-bd3d1861-9e8a-4ca1-a2b8-f0c4e480be66.png">
>>  

