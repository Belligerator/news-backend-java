package cz.belli.skodabackend.api.user;

import cz.belli.skodabackend.model.dto.SignInDTO;
import cz.belli.skodabackend.model.dto.TokenDTO;
import cz.belli.skodabackend.model.exception.UnauthorizedException;
import cz.belli.skodabackend.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username);

        if (userEntity == null) {
            throw new UnauthorizedException("User not found.");
        } else {
            return new User(userEntity.getUsername(), userEntity.getPassword(), Collections.emptyList());
        }
    }

    /**
     * Returns user by username.
     * @param username  Username of the user to return.
     * @return          User with given username.
     */
    public UserEntity getUserByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username);

        if (userEntity == null) {
            log.error("User not found: {}", username);
            throw new UnauthorizedException("User not found.");
        }

        return userEntity;
    }

    public UserEntity saveUser(UserEntity userEntity) {
        // check if mandatory fields are filled
        if (userEntity.getUsername() == null || userEntity.getUsername().isEmpty()) {
            log.error("Username is mandatory.");
            // todo handle this exception
            throw new IllegalArgumentException("Username is mandatory.");
        }
        return userRepository.save(userEntity);
    }

    /**
     * Signs in user with given credentials.
     * @param body  Credentials of the user to sign in.
     * @return      TokenDTO with access and refresh token.
     */
    public TokenDTO signIn(SignInDTO body) {
        UserEntity userEntity = this.getUserByUsername(body.getUsername());

        String refreshToken = this.jwtService.createRefreshToken();
        String accessToken = this.jwtService.createAccessToken(String.valueOf(userEntity.getId()));

        userEntity.setRefreshToken(refreshToken);
        userEntity.setExpirationDate(new Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000)); // 30 days

        return new TokenDTO(accessToken, refreshToken);
    }

}
