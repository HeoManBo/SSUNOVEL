package NovelForm.NovelForm.domain.novel;


import NovelForm.NovelForm.global.BaseEntityTime;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "novel")
//@ToString(exclude = {"author","platform", "reviews", "week" })
public class Novel extends BaseEntityTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "novel_idx")
    private Long id;

    @Column
    private String title;

    @Lob
    private String cover_image;

    @Column
    @Lob
    private String summary;

    @Column
    private int episode;

    @Column
    private int price;

    @Column
    private int download_cnt;

    @Column
    private double rating;

    @Column
    private int review_cnt;

    // 플랫폼 추가
    @Column
    private String is_naver;
    @Column
    private String is_kakao;
    @Column
    private String is_ridi;
    @Column
    private String is_munpia;

    //작품 관점에서 여러 작품은 하나의 작가에 속하므로 N:1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_idx")
    private Author author;

    @Column
    private String is_finished;

    @Column
    private String category;

    @OneToMany(mappedBy = "novel",fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @Builder
    public Novel(String title, String summary, int episode, int price, int download_cnt, String is_finished,
                 String cover_image, double rating, int review_cnt, String category, Author author,
                 String is_naver, String is_kakao, String is_ridi, String is_munpia) {
        this.title = title;
        this.summary = summary;
        this.episode = episode;
        this.price = price;
        this.download_cnt = download_cnt;
        this.category = category;
        this.rating = rating;
        this.cover_image = cover_image;
        this.review_cnt = review_cnt;
        this.is_finished = is_finished;
        this.author = author;
        this.is_naver = is_naver;
        this.is_kakao = is_kakao;
        this.is_ridi = is_ridi;
        this.is_munpia = is_munpia;
    }

    public void addAuthor(Author author){
        this.author = author;
        author.addNovel(this);
    }


    public void addReview(Review review){
        this.reviews.add(review);
        calculateRating(review);
    }

    //리뷰 작성시 리뷰 1 증가
    private void calculateRating(Review review){
        this.review_cnt += 1;
        this.rating += review.getRating();
    }

    //평점 부여시 평점 평가
    public double averageRating(){
        if(review_cnt == 0) return 0.0;
        return rating / review_cnt;
    }

    //리뷰 삭제시 수행됨
    public void deleteReview(Review deleteReview) {
        this.rating -= deleteReview.getRating(); //리뷰 점수 제거
        this.review_cnt -= 1;
        this.reviews.remove(deleteReview);
    }

    public void modifyRating(double before_rating, double after_rating){
        this.rating -= before_rating;
        this.rating += after_rating;
    }

    /**
     * url 추가 메소드
     */
    public void updateNaverUrl(String is_naver) {
        this.is_naver = is_naver;
    }

    public void updateKakaoUrl(String is_kakao) {
        this.is_kakao = is_kakao;
    }

    public void updateRidiUrl(String is_ridi) {
        this.is_ridi = is_ridi;
    }

    public void updateMunpiaUrl(String is_munpia) {
        this.is_munpia = is_munpia;
    }

    //download_cnt 증가 메소드
    public void plusDownload_cnt(int cnt){
        this.download_cnt += cnt;
    }

    //더 낮은 가격으로 갱신
    public void lowerPrice(int price){
        if(this.price > price){
            this.price = price;
        }
    }

    //더 긴 장르로 선택
    public void updateGenre(String genre){
        this.category = genre;
    }


    //해당 소설 정보
    @Override
    public String toString() {
       return "novel id : " + this.id
               + "title : " + this.title;
    }

}
