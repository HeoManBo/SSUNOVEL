package NovelForm.NovelForm.domain.alert.domain;


import NovelForm.NovelForm.domain.community.CommunityPost;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.novel.Author;
import NovelForm.NovelForm.global.BaseEntityTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Alert extends BaseEntityTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_idx")
    private Long id;

    /**
     * 회원은 여러 개의 알림을 가질 수 잇음 회원 1 : N 알림의 관계성립
     * N 쪽에 참조하는 키값 설정 --> 멤버 설정
     * 1 쪽에는 List<Alert> 설정.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    private Member member;

    @Column(name = "title")
    private String title;

    @Column(name = "status")
    private String status;

    //맨 처음은 읽지 않음 처리.
    @Column(name = "read_check")
    private int readCheck;

    // 알림 타입 정보
    @Column(name = "alert_type")
    @Enumerated(EnumType.STRING)
    private AlertType alertType;


    //알림에 대해서 요청해야 하는 url;
    // get, Post 등 해당 알림이 발생한 이유 (글에 댓글 달림, 작가 신작 등록)
    // 소설 이라면, 해당 소설 페이지가 나올 수 있도록 소설 상세 정보 요청에 대한 링크가 달린다.
    // 작성 글에 댓글이 달린거라면, 해당 작성글 페이지를 요청하는 링크가 달린다.
    @Column(name = "url")
    private String url;

    public void addMember(Member member){
        this.member= member;
        member.addAlert(this);
    }


    public Alert(String title, String url, AlertType alertType) {
        this.title = title;
        this.url = url;
        this.alertType = alertType;
        this.status = "activated";
        this.readCheck = 0;
    }


    // 작가 신작 알림 등록
    public Alert(Long novelId, Author author, Member member){
        this.title = author.getName() + " 작가의 신작이 등록되었습니다.";
        this.alertType = AlertType.AUTHOR;
        this.url = "https://www.novelforum.site/novel/" + novelId;
        this.status = "activated";
        this.readCheck = 0;
        this.addMember(member);
    }

    public Alert(CommunityPost communityPost, Member member){
        this.title = communityPost.getTitle() + " 작성글에 댓글이 달렸습니다.";
        this.alertType = AlertType.COMMUNITY;
        this.url = "https://www.novelforum.site/community/" + communityPost.getId();
        this.status = "activated";
        this.readCheck = 0;
        this.addMember(member);
    }


    public void read() {
        this.readCheck = 1;
        this.updateTime();
    }
}
