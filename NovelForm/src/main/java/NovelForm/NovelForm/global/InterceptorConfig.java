package NovelForm.NovelForm.global;

import NovelForm.NovelForm.domain.member.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {


    /**
     *  로그인 인터셉터 설정
     *  /member 하위의 url에 대해서 설정
     *  /member/login, /member/create 은 인터셉터 예외
     *  5/14 , 리뷰 작성시 Interceptor 추가
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .order(1)
                .addPathPatterns("/member/**", "/box", "/favorite/**", "/box/**", "/like/**", "/alert/**")
                .excludePathPatterns("/member/login", "/member/create", "/member/email",
                                    "/box/all", "/box/search/**", "/box/info/*",
                                    "/novel/review");
    }
}
