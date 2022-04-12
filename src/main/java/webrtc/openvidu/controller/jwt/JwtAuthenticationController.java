package webrtc.openvidu.controller.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import webrtc.openvidu.dto.JwtDto.JwtRequest;
import webrtc.openvidu.dto.JwtDto.JwtResponse;
import webrtc.openvidu.dto.UserDto;
import webrtc.openvidu.dto.UserDto.CreateUserRequest;
import webrtc.openvidu.service.jwt.JwtUserDetailsService;
import webrtc.openvidu.service.user.UserService;
import webrtc.openvidu.utils.JwtTokenUtil;

import java.util.Objects;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/v1/webrtc")
public class JwtAuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    private final JwtUserDetailsService userDetailsService;

    private final UserService userService;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getNickname(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getNickname());
        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> saveUser(@RequestBody CreateUserRequest request) throws Exception {
        return new ResponseEntity(userService.saveUser(request), HttpStatus.OK);
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}