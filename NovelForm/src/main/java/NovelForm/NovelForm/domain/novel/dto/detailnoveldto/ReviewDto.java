package NovelForm.NovelForm.domain.novel.dto.detailnoveldto;

import NovelForm.NovelForm.domain.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class ReviewDto implements Comparable<ReviewDto> {

    @Schema(description = "작성한 사람의 nickname", defaultValue = "XXXX")
    String nickname; // 작성한 멤버 nickname;

    @Schema(description = "작성한 리뷰 내용", defaultValue = "리뷰 내용이 없다면 null값")
    String content; //리뷰내용

    @Schema(description = "부여한 평점", defaultValue = "0.0~5.0 0.5단위")
    double rating; //별점

    @Schema(description = "작성 시간")
    LocalDateTime writeAt; //작성 시간

    @Schema(description = "해당 리뷰에 좋아요를 누른 수")
    int like_cnt; // 좋아요 수

    @Schema(description = "해당 리뷰 작성자 id")
    Long member_id;

    @Schema(description = "해당 리뷰 번호")
    Long review_id;

    @Schema(description = "로그인 한 경우 해당 리뷰를 좋아요를 눌렀는지 -> 눌럿다면 1으로, 비로그인 상태나 누르지 않았다면 0으로 설정")
    int already_like;

    public ReviewDto(String nickname, String content, double rating, LocalDateTime writeAt, Long like_cnt, Long member_id, Long review_id) {
        this.nickname = nickname;
        this.content = content;
        this.rating = rating;
        this.writeAt = writeAt;
        this.like_cnt = (int)like_cnt.intValue();
        this.member_id = member_id;
        this.review_id = review_id;
    }

    public ReviewDto(String nickname, String content, double rating, LocalDateTime writeAt, Long member_id, Long review_id){
        this.nickname = nickname;
        this.content = content;
        this.rating = rating;
        this.writeAt = writeAt;
        this.member_id = member_id;
        this.review_id = review_id;
    }

    public ReviewDto(String nickname, String content, double rating,
                     LocalDateTime createAt, int size, Long review_id, Long writer_id) {
        this.nickname = nickname;
        this.content = content;
        this.rating = rating;
        this.writeAt = createAt;
        this.like_cnt = size;
        this.member_id = writer_id;
        this.review_id = review_id;
        this.already_like = 0; //일단은 좋아요를 누르지 않은 것으로 초기값 설정
    }

    public void setAlready_like(int already_like) {
        this.already_like = already_like;
    }

    // 내림차순으로 정렬
    @Override
    public int compareTo(ReviewDto o) {
        return o.getLike_cnt() - this.like_cnt;
    }
}
