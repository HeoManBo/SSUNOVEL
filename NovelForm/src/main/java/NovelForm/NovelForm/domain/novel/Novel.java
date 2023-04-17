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
//@ToString(exclude = {"author","platform", "reviews", "week" })
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

    // 수정 해야함..
    /**
     * @OneToOne 은 주인이 아닌쪽에 lazy를 걸어도 즉시 로딩 됨. --> 바꿔야 함
     *
     */
    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Platform> platforms = new ArrayList<>();

    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Week> weeks  = new ArrayList<>();


//    @OneToOne(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private Week week;
//
//    @OneToOne(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private Platform platforms;


    @OneToMany(mappedBy = "novel",fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @Builder
    public Novel(String title, String summary, int episode, int price, int download_cnt,
                 double rating, int review_cnt, Author author, Platform platform, Week week) {
        this.title = title;
        this.summary = summary;
        this.episode = episode;
        this.price = price;
        this.download_cnt = download_cnt;
        this.rating = rating;
        this.review_cnt = review_cnt;
    }

    public void addAuthor(Author author){
        this.author = author;
        author.addNovel(this);
    }

    public void addWeek(Week week){
        this.weeks.add(week);
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
