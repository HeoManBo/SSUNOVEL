package NovelForm.NovelForm.domain.favorite.domain;


import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.global.BaseEntityTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class FavoriteNovel extends BaseEntityTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_novel_idx")
    private Long id;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_idx")
    private Novel novel;

    //양뱡향 관계를 위해 두 테이블에 모두 더해줌
    public void addMember(Member member){
        this.member = member;
        member.addFavoriteNovel(this);
    }

    public void addNovel(Novel novel){
        this.novel = novel;
    }


    public FavoriteNovel(Novel novel) {
        this.status = "activated";
        this.novel = novel;
    }
}
