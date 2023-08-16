package cz.belli.skodabackend.endpoint.auth;

import cz.belli.skodabackend.endpoint.auth.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
