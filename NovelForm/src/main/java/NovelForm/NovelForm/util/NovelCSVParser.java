package NovelForm.NovelForm.util;


import NovelForm.NovelForm.domain.novel.Author;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.repository.AuthorRepository;
import NovelForm.NovelForm.repository.NovelRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.util.*;

/**
 * CSV로 변환한 파일 정보를 DB에 저장하는 기능입니다.
 */
public class NovelCSVParser {

    //              0       1         2       3        4          5           6            7        8        9
    // CSV  순서 : title	is_complete	genre	author	synopsis	price	total_episode	comment	 img_link url_link

    // 고려사향 1 : 작가가 이미 DB상에 존재하는 경우 --> DB를 검색해서 있으면 해당 객체로 넣고
    // 없다면 작가 이름에 맞는 객체를 하나 만들어서 해당 작가를 DB에 삽입
    // 이때 새로 들어온 작가 객체가 있으므로 작가를 찾을 때 DB에서 찾고 -> 새로 추가한 작가 리스트에서 찾음
    Map<String, Novel> repo = new HashMap<>(); // Novel 정보 저장 key : 공백을 제거한 소설 명  value : 해당 소설 명의 작품 객체
    Map<String, Author> auth = new HashMap<>(); // auth 정보 저장 key : 공백을 제거한 작가 명 value : 해당 작가 객체

    /**
     *
     * @param fileName : 파싱할 CSV 파일 이름 (NovelForum 디렉토리 안에 있다고 가정)
     * @return 한 소설 정보마다 파싱한 List<String>이 List안에 저장되서 리턴 -> 정제하여 저장해야함.
     */
    static public List<List<String>> read(String fileName){
        String path = System.getProperty("user.dir"); // Novel 프로젝트 디렉토리에 CSV 파일이 있다고 가정

        List<List<String>> result = new ArrayList<>();
        String[] parsing;

        try{
            CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(path + "\\" + fileName)));
            csvReader.readNext(); // header pass;

            while((parsing = csvReader.readNext()) != null){
                result.add(Arrays.asList(parsing));
            }
        } catch (CsvValidationException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return result;
    }

    private static int deleteComma(String s) {
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

    public void initInsert(AuthorRepository authorRepository, NovelRepository novelRepository){
        String[] names = {"naver.csv", "kakao.csv", "munpia.csv", "ridibooks.csv"};

        for(int i=0; i<names.length; i++){
            mergeNovelFile(names[i], i);
        }
        //이제 DB에 넣는다
        authorRepository.saveAll(auth.values());
        novelRepository.saveAll(repo.values());
    }

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
}
