package NovelForm.NovelForm.domain.novel.dto.searchdto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 *  {
 *      count : 조건에 맞는 전체 조회 수
 *      novel_list{ (페이징 개수만큼 존재)
 *        {
 *          소설 명
 *          작가 이름
 *          소설 이미지(링크)
 *          장르
 *          리뷰 수(크롤링시 기준 리뷰수)
 *          사이트 자체 평점
 *         }
 *      }
 *  }
 */
@Getter
@AllArgsConstructor
public class MidFormmat {

    @Schema(description = "조회한 전체 소설 수")
    private int count;

    @Schema(description = "조건에 맞게 조회한 소설 List")
    private List<NovelDto> dto;

}
