package NovelForm.NovelForm.global;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    // 비동기 요청을 보내기 위해 WebClient 객체를 생성
    // 응답의 timeout 시간은 크롤링 시간에 맞춰 7일로 설정했다.
    @Bean
    public WebClient webClient(){
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofDays(7))
                ))
                .build();
    }
}
