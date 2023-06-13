package NovelForm.NovelForm.global;

import NovelForm.NovelForm.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTask {

    private final S3Service s3Service;
    private final RestTemplate restTemplate;

    /**
     * 서버가 시작될 때 실행된다...
     * 우선은 메소드가 종료되고 1일 후에 다시 호출하도록 설정
     */
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    public void callScrapingApi(){

        String result = restTemplate.getForObject("http://52.79.165.132/scraping", String.class);


        if(result.equals("\"ok\"")){
            log.info("스크래핑 성공");

            String[] keyList = {"naver.csv", "kakao.csv", "munpia0.csv", "munpia1.csv", "ridi0.csv", "ridi1.csv", "ridi2.csv"};
            String localFilePath = "./NovelForm/";

            for (String key : keyList) {
                try{
                    s3Service.download(localFilePath + key, key);
                }
                catch (Exception e){
                    log.error("S3에서 {} 다운 실패!", key);
                }

            }
        }
        else{
            log.info("스크래핑 실패");
        }

        log.info("다운 종료");
    }


}
