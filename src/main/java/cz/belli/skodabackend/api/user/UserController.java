package cz.belli.skodabackend.api.user;

import cz.belli.skodabackend.model.dto.SignInDTO;
import cz.belli.skodabackend.model.dto.TokenDTO;
import cz.belli.skodabackend.model.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/auth")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("sign-in")
    public TokenDTO signIn(@RequestBody SignInDTO body) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword());
            authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Invalid credentials.");
        }
        return this.userService.signIn(body);
    }


}
