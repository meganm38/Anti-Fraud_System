package antifraud;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

public interface UserRepository extends CrudRepository<User, Integer> {
    @Transactional
    @Query("UPDATE User u SET u.isNonLocked = ?1 WHERE u.username = ?2")
    @Modifying
    public void updateLock(boolean isNonLocked, String username);

    @Transactional
    @Query("UPDATE User u SET u.role = ?1 WHERE u.username = ?2")
    @Modifying
    public void updateRole(String role, String username);
}
