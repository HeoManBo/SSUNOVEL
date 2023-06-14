package NovelForm.NovelForm.global;

import NovelForm.NovelForm.global.s3.S3Service;
import NovelForm.NovelForm.repository.*;
import NovelForm.NovelForm.util.NovelCSVParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

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

    private final WebClient webClient;

    /**
     * 서버가 시작될 때 실행된다...
     * 매주 수요일 0시 0분에 호출된다.
     */
    @Scheduled(cron = "0 0 0 * * 3")
    public void callScrapingApi(){

        // 스크래핑 api 호출
        webClient.get()
                .uri("http://52.79.165.132/scraping")
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(response -> {
                    ;
                }, error->{
                    log.error("요청 처리 중 에러 {}", error.getMessage());
                });




//        webClient.get()
//                .uri("http://52.79.165.132/scraping")
//                .retrieve()
//                .bodyToMono(String.class)
//                .flatMap(response ->{
//                    // 비동기 요청 결과 처리
//                    if (response.equals("\"ok\"")) {
//                        log.info("스크래핑 성공");
//                        csvDownload();
//                        log.info("다운 종료");
//
//                        // 파싱
//                        //NovelCSVParser novelCSVParser = new NovelCSVParser();
//                        //novelCSVParser.mergeDB(authorRepository, novelRepository, alertRepository, favoriteAuthorRepository);
//                        return webClient.get()
//                                .uri("http://52.79.165.132/summary-recommend")
//                                .retrieve()
//                                .bodyToMono(String.class);
//                    } else {
//                        log.info("스크래핑 실패");
//                        return Mono.empty();
//                    }
//                })
//                .subscribe(secondResponse -> {
//                    if(secondResponse.equals("\"ok\"")){
//                        log.info("DB에 줄거리 기반 소설 추천 목록 넣기 성공");
//                    }
//                    else{
//                        log.info("DB 저장 실패");
//                    }
//                });

    }

//    private void csvDownload() {
//        String[] keyList = {"naver.csv", "kakao.csv", "munpia0.csv", "munpia1.csv", "ridi0.csv", "ridi1.csv", "ridi2.csv"};
//        String localFilePath = "./NovelForm/";
//
//        for (String key : keyList) {
//            try{
//                s3Service.download(localFilePath + key, key);
//            }
//            catch (Exception e){
//                log.error("S3에서 {} 다운 실패!", key);
//            }
//        }
//    }


}
