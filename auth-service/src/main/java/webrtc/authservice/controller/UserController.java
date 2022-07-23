package webrtc.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.authservice.dto.UserDto.CreateUserRequest;
import webrtc.authservice.dto.UserDto.DecreasePointRequest;
import webrtc.authservice.dto.UserDto.FindUserByEmailRequest;
import webrtc.authservice.dto.UserDto.FindUserWithPointByEmailRequest;
import webrtc.authservice.service.user.UserService;

import static org.springframework.http.HttpStatus.OK;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/v1/webrtc/auth")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody CreateUserRequest request) throws Exception{
        return new ResponseEntity<>(userService.save(request), OK);
    }

    @PostMapping("/user")
    public ResponseEntity<?> findUserByEmail(@RequestBody FindUserByEmailRequest request) {
        return new ResponseEntity<>(userService.findOneUserByEmail(request.getEmail()), OK);
    }

    @PostMapping("/user/point")
    public ResponseEntity<?> findUserWithPointByEmail(@RequestBody FindUserWithPointByEmailRequest request) {

        return new ResponseEntity<>(userService.findOneUserWithPointByEmail(request.getEmail()), OK);
    }
    
    @PostMapping("/decrease/point")
    public ResponseEntity<?> decreaseUserPoint(@RequestBody DecreasePointRequest request) {
        userService.decreasePoint(request.getUserEmail(), request.getPoint());
        return new ResponseEntity<>(OK);
    }

}
