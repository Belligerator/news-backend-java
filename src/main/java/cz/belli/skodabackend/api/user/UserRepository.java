package cz.belli.skodabackend.api.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    UserEntity findByUsername(String username);

    UserEntity findByIdAndRefreshToken(Integer id, String refreshToken);

}
