package NovelForm.NovelForm.domain.novel;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Week {
    @Id
    @GeneratedValue
    @Column(name = "week_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "novel_id", unique = true)
    private Novel novel;

    @Column
    private char monday;

    @Column
    private char tuesday;

    @Column
    private char wednesday;

    @Column
    private char thursday;

    @Column
    private char friday;
    @Column
    private char saturday;

    @Column
    private char sunday;

    @Builder
    public Week(char monday, char tuesday, char wednesday, char thursday, char friday, char saturday, char sunday) {
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
    }
}
