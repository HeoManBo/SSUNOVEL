package NovelForm.NovelForm.domain.novel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "recommend_novel")
public class RecommendNovel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommend_idx")
    private Long id;


    @OneToOne
    @JoinColumn(name = "origin_novel_idx", referencedColumnName = "novel_idx")
    private Novel originNovel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommend_novel_idx", referencedColumnName = "novel_idx")
    private Novel recommendNovel;



}
