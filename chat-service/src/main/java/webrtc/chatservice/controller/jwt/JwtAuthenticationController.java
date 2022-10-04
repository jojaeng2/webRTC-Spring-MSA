package webrtc.chatservice.controller.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.JwtDto.JwtRequest;
import webrtc.chatservice.dto.JwtDto.JwtResponse;
import webrtc.chatservice.dto.UsersDto.CreateUserRequest;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.service.jwt.JwtUserDetailsService;
import webrtc.chatservice.service.users.UsersService;
import webrtc.chatservice.utils.jwt.JwtTokenUtil;


@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/v1/webrtc/chat")
public class JwtAuthenticationController {

    private final JwtTokenUtil jwtTokenUtil;

    private final JwtUserDetailsService jwtUserDetailsService;

    private final UsersService usersService;

    private final HttpApiController httpApiController;

    private final PasswordEncoder passwordEncoder;

    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest request) throws Exception {
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(request.getEmail());
        if(passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            final String token = jwtTokenUtil.generateToken(userDetails);
            return ResponseEntity.ok(new JwtResponse(token));
        }
        else {
            throw new NotExistUserException();
        }
    }


    @PostMapping(value = "/register")
    public ResponseEntity<?> saveUser(@RequestBody CreateUserRequest request) throws Exception {
        Users user = usersService.saveUser(request);
        httpApiController.postSaveUser(request);
        return new ResponseEntity(user, HttpStatus.OK);
    }
}