package webrtc.chatservice.controller.jwt;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.JwtDto.JwtRequest;
import webrtc.chatservice.dto.JwtDto.JwtResponse;
import webrtc.chatservice.dto.UserDto;
import webrtc.chatservice.dto.UserDto.CreateUserRequest;
import webrtc.chatservice.dto.UserDto.CreateUserResponse;
import webrtc.chatservice.service.jwt.JwtUserDetailsService;
import webrtc.chatservice.service.user.UserService;
import webrtc.chatservice.utils.CustomJsonMapper;
import webrtc.chatservice.utils.JwtTokenUtil;


@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/v1/webrtc/chat")
public class JwtAuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    private final JwtUserDetailsService jwtUserDetailsService;

    private final UserService userService;

    private final HttpApiController httpApiController;



    @ApiResponses({
            @ApiResponse(code = 200, message = "토큰을 정상적으로 발급합니다."),
            @ApiResponse(code = 404, message = "로그인 정보가 잘못되었습니다.")
    })
    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

        final UserDetails userDetails = jwtUserDetailsService
                .loadUserByUsername(authenticationRequest.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }


    @PostMapping(value = "/register")
    public ResponseEntity<?> saveUser(@RequestBody CreateUserRequest request) throws Exception {
        User user = userService.saveUser(request);
        httpApiController.postSaveUser(request);
        return new ResponseEntity(user, HttpStatus.OK);
    }
}