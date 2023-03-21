package sol.funny.datacore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sol.funny.datacore.entity.domain.Client;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    public Client getClientByUserNameAndStatus(String userName, String status);

    public boolean existsByUserNameAndStatus(String userName, String status);

    @Query(value = "select ROLE_VALE FROM Client c INNER JOIN CLIENT_ROLE cr ON c.USER_NAME = cr.USER_NAME AND cr.STATUS = 'A' " +
            " INNER JOIN ROLES r ON r.ROLE_ID = cr.USER_ROLE AND r.STATUS = 'A' WHERE c.USER_NAME = :username AND c.STATUS = :status ", nativeQuery = true)
    public List<String> getClientRolesByUserNameAndStatus(@Param("username") String username,@Param("status") String status);
}
