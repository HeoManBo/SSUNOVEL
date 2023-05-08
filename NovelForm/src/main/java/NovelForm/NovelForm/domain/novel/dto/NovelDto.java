package NovelForm.NovelForm.domain.novel.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 소설 검색시 반환받을 NovelDto 입니다. (작가 포함)
 */
@Getter
public class NovelDto {

    private String title;
    private String authorName;
    private String img_link;
    private double reivew;
    private int review_cnt;
    private String category;
    private Long novelId;

    @Builder
    public NovelDto(String title, String authorName, String img_link, double reivew, int review_cnt, String category,
                    Long novelId) {
        this.title = title;
        this.authorName = authorName;
        this.img_link = img_link;
        this.reivew = reivew;
        this.review_cnt = review_cnt;
        this.category = category;
        this.novelId = novelId;
    }

}
