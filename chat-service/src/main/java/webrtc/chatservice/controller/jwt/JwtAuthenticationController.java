package webrtc.chatservice.controller.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.JwtDto.JwtRequest;
import webrtc.chatservice.dto.JwtDto.JwtResponse;
import webrtc.chatservice.dto.UsersDto.CreateUserRequest;
import webrtc.chatservice.service.jwt.JwtUserDetailsService;
import webrtc.chatservice.service.users.UsersService;
import webrtc.chatservice.utils.JwtTokenUtil;


@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/v1/webrtc/chat")
public class JwtAuthenticationController {

    private final JwtTokenUtil jwtTokenUtil;

    private final JwtUserDetailsService jwtUserDetailsService;

    private final UsersService usersService;

    private final HttpApiController httpApiController;

    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

        final UserDetails userDetails = jwtUserDetailsService
                .loadUserByUsername(authenticationRequest.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }


    @PostMapping(value = "/register")
    public ResponseEntity<?> saveUser(@RequestBody CreateUserRequest request) throws Exception {
        Users users = usersService.saveUser(request);
        httpApiController.postSaveUser(request);
        return new ResponseEntity(users, HttpStatus.OK);
    }
}