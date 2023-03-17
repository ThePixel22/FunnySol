package sol.funny.datacore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sol.funny.datacore.entity.domain.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
    public Client getClientByUserNameAndStatus(String userName, String status);

    public boolean existsByUserNameAndStatus(String userName, String status);
}
