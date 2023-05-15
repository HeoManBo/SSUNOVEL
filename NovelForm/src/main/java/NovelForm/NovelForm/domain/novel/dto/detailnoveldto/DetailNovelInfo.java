package NovelForm.NovelForm.domain.novel.dto.detailnoveldto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@Data
//세부 상세 조회시 보여줘야할 것
public class DetailNovelInfo {

    //novel repo를 통해 가져와야할 값
    @Schema(description = "이미지 링크", defaultValue = "https://img.xxx.net/cover")
    private String image_url;

    @Schema(description = "소설 제목", defaultValue = "test title")
    private String title;

    @Schema(description = "소설 내용", defaultValue = "소설 내용입니다.")
    private String content;

    @Schema(description = "작가 명")
    private String authorName;

    @Schema(description = "소설 장르")
    private String category;

    @Schema(description = "네이버 소설 경로")
    private String is_naver;

    @Schema(description = "카카오 웹 소설 경로")
    private String is_kakao;

    @Schema(description = "문피아 웹 소설 경로")
    private String is_munpia;

    @Schema(description = "리디북스 웹 소설 경로")
    private String is_ridi;

    @Schema(description = "해당 웹 소설 사이트 리뷰 수")
    private int review_cnt;

    @Schema(description = "해당 웹 소설 사이트 평점")
    private double rating;

    //review repo를 통해 가져와야할 값
    //전체 리뷰 수, 전체 리뷰 평균,
    @Schema(description = "해당 웹 소설 리뷰 List")
    List<ReviewDto> reviewInfos = new ArrayList<>();

    //favorite repo를 통해 가져와야할 값
    //상세 조회한 회원이 이미 좋아하는 소설인 경우 1
    //비로그인 회원이거나, 좋아요를 누르지 않은 회원인 경우 0
    @Schema(description = "이미 즐겨찾기 한 소설인지 확인", defaultValue = "했으면 1, 안 했으면 0")
    int alreadyLike;
    @Schema(description = "내가 부여한 평점", defaultValue = "평점 미 부여시 0점, 그 외엔 0.5~5.0 0.5단위")
    private double my_rating;
    @Schema(description = "내가 작성한 리뷰", defaultValue = "미 작성시 null 값으로 처리")
    private String my_review;

    @Builder
    public DetailNovelInfo(String image_url, String title, String content, String category,
                           String is_naver, String is_kakao, String is_munpia, String is_ridi, int review_cnt,
                           double rating, String authorName) {
        this.image_url = image_url;
        this.title = title;
        this.content = content;
        this.category = category;
        this.is_naver = is_naver;
        this.is_kakao = is_kakao;
        this.is_munpia = is_munpia;
        this.is_ridi = is_ridi;
        this.review_cnt = review_cnt;
        this.rating = rating;
        this.authorName = authorName;
    }

    public DetailNovelInfo() {

    }
}
