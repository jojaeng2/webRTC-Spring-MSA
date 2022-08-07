package webrtc.chatservice.service.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.service.users.UsersService;

import java.util.ArrayList;

@RequiredArgsConstructor
@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UsersService usersService;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        Users users = usersService.findOneUserByEmail(userEmail);

        return new org.springframework.security.core.userdetails.User(users.getEmail(), users.getPassword(), new ArrayList<>());

    }
}
