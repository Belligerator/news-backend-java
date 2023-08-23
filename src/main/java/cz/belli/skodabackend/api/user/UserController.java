package cz.belli.skodabackend.api.user;

import cz.belli.skodabackend.model.dto.AuthBodyDTO;
import cz.belli.skodabackend.model.dto.TokenDTO;
import cz.belli.skodabackend.model.exception.ExtendedResponseStatusException;
import cz.belli.skodabackend.model.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("api/auth")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    /**
     * Signs up user with given username and password.
     *
     * @throws ExtendedResponseStatusException    409 CONFLICT - if user already exists.
     * @throws ExtendedResponseStatusException    400 BAD_REQUEST - if username or password is empty.
     * @param body                                Body with username and password.
     * @return                                    TokenDTO with access and refresh token.
     */
    @PostMapping("sign-up")
    public TokenDTO signUp(@RequestBody @Valid AuthBodyDTO body) {
        return this.userService.signUp(body);
    }

    /**
     * Signs in user with given username and password.
     *
     * @param body  Body with username and password.
     * @return      TokenDTO with access and refresh token.
     */
    @PostMapping("sign-in")
    public TokenDTO signIn(@RequestBody @Valid AuthBodyDTO body) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword());
            authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Invalid credentials.");
        }
        return this.userService.signIn(body);
    }

    /**
     * Signs out user with his id. Take id from token.
     */
    @GetMapping("sign-out")
    public void signOut() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            this.userService.signOut(authentication.getName());
        } else {
            throw new UnauthorizedException("User not authenticated.");
        }
    }

    /**
     * Refreshes access token and refresh token. Saves new refresh token to database.
     * If refresh token is invalid, throws UnauthorizedException, user must sign in again.
     *
     * @throws UnauthorizedException    If refresh token is invalid.
     * @param data                      Map with refresh token and user id.
     * @return                          TokenDTO with new access and refresh token.
     */
    @PostMapping("refresh-token")
    public TokenDTO refreshToken(@RequestBody Map<String, Object> data) {
        String refreshToken = (String) data.get("refreshToken");
        Integer userId = (Integer) data.get("userId");

        return this.userService.refreshToken(userId, refreshToken);
    }
}
