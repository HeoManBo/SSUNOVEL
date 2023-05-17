package NovelForm.NovelForm.domain.alert.domain;


import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.global.BaseEntityTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Alert extends BaseEntityTime {
    @Id
    @GeneratedValue
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

    //알림이 발생한 곳의 url;
    @Column(name = "url")
    private String url;

    public void addMember(Member member){
        this.member= member;
        member.addAlert(this);
    }

}
