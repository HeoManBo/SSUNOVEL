package NovelForm.NovelForm.domain.member.domain;


import NovelForm.NovelForm.domain.alert.domain.Alert;
import NovelForm.NovelForm.domain.box.domain.Box;
import NovelForm.NovelForm.domain.comment.Comment;
import NovelForm.NovelForm.domain.community.CommunityPost;
import NovelForm.NovelForm.domain.favorite.domain.FavoriteAuthor;
import NovelForm.NovelForm.domain.favorite.domain.FavoriteBox;
import NovelForm.NovelForm.domain.favorite.domain.FavoriteNovel;
import NovelForm.NovelForm.domain.like.domain.Like;
import NovelForm.NovelForm.domain.novel.Review;
import NovelForm.NovelForm.global.BaseEntityTime;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 멤버 Table mapping
 * 
 * cascade의 경우에는 member 하나 하고만 연관이 있는 이들에만 걸어야 한다.
 * 여러 멤버와 연관이 있는 경우에는 cascade로 삭제하지 말자
 *
 * Cascade.REMOVE 적용 : FavoriteNovel, FavoriteAuthor, FavoriteBox, Like, Alert
 * 삭제 시 직접 삭제 해야 하는 것들 : Box, Review, Comment, CommunityPost
 *
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@ToString(exclude = {"alerts", "favorite_novels" , "communityPosts", "favoriteBoxes"})
public class Member extends BaseEntityTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_idx")
    private Long id;

    @Column(unique = true)
    private String email;

    @Column
    private String password;


    @Column
    private Integer age;

    @Column
    private String nickname;

    @Column
    @Enumerated(EnumType.STRING) //DB에 enum 값을 String의 형태로 저장
    private Gender gender;

//  SNS 로그인 기능은 아직 미구현.
    @Column(nullable = true)
    private String sns_token;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private TokenSource token_source;

    @Column
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    /**
     * 이미지 경로 저장
     */
    @Column(nullable = true)
    private String profile_image;

    // mapped 를 통해 member를 참조하고 있는 aler 리t스트를 모음.
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Alert> alerts = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<FavoriteNovel> favorite_novels = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<CommunityPost> communityPosts = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<FavoriteBox> favoriteBoxes = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<FavoriteAuthor> favoriteAuthors = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Box> boxes = new ArrayList<>();


    @Builder
    public Member(String email, String password, String nickname, Gender gender, LoginType loginType, Integer age) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.gender = gender;
        this.loginType = loginType;
        this.age = age;
    }


    public void updateMember(String password, String nickname, Gender gender, Integer age){
        this.password = password;
        this.nickname = nickname;
        this.gender = gender;
        this.age = age;
        this.updateTime();
    }


    //연관관계 메소드 --> 파라미터로 넘어온 객체에서 해당 함수 호출
    //알람 추가
    public void addAlert(Alert alert){
        this.alerts.add(alert);
    }

    //즐겨찾기한 소설 추가
    public void addFavoriteNovel(FavoriteNovel favoriteNovel){
        // Q : favoriteNovel의 member와 이 멤버가 같은지 비교해야할까?
        this.favorite_novels.add(favoriteNovel);
    }

    //작성한 게시물 추가
    public void addCommunityPost(CommunityPost communityPost){
        this.communityPosts.add(communityPost);
    }

    //작성한 댓글 추가
    public void addComment(Comment comment){
        this.comments.add(comment);
    }


    //박스 추가
    public void addFavoriteBoxes(FavoriteBox favoriteBox){
        this.favoriteBoxes.add(favoriteBox);
    }

    public void addMyReview(Review review){
        this.reviews.add(review);
    }

    //리뷰 삭제 메소드
    public void deleteMyReview(Review deleteReview) {
        this.reviews.remove(deleteReview);
    }
}
