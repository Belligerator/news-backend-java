package cz.belli.skodabackend.api.user;

import cz.belli.skodabackend.model.dto.AuthBodyDTO;
import cz.belli.skodabackend.model.dto.TokenDTO;
import cz.belli.skodabackend.model.exception.ExtendedResponseStatusException;
import cz.belli.skodabackend.model.exception.UnauthorizedException;
import cz.belli.skodabackend.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Signs up user with given credentials. After sign up, user is signed in and access and refresh token is returned.
     *
     * @throws ExtendedResponseStatusException  409 CONFLICT - if user already exists.
     * @param body                              Credentials of the user to sign up.
     * @return                                  TokenDTO with access and refresh token.
     */
    public TokenDTO signUp(AuthBodyDTO body) {

        UserEntity oldUserEntity = this.userRepository.findByUsername(body.getUsername());

        if (oldUserEntity != null) {
            throw new ExtendedResponseStatusException(HttpStatus.CONFLICT, "User already exists.");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(body.getUsername());
        userEntity.setPassword(this.passwordEncoder.encode(body.getPassword()));
        userEntity.setActive(true);

        userRepository.save(userEntity);

        return this.signIn(body);
    }

    /**
     * Signs in user with given credentials.
     *
     * @param body  Credentials of the user to sign in.
     * @return      TokenDTO with access and refresh token.
     */
    public TokenDTO signIn(AuthBodyDTO body) {
        UserEntity userEntity = this.getUserByUsername(body.getUsername());

        String refreshToken = this.jwtService.createRefreshToken();
        String accessToken = this.jwtService.createAccessToken(String.valueOf(userEntity.getId()));

        userEntity.setRefreshToken(refreshToken);
        userEntity.setExpirationDate(new Date(System.currentTimeMillis() + JwtService.REFRESH_TOKEN_EXPIRATION_TIME));
        this.userRepository.save(userEntity);

        return new TokenDTO(accessToken, refreshToken);
    }

    /**
     * Signs out user with given id.
     *
     * @param userid    Id of the user to sign out.
     */
    public void signOut(String userid) {
        try {
            UserEntity userEntity = this.userRepository.getReferenceById(Integer.parseInt(userid));
            userEntity.setRefreshToken(null);
            userEntity.setExpirationDate(null);
            this.userRepository.save(userEntity);
        } catch (EntityNotFoundException e) {
            // User not found, so we don't have sign him out.
        }
    }

    /**
     * Returns user by username.
     *
     * @throws ExtendedResponseStatusException  404 NOT_FOUND - if user not found.
     * @param username  Username of the user to return.
     * @return          User with given username.
     */
    public UserEntity getUserByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username);

        if (userEntity == null) {
            log.error("User not found: {}", username);
            throw new ExtendedResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        return userEntity;
    }

    /**
     * Refreshes access token and refresh token. Saves new refresh token to database.
     * If refresh token is invalid, throws UnauthorizedException, user must sign in again.
     *
     * @throws UnauthorizedException    If refresh token is invalid.
     * @param userId        Id of the user to refresh token.
     * @param refreshToken  Refresh token used to refresh access token.
     * @return              TokenDTO with new access and refresh token.
     */
    public TokenDTO refreshToken(Integer userId, String refreshToken) {
        UserEntity userEntity = this.userRepository.findByIdAndRefreshToken(userId, refreshToken);

        if (userEntity == null || userEntity.getExpirationDate() == null || userEntity.getExpirationDate().before(new Date())) {
            throw new UnauthorizedException("Invalid refresh token, please sign in.");
        }

        String newAccessToken = this.jwtService.createAccessToken(String.valueOf(userEntity.getId()));
        String newRefreshToken = this.jwtService.createRefreshToken();

        userEntity.setRefreshToken(newRefreshToken);
        userEntity.setExpirationDate(new Date(System.currentTimeMillis() + JwtService.REFRESH_TOKEN_EXPIRATION_TIME));
        this.userRepository.save(userEntity);

        return new TokenDTO(newAccessToken, newRefreshToken);
    }

    /**
     * Loads user by username. Used by Spring Security.
     *
     * @param username  the username identifying the user whose data is required.
     * @return          a fully populated user record (never <code>null</code>)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username);

        if (userEntity == null) {
            throw new UnauthorizedException("User not found.");
        } else {
            return new User(userEntity.getUsername(), userEntity.getPassword(), Collections.emptyList());
        }
    }

}
