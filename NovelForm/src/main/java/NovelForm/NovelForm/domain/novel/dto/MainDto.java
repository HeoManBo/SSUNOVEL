package NovelForm.NovelForm.domain.novel.dto;

import NovelForm.NovelForm.domain.novel.dto.searchdto.NovelDto;
import lombok.Data;

import java.util.List;

/**
 * 메인 화면에 출력할 소설 리스트 Dto 입니다.
 */
@Data
public class MainDto {
    List<NovelDto> rankingNovel; // 별점 + 별점 평균 높은 소설\

    //List<NovelDto> recommandNovel; //차후 머신러닝을 통해 추천할 소설 리스트
}
