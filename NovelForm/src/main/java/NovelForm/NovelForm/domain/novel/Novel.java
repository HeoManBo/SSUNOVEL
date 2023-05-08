package NovelForm.NovelForm.domain.novel;


import NovelForm.NovelForm.domain.member.Review;
import NovelForm.NovelForm.global.BaseEntityTime;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "novel")
@ToString(exclude = {"author", "reviews"})
public class Novel extends BaseEntityTime {

    @Id
    @GeneratedValue
    @Column(name = "novel_id")
    private Long id;

    @Column
    private String title;

    @Column
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

    //작품 관점에서 여러 작품은 하나의 작가에 속하므로 N:1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    @Column
    private String is_finished;

    @Column
    // @Convert(converter = CategoryConverter.class)
    private String category;

    @Column
    private int is_naver;

    @Column
    private int is_kakao;

    @Column
    private int is_munpia;

    @Column
    private int is_ridi;

    @OneToMany(mappedBy = "novel",fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @Builder
    public Novel(String title, String summary, int episode, int price, int download_cnt, String is_finished,
                 String cover_image, double rating, int review_cnt, String category, Author author,
                 int is_kakao, int is_munpia, int is_naver, int is_ridi) {
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
        this.is_kakao = is_kakao;
        this.is_munpia = is_munpia;
        this.is_ridi = is_ridi;
        this.is_naver = is_naver;
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
        if(review_cnt == 0 ) return 0.0;
        return rating / review_cnt;
    }

    //해당 소설 정보
    @Override
    public String toString() {
       return "novel id : " + this.id
               + "title : " + this.title;
    }
}
