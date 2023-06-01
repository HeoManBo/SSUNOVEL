package NovelForm.NovelForm.domain.novel;

import NovelForm.NovelForm.domain.novel.dto.CategoryDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static NovelForm.NovelForm.domain.novel.NovelService.PagingSize;

@Slf4j
@RequiredArgsConstructor
public class CustomNovelRepositoryImpl implements CustomNovelRepository{

    private final EntityManager em;

    @Override
    public List<Novel> findGenreAndPlatformNovel(CategoryDto categoryDto) {
        String query = "select n from Novel n join fetch n.author";
        String where = " where ";
        List<String> whereCondition = new ArrayList<>();
        String orderBy = " order by " + "n." + categoryDto.getOrderBy() + " desc";
        if (categoryDto.getIsFinished() == 1) { //완결만 보고 싶다면
            whereCondition.add("n.is_finished like '완결'");
        }
        if (!categoryDto.getPlatform().equals("None")) { //플랫폼이 선택되어있으면
            whereCondition.add("n."+categoryDto.getPlatform()+" is not null"); //값이 null이 아닌 경우 가져옴
        }
        if (!categoryDto.getGenre().equals("None")) { //각 장르에 맞게 쿼리를 제작함
            switch (categoryDto.getGenre()) {
                case "현판" ->  whereCondition.add("(n.category like '%현판%' or n.category like '%현대판타지%')");
                case "로판" -> whereCondition.add("(n.category like '%로판%' or n.category like '%로맨스판타지%')");
                case "판타지" -> whereCondition.add("n.category like '판타지%'");
                case "로맨스" -> whereCondition.add("n.category like '%로맨스'");
                default -> whereCondition.add("n.category like '%" + categoryDto.getGenre() +"%'");
            }
        }
        if(!whereCondition.isEmpty()){ //만약 조건이 존재면 where절 추가
            query += where;
            query += String.join(" and ", whereCondition);
        }
        query += orderBy; //기본적인 정렬 순서추가
        return em.createQuery(query)
                .setFirstResult(categoryDto.getPageNum() * PagingSize).setMaxResults(PagingSize)
                .getResultList();
    }

    @Override
    public Long totalCount(CategoryDto categoryDto) {
        String query = "select count(n) from Novel n inner join n.author";
        String where = " where ";
        List<String> whereCondition = new ArrayList<>();
        String orderBy = " order by " + "n." + categoryDto.getOrderBy() + " desc";
        if (categoryDto.getIsFinished() == 1) { //완결만 보고 싶다면
            whereCondition.add("n.is_finished like '완결'");
        }
        if (!categoryDto.getPlatform().equals("None")) { //플랫폼이 선택되어있으면
            whereCondition.add("n."+categoryDto.getPlatform()+" is not null"); //값이 null이 아닌 경우 가져옴
        }
        if (!categoryDto.getGenre().equals("None")) {
            switch (categoryDto.getGenre()) {
                case "현판" ->  whereCondition.add("(n.category like '%현판%' or n.category like '%현대판타지%')");
                case "로판" -> whereCondition.add("(n.category like '%로판%' or n.category like '%로맨스판타지%')");
                case "판타지" -> whereCondition.add("n.category like '판타지'");
                case "로맨스" -> whereCondition.add("n.category like '로맨스'");
                default -> whereCondition.add("n.category like '%" + categoryDto.getGenre() +"%'");
            }
        }
        if(!whereCondition.isEmpty()){
            query += where;
            query += String.join(" and ", whereCondition);
        }
        query += orderBy; //기본적인 정렬 순서추가
        log.info("count query : {}", em.createQuery(query).getSingleResult().getClass().getCanonicalName());
        return Long.parseLong(em.createQuery(query).getSingleResult().toString()); // getClass를 찍어보면 java.lang.Long이 나옴.
    }
}
