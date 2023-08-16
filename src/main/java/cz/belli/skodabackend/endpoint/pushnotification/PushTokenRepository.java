package cz.belli.skodabackend.endpoint.pushnotification;

import cz.belli.skodabackend.endpoint.pushnotification.PushTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushTokenRepository extends JpaRepository<PushTokenEntity, Long> {
}
