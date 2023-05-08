package org.example;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, CsvValidationException {
        String path = System.getProperty("user.dir"); // Novel 프로젝트 디렉토리에 CSV 파일이 있다고 가정
        //CSVReader csvReader = new CSVReader(new FileReader("series1.csv"));
        CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(path + "\\series1.csv")));
        csvReader.readNext();
        List<List<String>> list = new ArrayList<>();
        String[] line;
        int cnt = 0;
        while((line = csvReader.readNext()) != null && cnt < 2){
            list.add(Arrays.asList(line));
            cnt++;
        }
    }
}