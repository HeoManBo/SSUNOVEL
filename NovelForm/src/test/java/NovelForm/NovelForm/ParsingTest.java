package NovelForm.NovelForm;


import NovelForm.NovelForm.domain.novel.Author;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.repository.AuthorRepository;
import NovelForm.NovelForm.repository.NovelRepository;
import NovelForm.NovelForm.util.NovelCSVParser;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.*;

@Transactional
@SpringBootTest
public class    ParsingTest {

    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private AuthorRepository authorRepository;

    //              0       1         2       3        4          5           6            7        8        9
    // CSV  순서 : title	is_complete	genre	author	synopsis	price	total_episode	comment	 img_link url_link

    // 고려사향 1 : 작가가 이미 DB상에 존재하는 경우 --> DB를 검색해서 있으면 해당 객체로 넣고
    // 없다면 작가 이름에 맞는 객체를 하나 만들어서 해당 작가를 DB에 삽입
    // 이때 새로 들어온 작가 객체가 있으므로 작가를 찾을 때 DB에서 찾고 -> 새로 추가한 작가 리스트에서 찾음
    Map<String, Novel> repo = new HashMap<>(); // Novel 정보 저장 key : 공백을 제거한 소설 명  value : 해당 소설 명의 작품 객체
    Map<String, Author> auth = new HashMap<>(); // auth 정보 저장 key : 공백을 제거한 작가 명 value : 해당 작가 객체

    private void mergeNovelFile(String name, int idx) {
        //name에 해당하는 csv 파일 파싱하기
        List<List<String>> novel_info = NovelCSVParser.read(name);

        //각 소설 정보를 돌면서
        for (List<String> novel : novel_info) {
            //이미 기존의 Map에 작가가 있는지 확인한다.
            String removeWhiteSpace = novel.get(0).replace(" ", "");
            Novel presentNovel;
            if((presentNovel = repo.get(removeWhiteSpace)) != null){ //기존에 이미 있는 소설이라면
                presentNovel.plusDownload_cnt(Integer.parseInt(novel.get(7))); //리뷰 수를 더한다.
                settingURL(presentNovel, novel.get(9), idx); // URL 세팅
                presentNovel.lowerPrice(deleteComma(novel.get(5))); //더 낮은 가격으로 Price 설정
                if(presentNovel.getCategory().length() <= novel.get(2).length()){ //더 긴 장르 명으로 장르 선택,
                    presentNovel.updateGenre(novel.get(2));
                }
            }
            else{
                //기존에 없는 소설이라면 소설 생성
                Novel newNovel = Novel.builder()
                        .title(novel.get(0))
                        .is_finished(novel.get(1))
                        .category(novel.get(2))
                        .summary(novel.get(4))
                        .price(deleteComma(novel.get(5)))
                        .episode(deleteComma(novel.get(5)))
                        .download_cnt(Integer.parseInt(novel.get(7)))
                        .cover_image(novel.get(8))
                        .build();
                settingURL(newNovel, novel.get(9), idx);
                String removeWhiteSpaceAuthor = novel.get(3).replace(" ","");
                Author presentAuthor;
                if((presentAuthor = auth.get(removeWhiteSpaceAuthor)) != null){ //기존에 이미 존재하는 작가라면
                    newNovel.addAuthor(auth.get(removeWhiteSpaceAuthor));
                }else{ //없다면 새로 만든다.
                    Author newAuthor = new Author(novel.get(3));
                    auth.put(removeWhiteSpaceAuthor, newAuthor);
                    newNovel.addAuthor(newAuthor);
                }
                //작가까지 넣었다면 소설을 Map에 넣는다
                repo.put(novel.get(0).replace(" ", ""), newNovel);
            }
        }

    }

    /**
     * 초기 DB엔 아무런 데이터가 존재하지 않으므로 DB안에 소설 or 작가가 있는지 확인할 필요는 없다.
     */
    @Test
    @Rollback(false)
    void initInsert(){
        String[] names = {"naver.csv", "kakao.csv", "munpia.csv", "ridibooks.csv"};

        for(int i=0; i<names.length; i++){
            mergeNovelFile(names[i], i);
        }
        //이제 DB에 넣는다
        authorRepository.saveAll(auth.values());
        novelRepository.saveAll(repo.values());
    }


    /**
     * URL 세팅
     * idx 0 : naver, 1 : kakao, 2 : munpia, 3 : ridibooks;
     */
    private void settingURL(Novel novel, String url, int idx) {
        if(idx == 0){
            novel.updateNaverUrl(url);
        }
        else if(idx == 1){
            novel.updateKakaoUrl(url);
        }
        else if(idx == 2){
            novel.updateMunpiaUrl(url);
        }
        else if(idx == 3){
            novel.updateRidiUrl(url);
        }
    }



    @Test
    @Rollback(false) //DB에 삽입되는 거 확인하고 싶다면 주석 해제
    void 파싱후저장(){
        //일단 파싱해오기
        List<List<String>> read = NovelCSVParser.read("novel.csv");//테스트
        System.out.println(read.size());
//        List<Novel> newNovelList = new ArrayList<>();
//        List<Author> newAuthorList = new ArrayList<>();
//        List<Author> currentAuthor = authorRepository.findAll(); // DB안에 있는 작가 리스트 가져옴
//        for (List<String> info : read) {
//            //1.해당 작가명이 DB상에 이미 존재하는지
//            Optional<Author> inDBAuthor = currentAuthor.parallelStream().filter(author -> author.getName().equals(info.get(3))).findAny();
//            Author isPresent = null;
//            if(inDBAuthor.isPresent()){
//                isPresent = inDBAuthor.get();
//            }
//            Novel newNovel = null;
//            if(isPresent == null){ //DB에 현재 작가 이름에 속하는 Author 튜플이 없으면.
//                Author newAuthor = null;
//                //2.현재 새로 추가한 작가 중에 동일한 이름의 작가가 있는지 찾음
//                Optional<Author> findList= newAuthorList.parallelStream().filter(author -> author.getName().equals(info.get(3))).findAny();
//                if(findList.isEmpty()){ //1,2 둘다 없다면 새로 만듬
//                    newAuthor = new Author(info.get(3));
//                    newAuthorList.add(newAuthor);
//                }else{
//                    newAuthor = findList.get();
//                }
//                int review_cnt = deleteComma(info.get(7));
//                int epi = deleteComma(info.get(6));
//                newNovel = Novel.builder()
//                        .title(info.get(0))
//                        .is_finished(info.get(1))
//                        .category(info.get(2))
//                        .summary(info.get(4))
//                        .price(Integer.parseInt(info.get(5)) * 100)
//                        .episode(epi)
//                        .download_cnt(review_cnt)
//                        .cover_image(info.get(8))
//                        .is_kakao(info.get(9))
//                        .build();
//                newNovel.addAuthor(newAuthor);
//            }else{
//                int review_cnt = deleteComma(info.get(7));
//                int epi = deleteComma(info.get(6));
//                newNovel = Novel.builder()
//                        .title(info.get(0))
//                        .is_finished(info.get(1))
//                        .category(info.get(2))
//                        .summary(info.get(4))
//                        .price(Integer.parseInt(info.get(5)) * 100)
//                        .episode(epi)
//                        .download_cnt(review_cnt)
//                        .cover_image(info.get(8))
//                        .is_kakao(info.get(9))
//                        .build();
//                newNovel.addAuthor(isPresent);
//            }
//            newNovelList.add(newNovel);
//        }
//        authorRepository.saveAll(newAuthorList);
//        novelRepository.saveAll(newNovelList);
    }


    private int deleteWon(String s){
        if(s.contains("원")){
            String[] split = s.split("원");
            return Integer.parseInt(split[0]);
        }
        else{
            return Integer.parseInt(s);
        }
    }


    private int deleteComma(String s) {
        if(s.contains(",")){
            String[] split = s.split(",");
            StringBuilder toInt = new StringBuilder();
            for (String s1 : split) {
                toInt.append(s1);
            }
            return Integer.parseInt(toInt.toString());
        }
        else{
            return Integer.parseInt(s);
        }
    }
}
