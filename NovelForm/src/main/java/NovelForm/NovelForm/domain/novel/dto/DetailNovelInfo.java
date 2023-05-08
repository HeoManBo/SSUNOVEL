package NovelForm.NovelForm.domain.novel.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
//세부 상세 조회시 보여줘야할 것
public class DetailNovelInfo {

    private String title;
    private String content;
    private String reivew_cnt;
    private double rating;

    List<ReviewInfo> reviewInfos = new ArrayList<>();

    private int is_naver;
    private int is_kakao;
    private int is_munpia;
    private int is_ridi;

    public DetailNovelInfo(String title, String content, String reivew_cnt, double rating,
                           int is_naver, int is_kakao, int is_munpia, int is_ridi) {
        this.title = title;
        this.content = content;
        this.reivew_cnt = reivew_cnt;
        this.rating = rating;
        this.is_naver = is_naver;
        this.is_kakao = is_kakao;
        this.is_munpia = is_munpia;
        this.is_ridi = is_ridi;
    }
}


//각 리뷰당 있어야할 정보
@AllArgsConstructor
class ReviewInfo{
    String nickname; // 작성한 멤버 nickname;
    String content; //리뷰내용
    double rating; //별점
    LocalDateTime writeAt; //작성 시간
}