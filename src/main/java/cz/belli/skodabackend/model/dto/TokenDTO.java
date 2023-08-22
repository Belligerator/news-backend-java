package cz.belli.skodabackend.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDTO {

    String accessToken;
    String refreshToken;

    public TokenDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
