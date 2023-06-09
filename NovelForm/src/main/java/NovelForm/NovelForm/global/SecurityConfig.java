package NovelForm.NovelForm.global;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // Spring Security 설정 활성화
public class SecurityConfig {

    // filterChain을 설정해두면, 컨트롤러로 정상적으로 요청이 들어온다.
    // 다만, disable() 처리를 한 부분이 문제가 될 수 있다.
    // 아마 쿠키를 다루는 부분에서 문제가 될 수 있으니 나중에 문제가 난다면 여기를 확인하자.

    // cors 허용 -> 브라우저에서 다른 도메인에서 내 도메인으로 요청을 전달 할 수 있다.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors();    // cors 허용
        http.csrf().disable();
        return http.build();
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
