package NovelForm.NovelForm.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Ridi {

    @Id
    @Column(name = "ridi_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long user_idx;

    @Column
    private Long novel_idx;

    @Column
    private double rating;


    public Ridi(Long user_idx, Long novel_idx, double rating) {
        this.user_idx = user_idx;
        this.novel_idx = novel_idx;
        this.rating = rating;
    }
}
