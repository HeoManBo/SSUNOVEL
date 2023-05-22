package NovelForm.NovelForm.domain.novel;

import NovelForm.NovelForm.domain.novel.dto.CategoryDto;

import java.util.List;


public interface CustomNovelRepository {
    List<Novel> findGenreAndPlatformNovel(CategoryDto categoryDto);
    Long totalCount(CategoryDto categoryDto);
}
