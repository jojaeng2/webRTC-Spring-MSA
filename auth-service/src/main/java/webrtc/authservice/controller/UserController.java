package webrtc.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.authservice.dto.UserDto.CreateUserRequest;
import webrtc.authservice.dto.UserDto.FindUserByEmailRequest;
import webrtc.authservice.service.UserService;

import static org.springframework.http.HttpStatus.OK;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/v1/webrtc")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody CreateUserRequest request) throws Exception{
        return new ResponseEntity<>(userService.saveUser(request), OK);
    }

    @PostMapping("/user")
    public ResponseEntity<?> findUserByEmail(@RequestBody FindUserByEmailRequest request) {
        return new ResponseEntity<>(userService.findOneUserByEmail(request.getEmail()), OK);
    }

}
