package NovelForm.NovelForm.domain.novel.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 검색시 반환될 JSON DTO
 *
 */
@Getter
@AllArgsConstructor
public class SearchDto {
    private MidFormmat forNovel;  //검색어에 대한 소설명이 담길 리스트입니다.
    private MidFormmat forAuthor;  //검색어에 대한 작가명이 담길 리스트입니다.
}
