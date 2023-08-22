package cz.belli.skodabackend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Basic authentication provider. Used for basic authentication.
 * Compare credentials from Authorization header with values from environment variables.
 */
@Component
public class BasicAuthenticationProvider implements AuthenticationProvider {

    @Value("${spring.security.user.name}")
    private String BASIC_AUTH_USERNAME;

    @Value("${spring.security.user.password}")
    private String BASIC_AUTH_PASSWORD;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String username = auth.getName();
        String password = auth.getCredentials().toString();

        if (username != null && username.equals(BASIC_AUTH_USERNAME) &&
                password != null && password.equals(BASIC_AUTH_PASSWORD)) {
            return new UsernamePasswordAuthenticationToken(username, password, Collections.emptyList());
        } else {
            throw new BadCredentialsException("Invalid basic authentication credentials.");
        }
    }

    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}
