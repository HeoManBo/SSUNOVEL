package NovelForm.NovelForm.global;

import NovelForm.NovelForm.global.s3.S3Service;
import NovelForm.NovelForm.repository.*;
import NovelForm.NovelForm.util.NovelCSVParser;
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

    private final NovelRepository novelRepository;

    private final FavoriteAuthorRepository favoriteAuthorRepository;
    private final AuthorRepository authorRepository;

    private final AlertRepository alertRepository;

    /**
     * 서버가 시작될 때 실행된다...
     * 우선은 메소드가 종료되고 3일 후에 다시 호출하도록 설정
     */
    //@Scheduled()
    public void callScrapingApi(){

        // 스크래핑 + CSV 파일 얻기
        String result = restTemplate.getForObject("http://52.79.165.132/scraping", String.class);


        if(result.equals("\"ok\"")){
            log.info("스크래핑 성공");
            csvDownload();
        }
        else{
            log.info("스크래핑 실패");
        }

        log.info("다운 종료");


        NovelCSVParser novelCSVParser = new NovelCSVParser();
        novelCSVParser.mergeDB(authorRepository, novelRepository, alertRepository, favoriteAuthorRepository);


        // DB에 줄거리가 유사한 소설 넣기
        result = restTemplate.getForObject("http://52.79.165.132/summary-recommend", String.class);

        if(result.equals("\"ok\"")){
            log.info("DB에 줄거리 기반 소설 추천 목록 넣기 성공");
        }
        else{
            log.info("DB 저장 실패");
        }
    }

    private void csvDownload() {
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


}
