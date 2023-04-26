package NovelForm.NovelForm.domain.member.interceptor;

import NovelForm.NovelForm.domain.member.exception.LoginInterceptorException;
import NovelForm.NovelForm.global.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 사용자가 로그인을 했는지 안했는지를 체크하는 인터셉터
 *  인증을 하지 않았다면, LoginInterceprotException을 낸다.
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();

        log.info("인증 체크 인터셉터...{}", requestURI);

        HttpSession session = request.getSession(false);    // 생성 X, 가져오기

        if(session == null || session.getAttribute(SessionConst.LOGIN_MEMBER_ID) == null){
            log.info("미인증 사용자");

            throw new LoginInterceptorException("미인증 사용자");
        }

        return true;
    }

}
