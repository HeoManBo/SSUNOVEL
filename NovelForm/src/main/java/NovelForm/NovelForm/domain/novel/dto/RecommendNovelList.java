package NovelForm.NovelForm.domain.novel.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class RecommendNovelList {
    List<Long> novel_id = new ArrayList<>();
}
