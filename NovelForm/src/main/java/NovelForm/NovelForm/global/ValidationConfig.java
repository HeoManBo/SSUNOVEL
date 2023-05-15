package NovelForm.NovelForm.global;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * pathVariable, requestParam 검증을 위한 Bean추가
 */
@Configuration
public class ValidationConfig {
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor(){
        MethodValidationPostProcessor validationChk = new MethodValidationPostProcessor();
        return validationChk;
    }
}
