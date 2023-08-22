package cz.belli.skodabackend.api.pushnotification;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PushNotificationRepository extends JpaRepository<PushTokenEntity, String> {

    /**
     * Delete tag by id. Use query to avoid exception when deleting non existing entity.
     * @param id    Id of entity to delete.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM PushTokenEntity entity WHERE entity.id = :id")
    void deleteById(@NotNull String id);

}
