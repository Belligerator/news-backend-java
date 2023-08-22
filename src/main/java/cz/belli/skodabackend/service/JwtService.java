package cz.belli.skodabackend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import cz.belli.skodabackend.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String JWT_SECRET;

    /**
     * Creates JWT token for given subject.
     * @param subject   User identifier.
     * @return          JWT token.
     */
    public String createAccessToken(String subject) {
        Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET.getBytes());
        return JWT.create()
                .withSubject(subject)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 1000)) // 60 minutes
                .sign(algorithm);
    }

    /**
     * Creates random refresh token. This token is used to refresh access token.
     * @return  Refresh token. Random string.
     */
    public String createRefreshToken() {
        return Utils.generateRandomToken(Constants.REFRESH_TOKEN_SIZE);
    }
}
