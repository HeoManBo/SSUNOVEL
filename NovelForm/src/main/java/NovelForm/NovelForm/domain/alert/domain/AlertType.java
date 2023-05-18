package NovelForm.NovelForm.domain.alert.domain;

import NovelForm.NovelForm.domain.member.domain.Gender;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum AlertType {
    AUTHOR, COMMUNITY;

    /**
     * 요청으로 들어온 String을 enum타입으로 바꾸기
     * @param val
     */
    @JsonCreator
    public static AlertType stringToGender(String val){
        for (AlertType value : AlertType.values()) {
            if(value.name().equals(val)){
                return value;
            }
        }
        return null;
    }
}
