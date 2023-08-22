package cz.belli.skodabackend.security;

import cz.belli.skodabackend.security.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint authEntryPoint;
    private final JwtAuthorizationFilter customAuthorizationFilter;
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final BasicAuthenticationProvider basicAuthenticationProvider;

    /**
     * This bean is used for requests validation with JWT.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain securityJwtFilterChain(HttpSecurity http) throws Exception {
        // Only some endpoints are secured with JWT. It is implemented as example of how to use JWT.
        // However, in real application, JWT should be used for all endpoints.
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(customAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .requestMatchers((requestMatchers) ->
                        requestMatchers
                                .antMatchers("/api/articles/**")
                )
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .antMatchers("/api/articles/**").authenticated()
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(authEntryPoint)
                )
                .sessionManagement(sessionManagement ->
                        // Do not remember user session. Check token in every request.
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    /**
     * This bean is used for requests validation with basic authentication.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain securityBasicFilterChain(HttpSecurity http) throws Exception {
        // Basic authentication is shown as example of how to use it.
        // In this sample application, it is used only for push notifications creation and deletion from mobile app.
        http
                .csrf(AbstractHttpConfigurer::disable)
                .requestMatchers((requestMatchers) ->
                        requestMatchers
                                .antMatchers("/api/push-notifications/**")
                )
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .antMatchers("/api/push-notifications/**").authenticated()
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(authEntryPoint)
                )
                // Custom authentication provider is used for basic authentication.
                // It is used for checking credentials from Authorization header.
                .authenticationProvider(basicAuthenticationProvider)
                .httpBasic().authenticationEntryPoint(basicAuthenticationEntryPoint());
        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint basicAuthenticationEntryPoint() {
        BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName("basic auth realm");
        return entryPoint;
    }

    /**
     * This bean is used for authentication of requests with username and password.
     * Used in sign-in endpoint. {@link cz.belli.skodabackend.api.user.UserController}
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(List<AuthenticationProvider> myAuthenticationProviders) {
        // We have to create our own authentication manager, because we have more authentication providers.
        // authenticationProvider() above and BasicAuthenticationProvider.
        return new ProviderManager(myAuthenticationProviders);
    }

}

