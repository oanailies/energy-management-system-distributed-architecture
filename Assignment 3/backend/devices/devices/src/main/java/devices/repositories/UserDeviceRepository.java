package devices.repositories;

import devices.entities.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    Optional<Object> findByDeviceId(Long deviceId);

    List<UserDevice> findByUserId(Long userId);

    boolean existsByDeviceId(Long deviceId);

    Optional<UserDevice> findByUserIdAndDeviceId(Long userId, Long deviceId);
}
