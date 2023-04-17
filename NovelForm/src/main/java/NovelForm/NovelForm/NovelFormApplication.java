package NovelForm.NovelForm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@SpringBootApplication
public class NovelFormApplication {

	public static void main(String[] args) {
		SpringApplication.run(NovelFormApplication.class, args);
	}

}
