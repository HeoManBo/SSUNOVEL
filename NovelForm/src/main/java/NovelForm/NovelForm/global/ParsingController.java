package NovelForm.NovelForm.global;

import NovelForm.NovelForm.global.s3.S3Service;
import NovelForm.NovelForm.repository.AlertRepository;
import NovelForm.NovelForm.repository.AuthorRepository;
import NovelForm.NovelForm.repository.FavoriteAuthorRepository;
import NovelForm.NovelForm.repository.NovelRepository;
import NovelForm.NovelForm.util.NovelCSVParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ParsingController {

    private final S3Service s3Service;
    private final RestTemplate restTemplate;

    private final NovelRepository novelRepository;

    private final FavoriteAuthorRepository favoriteAuthorRepository;
    private final AuthorRepository authorRepository;

    private final AlertRepository alertRepository;

    private final WebClient webClient;

    @GetMapping("parsing")
    public BaseResponse<String> callParsing(){

        // CSV 파일 다운로드
        log.info("CSV 파일 다운로드");
        csvDownload();

        // 파싱
        log.info("파싱");
        NovelCSVParser novelCSVParser = new NovelCSVParser();
        novelCSVParser.mergeDB(authorRepository, novelRepository, alertRepository, favoriteAuthorRepository);

        // recommend 요청
        log.info("recommend 요청");
        webClient.get()
                .uri("http://52.79.165.132/summary-recommend")
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(response -> {
                    ;
                }, error ->{
                    log.error("요청 처리 중 에러 {}", error.getMessage());
                });


        return new BaseResponse<>("ok");
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
