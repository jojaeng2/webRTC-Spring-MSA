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


    
    @ApiOperation(value = "jwt token 발급", notes =
                    "jwt Access token을 발급 받습니다. \n" +
                    "1. 아직 토큰을 발급받지 않은 상태이니, Header에는 별도의 처리를 안하셔도 됩니다.\n" +
                    "2. email과 password로 가입된 사용자인지 확인합니다.\n"
    )
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "email"
            , value = "로그인에 사용할 email 입니다."
            , required = true
            , dataType = "string"
            , paramType = "query"
        ),
        @ApiImplicitParam(
                name = "password"
                , value = "로그인에 사용할 password입니다."
                , required = true
                , dataType = "string"
                , paramType = "query"
        )
    })
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



    @ApiOperation(value = "회원가입", notes =
            "회원가입 기능을 제공하는 API이지만, 회원가입은 채팅 서비스에서 진행하지 않고, 메인 서비스에서 진행하게 될 예정입니다. \n" +
            "메인 서비스와 채팅 서비스가 분리되어 있기 때문에, 임시 회원을 만들고 테스트 하는 용도로 사용합니다. \n"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "nickname"
                    , value = "커뮤니티에서 사용할 nickname입니다. "
                    , required = true
                    , dataType = "string"
                    , paramType = "query"
            ),
            @ApiImplicitParam(
                    name = "email"
                    , value = "회원 email입니다."
                    , required = true
                    , dataType = "string"
                    , paramType = "query"
            ),
            @ApiImplicitParam(
                    name = "password"
                    , value = "password입니다."
                    , required = true
                    , dataType = "string"
                    , paramType = "query"
            )
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "회원을 정상적으로 생성합니다.")
    })
    @PostMapping(value = "/register")
    public ResponseEntity<?> saveUser(@RequestBody CreateUserRequest request) throws Exception {
        User user = userService.saveUser(request);
        httpApiController.postSaveUser(request);
        return new ResponseEntity(user, HttpStatus.OK);
    }
}