package sol.funny.restbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories({"sol.funny.datacore.repository"})
@EntityScan("sol.funny.datacore.*")
public class RestBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestBackendApplication.class, args);
	}

}
