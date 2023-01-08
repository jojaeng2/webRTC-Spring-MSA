package webrtc.v1.utils.jwt;

import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.user.entity.Users;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.user.repository.UsersRepository;

@RequiredArgsConstructor
@Service
public class JwtUserDetailsService implements UserDetailsService {

  private final UsersRepository usersRepository;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
    Users user = usersRepository.findById(id)
        .orElseThrow(NotExistUserException::new);
    return new org.springframework.security.core.userdetails.User(user.getId(), user.getPassword(),
        new ArrayList<>());
  }
}
