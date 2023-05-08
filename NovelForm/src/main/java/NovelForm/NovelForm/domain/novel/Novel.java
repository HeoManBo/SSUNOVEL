package NovelForm.NovelForm.domain.novel;


import NovelForm.NovelForm.domain.member.domain.Review;
import NovelForm.NovelForm.global.BaseEntityTime;
import jakarta.persistence.*;
import lombok.*;

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


    // 플랫폼 추가
    @Column
    private int is_naver;
    @Column
    private int is_kakao;
    @Column
    private int is_ridi;
    @Column
    private int is_munpia;


    //작품 관점에서 여러 작품은 하나의 작가에 속하므로 N:1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_idx")
    private Author author;

    // 수정 해야함..
    /**
     * @OneToOne 은 주인이 아닌쪽에 lazy를 걸어도 즉시 로딩 됨. --> 바꿔야 함
     *
     */
    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Platform> platforms = new ArrayList<>();


    @Column
    private String is_finished;

    @Column
    // @Convert(converter = CategoryConverter.class)
    private String category;

//    @OneToOne(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private Week week;
//
//    @OneToOne(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private Platform platforms;
    @OneToMany(mappedBy = "novel",fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @Builder
    public Novel(String title, String summary, int episode, int price, int download_cnt, String is_finished,
                 String cover_image, double rating, int review_cnt, String category, Author author,
                 int is_naver, int is_kakao, int is_ridi, int is_munpia) {
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


    public void addPlatform(Platform platform){
        this.platforms.add(platform);
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
