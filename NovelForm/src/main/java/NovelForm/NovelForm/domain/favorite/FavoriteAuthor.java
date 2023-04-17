package NovelForm.NovelForm.domain.favorite;


import NovelForm.NovelForm.domain.member.Member;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.global.BaseEntityTime;
import jakarta.persistence.*;

@Entity
public class FavoriteAuthor extends BaseEntityTime {

    @Id
    @GeneratedValue
    @Column(name = "favorite_novel_id")
    private Long id;

    private String status;


    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;


    @ManyToOne
    @JoinColumn(name = "novel_id")
    private Novel novel;

}
