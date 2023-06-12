package NovelForm.NovelForm.global.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3Client;

    /**
     * 기존 파일의 내용을 지우고 다시 쓴다.
     *
     * @param localFilePath
     * @param key
     * @return
     */
    public Boolean download(String localFilePath, String key) {

        S3Object s3Object = amazonS3Client.getObject(bucket, key);
        File localFile = new File(localFilePath);

        try(FileOutputStream fos = new FileOutputStream(localFile)){
            byte[] buffer = new byte[1024];
            int bytesRead;

            while((bytesRead = s3Object.getObjectContent().read(buffer)) != -1){
                fos.write(buffer, 0, bytesRead);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
