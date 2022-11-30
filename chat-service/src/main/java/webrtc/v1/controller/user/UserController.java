package webrtc.v1.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import webrtc.v1.domain.Users;
import webrtc.v1.dto.JwtDto.JwtRequest;
import webrtc.v1.dto.JwtDto.JwtResponse;
import webrtc.v1.dto.UsersDto.CreateUserRequest;
import webrtc.v1.exception.UserException.NotExistUserException;
import webrtc.v1.service.jwt.JwtUserDetailsService;
import webrtc.v1.service.user.UsersService;
import webrtc.v1.utils.jwt.JwtTokenUtil;


@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/v1/webrtc/chat")
public class UserController {

    private final UsersService userService;

    private final JwtTokenUtil jwtTokenUtil;

    private final JwtUserDetailsService jwtUserDetailsService;

    private final PasswordEncoder passwordEncoder;

    @PostMapping(value = "/register")
    public ResponseEntity<?> save(@RequestBody CreateUserRequest request) throws Exception {
        Users user = userService.save(request);
        return new ResponseEntity(user, HttpStatus.OK);
    }

    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest request) throws Exception {
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(request.getEmail());
        if (passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            final String token = jwtTokenUtil.generateToken(userDetails);
            return ResponseEntity.ok(new JwtResponse(token));
        }
        throw new NotExistUserException();
    }
}