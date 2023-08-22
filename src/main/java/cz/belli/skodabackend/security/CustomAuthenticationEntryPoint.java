package cz.belli.skodabackend.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Custom authentication entry point. Throws AuthenticationException if authentication fails.
 * {@link cz.belli.skodabackend.exceptionhandler.GlobalControllerExceptionHandler} cannot handle exception from filter,
 * so this class is used to handle exception from filter and resolve it to {@link cz.belli.skodabackend.exceptionhandler.GlobalControllerExceptionHandler}.
 * So custom error response can be returned in response.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver resolver;

    public CustomAuthenticationEntryPoint(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        resolver.resolveException(request, response, null, authException);
    }

}
