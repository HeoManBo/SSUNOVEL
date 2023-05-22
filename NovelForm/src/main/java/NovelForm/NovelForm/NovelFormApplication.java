package NovelForm.NovelForm;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.bind.annotation.GetMapping;


@EnableJpaAuditing
@SpringBootApplication
public class NovelFormApplication {

	public static void main(String[] args) {
		SpringApplication.run(NovelFormApplication.class, args);
	}


	@GetMapping("/")
	public String index(){
		return "index.html";
	}

}
