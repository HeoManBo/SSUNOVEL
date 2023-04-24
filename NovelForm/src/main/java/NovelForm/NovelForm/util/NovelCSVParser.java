package NovelForm.NovelForm.util;


import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * CSV로 변환한 파일 정보를 DB에 저장하는 기능입니다.
 */
public class NovelCSVParser {
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

/*    public static void main(String[] args) {
        Runtime r = Runtime.getRuntime();
        System.out.println("전체 힙 메모리 " +r.totalMemory());
        long l = r.freeMemory();
        System.out.println("이용 가능한 힙 메모리 : " + l);
        List<List<String>> read = read("seriesinfo1.csv");
        long l2 = r.freeMemory();
        System.out.println("소설 리스트 정보 얻은 후 가용 가능한 메모리 : " + l2);
        System.out.println("소설 리스트 객체의 크기는 : " + (l-l2)); //바이트 단위
    }*/
}
