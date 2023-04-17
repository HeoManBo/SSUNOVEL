package NovelForm.NovelForm.domain.novel;

import NovelForm.NovelForm.global.BaseEntityTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Platform extends BaseEntityTime {
    @Id
    @GeneratedValue
    @Column(name = "platform_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "novel_id",unique = true)
    private Novel novel;

    @Column
    private String status;

    @Column
    private char naver;

    @Column
    private char kakao;

    @Column
    private char munpia;

    @Column
    private char ridibooks;

    @Builder
    public Platform(Novel novel, char naver, char kakao, char munpia, char ridibooks) {
        this.novel = novel;
        this.naver = naver;
        this.kakao = kakao;
        this.munpia = munpia;
        this.ridibooks = ridibooks;
    }
}
