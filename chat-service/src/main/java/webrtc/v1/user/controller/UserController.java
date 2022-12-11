package webrtc.v1.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import webrtc.v1.user.entity.Users;
import webrtc.v1.utils.jwt.dto.JwtDto.JwtRequest;
import webrtc.v1.utils.jwt.dto.JwtDto.JwtResponse;
import webrtc.v1.user.dto.UsersDto.CreateUserRequest;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.utils.jwt.JwtUserDetailsService;
import webrtc.v1.user.service.UsersService;
import webrtc.v1.utils.jwt.JwtTokenUtil;


@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/v1/webrtc/chat")
@Slf4j
public class UserController {

    private final UsersService userService;

    private final JwtTokenUtil jwtTokenUtil;

    private final JwtUserDetailsService jwtUserDetailsService;

    private final PasswordEncoder passwordEncoder;

    @PostMapping(value = "/register")
    public ResponseEntity<?> save(
            @RequestBody CreateUserRequest request
    ) throws Exception {
        Users user = userService.save(request);
        return new ResponseEntity(user, HttpStatus.OK);
    }

    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody JwtRequest request
    ) {
        final Users user = userService.findOneByEmail(request.getEmail());
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(user.getId().toString());
        if (passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            final String token = jwtTokenUtil.generateToken(userDetails);
            return ResponseEntity.ok(new JwtResponse(token));
        }
        throw new NotExistUserException();
    }
}