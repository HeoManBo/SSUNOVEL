package NovelForm.NovelForm.domain.member;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Gender {
    MALE, FEMALE;

    /**
     * 요청으로 들어온 String을 enum타입으로 바꾸기
     * @param val
     */
    @JsonCreator
    public static Gender stringToGender(String val){
        for (Gender value : Gender.values()) {
            if(value.name().equals(val)){
                return value;
            }
        }
        return null;
    }
}

