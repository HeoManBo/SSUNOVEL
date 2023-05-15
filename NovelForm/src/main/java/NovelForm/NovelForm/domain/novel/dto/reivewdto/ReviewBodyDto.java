package NovelForm.NovelForm.domain.novel.dto.reivewdto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리뷰 작성시 넘어올 때,
 * 평점, 리뷰 내용이 담길 Dto 입니다.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewBodyDto {

    //최대 0.5 ~ 5.0 부여 가능
    @DecimalMin(value = "0.5")
    @DecimalMax(value = "5.0")
    private double rating;

    @Schema(description = "리뷰 작성 내용")
    private String content;

}
