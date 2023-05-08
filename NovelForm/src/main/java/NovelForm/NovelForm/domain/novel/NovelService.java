package NovelForm.NovelForm.domain.novel;


import NovelForm.NovelForm.domain.novel.dto.MidFormmat;
import NovelForm.NovelForm.domain.novel.dto.NovelDto;
import NovelForm.NovelForm.domain.novel.dto.SearchDto;
import NovelForm.NovelForm.repository.NovelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class NovelService {
    private final NovelRepository novelRepository;
    private static final int PagingSize = 10; //페이징할 때 크기 기본적으로 10개로 설정,

    /**
     *
     * keyword : 검색어
     * return value : 검색어에 해당되는 소설의 정보를 가져온디.
     * 단순히 조회만 하므로 읽기 전용으로 가져온다.
     */
    @Transactional(readOnly = true)
    public SearchDto searchInfo(String keyword, int novelPageNum, int AuthorPageNum){
        List<NovelDto> findNovels;
        List<NovelDto> findNovelsWithAuthor;

        //검색어가 제목에 포함되는 소설의 전체 개수를 찾는다
        int novelCnt = novelRepository.countWithMatchingTitle(keyword);
        if(novelCnt == 0){ //검색어에 포함되는 소설이 없다면 null 처리
            findNovels = null;
        }else{
            //페이징 번호에 따른 소설을 저장한다.
            findNovels = findNovel(keyword, novelPageNum);
        }
        MidFormmat first = new MidFormmat(novelCnt, findNovels);

        //검색어가 작가명에 포함되는 소설의 전체 개수를 찾는다.
        int authorNovelCnt = novelRepository.countWithMatchingAuthorName(keyword);
        if(authorNovelCnt == 0){
            findNovelsWithAuthor = null;
        }else{
            findNovelsWithAuthor = findNovelWithAuthor(keyword, novelPageNum);
        }

        MidFormmat second = new MidFormmat(authorNovelCnt, findNovelsWithAuthor);

        return new SearchDto(first, second);
    }

    private List<NovelDto> findNovel(String keyword, int novelPageNum){
        //소설 검색
        Pageable novelPageable = PageRequest.of(novelPageNum, PagingSize);//페이징은 0번부터시작
        Page<Novel> findNovels = novelRepository.findByTitleWithPaging(keyword, novelPageable);
        return findNovels.getContent().stream()
                .map(novel -> new NovelDto(novel.getTitle(),novel.getAuthor().getName(), novel.getCover_image(),
                        novel.getRating(), novel.getDownload_cnt(), novel.getCategory(), novel.getId())).collect(Collectors.toList());
    }

    private List<NovelDto> findNovelWithAuthor(String author_name, int authorPageNum){
        //소설 검색
        Pageable authorPageable = PageRequest.of(authorPageNum, PagingSize); //페이징은 0번부터시작
        Page<Novel> findNovels = novelRepository.findByAuthorWithPaging(author_name, authorPageable);
        return findNovels.getContent().stream()
                .map(novel -> new NovelDto(novel.getTitle(),novel.getAuthor().getName(), novel.getCover_image(),
                        novel.getRating(), novel.getDownload_cnt(), novel.getCategory(), novel.getId())).collect(Collectors.toList());
    }

}
