package NovelForm.NovelForm.domain.novel.dto.searchdto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 검색시 반환될 JSON DTO
 *
 */
@Getter
@AllArgsConstructor
public class SearchDto {
    @Schema(description = "검색어에 대한 소설 정보가 담길 리스트")
    private MidFormmat forNovel;  //검색어에 대한 소설명이 담길 리스트입니다.
    @Schema(description = "검색어에 대한 작가가 가지고 있는 소설 정보가 담길 리스트")
    private MidFormmat forAuthor;  //검색어에 대한 작가명이 담길 리스트입니다.
}
