package NovelForm.NovelForm.domain.novel;


import NovelForm.NovelForm.domain.novel.dto.CategoryDto;
import NovelForm.NovelForm.domain.novel.dto.MainDto;
import NovelForm.NovelForm.domain.novel.dto.detailnoveldto.DetailNovelInfo;
import NovelForm.NovelForm.domain.novel.dto.searchdto.MidFormmat;
import NovelForm.NovelForm.domain.novel.dto.searchdto.NovelDto;
import NovelForm.NovelForm.repository.AuthorRepository;
import NovelForm.NovelForm.repository.NovelRepository;
import NovelForm.NovelForm.util.NovelCSVParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class NovelService {
    private final NovelRepository novelRepository;
    private final AuthorRepository authorRepository;
    public static final int PagingSize = 10; //페이징할 때 크기 기본적으로 10개로 설정,

    /**
     *
     * keyword : 검색어
     * return value : 검색어에 해당되는 소설의 정보를 가져온디.
     * 단순히 조회만 하므로 읽기 전용으로 가져온다.
     */
/*
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
*/

    // keyword가 포함되는 소설 제목과, 페이징 번호에 해당하는 소설 리스트를 반환한다.
    @Transactional(readOnly = true)
    public MidFormmat findNovels(String keyword, int novelPageNum, String orderBy){
        //소설 검색
        Pageable novelPageable = PageRequest.of(novelPageNum, PagingSize);//페이징은 0번부터시작
        Page<Novel> findNovels = novelRepository.findByTitleWithPaging(keyword, novelPageable);
        //조건에 만족하는 소설이 없다면 반환
        if(findNovels.getTotalElements() == 0){
            return new MidFormmat(0, null);
        }
        List<NovelDto> novelDtoList = findNovels.getContent().stream()
                .map(novel -> new NovelDto(novel.getTitle(), novel.getAuthor().getName(), novel.getCover_image(),
                        novel.averageRating(), novel.getReview_cnt(), novel.getCategory(), novel.getId())).toList();
        return new MidFormmat((int)findNovels.getTotalElements(), novelDtoList);
    }

    // keyword가 포함되는 작가가 쓴 소설 제목과, 페이징 번호에 해당하는 소설 리스트를 반환한다.
    @Transactional(readOnly = true)
    public MidFormmat findNovelWithAuthor(String author_name, int authorPageNum, String orderBy){
        //소설 검색
        Pageable authorPageable = PageRequest.of(authorPageNum, PagingSize); //페이징은 0번부터시작
        Page<Novel> findNovels = novelRepository.findByAuthorWithPaging(author_name, authorPageable);
        //조건에 만족하는 소설이 없다면 반환
        if(findNovels.getTotalElements() == 0){
            return new MidFormmat(0, null);
        }
        List<NovelDto> novelDtoList =  findNovels.getContent().stream()
                .map(novel -> new NovelDto(novel.getTitle(),novel.getAuthor().getName(), novel.getCover_image(),
                        novel.averageRating(), novel.getReview_cnt(), novel.getCategory(), novel.getId())).toList();
        return new MidFormmat((int)findNovels.getTotalElements(), novelDtoList);
    }

    /**
     *
     * @param novel_id 에 맞는 novel 정보를 반환합니다.
     * @return novel_id에 대응되는 DetailNovelDto, 없다면 null
     */
    @Transactional(readOnly = true)
    public Novel findNovel(Long novel_id){
        Optional<Novel> find = novelRepository.findById(novel_id);
        Novel novel;
        //없다면 null 처리
        if(find.isEmpty()){
            return null;
        }
        novel = find.get();
        return novel;
    }

    /**
     * 해당 장르에 속한 소설중에서 필터링을 조건에 대응되는 페이지 번호의 소설 리스트을 반환합니다.
     * 필터링 조건은 조건을 만족하는 것부터 내림차순으로 제공됩니다. (리뷰 순이면 리뷰가 많은 것부터 -> 적은 순으로)
     */
    @Transactional(readOnly = true)
    public MidFormmat findNovelWithGenre(String genre, String filtering, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, PagingSize, Sort.by(filtering).descending());
        Page<Novel> findNovels = novelRepository.findByGenreWithFiltering(genre, pageable);
        if(findNovels.getTotalElements() == 0){ //아무런 소설도 찾지 못했다면 null을 반환하자
            return null;
        }
        List<NovelDto> dtos = makeNovelDtoList(findNovels);
        return new MidFormmat((int)findNovels.getTotalElements(), dtos);
    }

    //찾은 소설을 NovelDto List로 변환
    private List<NovelDto> makeNovelDtoList(Page<Novel> findNovels){
        return findNovels.getContent().stream()
                .map(novel -> new NovelDto(novel.getTitle(), novel.getAuthor().getName(), novel.getCover_image(),
                        novel.averageRating(), novel.getReview_cnt(), novel.getCategory(), novel.getId())).toList();
    }

    /**
     * 크롤링 당시 웹 소설에서 댓글이 많은 순으로 소설 20개를 출력합니다.
     */
    public List<NovelDto> findRankingNovel() {
        List<Novel> findNovels = novelRepository.findByNovelRanking();
        return findNovels.stream()
                .map(novel -> new NovelDto(novel.getTitle(), novel.getAuthor().getName(), novel.getCover_image(),
                        novel.averageRating(), novel.getReview_cnt(), novel.getCategory(), novel.getId())).toList();
    }


    /**
     * 카테고리 검색 수행 service입니다.
     */
    @Transactional(readOnly = true)
    public MidFormmat findNovelsWithCategory(CategoryDto categoryDto) {
        List<Novel> findNovels = novelRepository.findGenreAndPlatformNovel(categoryDto);
        List<NovelDto> dto = findNovels.stream()
                .map(novel -> new NovelDto(novel.getTitle(), novel.getAuthor().getName(), novel.getCover_image(),
                        novel.averageRating(), novel.getReview_cnt(), novel.getCategory(), novel.getId())).toList();
        long size = novelRepository.totalCount(categoryDto);
        return new MidFormmat((int)size, dto);
    }
}
