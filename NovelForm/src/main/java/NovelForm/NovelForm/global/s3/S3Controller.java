package NovelForm.NovelForm.global.s3;

import NovelForm.NovelForm.global.BaseResponse;
import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("s3")
@RequiredArgsConstructor
public class S3Controller {
    private final S3Service s3Service;

    @GetMapping("/download")
    public BaseResponse<Boolean> download(){

        // NovelForm 패키지 바로 밑에 파일이 저장된다.
        //String[] keyList = {"naver.csv", "kakao.csv", "munpia.csv", "ridibooks.csv"};
        String[] keyList = {"test.csv"};
        String localFilePath = "./NovelForm/";

        for (String key : keyList) {
            s3Service.download(localFilePath + key, key);
        }

        return new BaseResponse<>(true);
    }
}
