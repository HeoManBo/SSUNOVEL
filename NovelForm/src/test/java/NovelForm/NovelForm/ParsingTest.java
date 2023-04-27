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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@SpringBootTest
public class ParsingTest {

    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private AuthorRepository authorRepository;

    //              0       1         2       3        4          5           6            7        8
    // CSV  순서 : title	is_complete	genre	author	synopsis	price	total_episode	comment	 img_link

    // 고려사향 1 : 작가가 이미 DB상에 존재하는 경우 --> DB를 검색해서 있으면 해당 객체로 넣고
    // 없다면 작가 이름에 맞는 객체를 하나 만들어서 해당 작가를 DB에 삽입
    // 이때 새로 들어온 작가 객체가 있으므로 작가를 찾을 때 DB에서 찾고 -> 새로 추가한 작가 리스트에서 찾음


    //@Test
    @Rollback(false) //DB에 삽입되는 거 확인하고 싶다면 주석 해제
    void 파싱후저장(){
        //일단 파싱해오기
        List<List<String>> read = NovelCSVParser.read("series1.csv");//테스트
        List<Novel> newNovelList = new ArrayList<>();
        List<Author> newAuthorList = new ArrayList<>();
        List<Author> currentAuthor = authorRepository.findAll(); // DB안에 있는 작가 리스트 가져옴
        for (List<String> info : read) {
            //1.해당 작가명이 DB상에 이미 존재하는지
            Optional<Author> inDBAuthor = currentAuthor.parallelStream().filter(author -> author.getName().equals(info.get(3))).findAny();
            Author isPresent = null;
            if(inDBAuthor.isPresent()){
                isPresent = inDBAuthor.get();
            }
            Novel newNovel = null;
            if(isPresent == null){ //DB에 현재 작가 이름에 속하는 Author 튜플이 없으면.
                Author newAuthor = null;
                //2.현재 새로 추가한 작가 중에 동일한 이름의 작가가 있는지 찾음
                Optional<Author> findList= newAuthorList.parallelStream().filter(author -> author.getName().equals(info.get(3))).findAny();
                if(findList.isEmpty()){ //1,2 둘다 없다면 새로 만듬
                    newAuthor = new Author(info.get(3));
                    newAuthorList.add(newAuthor);
                }else{
                    newAuthor = findList.get();
                }
                newNovel = Novel.builder()
                        .title(info.get(0))
                        .is_finished(info.get(1))
                        .category(info.get(2))
                        .summary(info.get(4))
                        .price(Integer.parseInt(info.get(5)) * 100)
                        .episode(Integer.parseInt(info.get(6)))
                        .download_cnt(Integer.parseInt(info.get(7)))
                        .cover_image(info.get(8))
                        .build();
                newNovel.addAuthor(newAuthor);
            }else{
                newNovel = Novel.builder()
                        .title(info.get(0))
                        .is_finished(info.get(1))
                        .category(info.get(2))
                        .summary(info.get(4))
                        .price(Integer.parseInt(info.get(5)) * 100)
                        .episode(Integer.parseInt(info.get(6)))
                        .download_cnt(Integer.parseInt(info.get(7)))
                        .cover_image(info.get(8))
                        .build();
                newNovel.addAuthor(isPresent);
            }
            newNovelList.add(newNovel);
        }
        authorRepository.saveAll(newAuthorList);
        novelRepository.saveAll(newNovelList);
    }
}
