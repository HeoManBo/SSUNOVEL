package NovelForm.NovelForm.global;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.List;

/**
 *  에러 결과 메시지 생성 클래스
 */
public class ErrorResultCreater {

    /*
        object 에러를 Json 형태로 반환
     */
    public static String objectErrorToJson(List<FieldError> errorList) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, String> map = new HashMap<>();

        for (FieldError error : errorList) {
            map.put(error.getField(), error.getDefaultMessage());
        }

        System.out.println("objectMapper.writeValueAsString(map) = " + objectMapper.writeValueAsString(map));
        
        return objectMapper.writeValueAsString(map);
    }

}
