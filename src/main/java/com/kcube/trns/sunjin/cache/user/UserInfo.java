package com.kcube.trns.sunjin.cache.user;

public record UserInfo(
        Long userId,
        String name,
        String userDisp,
        Long dprtId,
        String dprtName,
        Long gradeId,
        String gradeName,
        Long pstnId,
        String pstnName,
        String trnsSrc,
        String trnsKey
) {
}
// 어노테이션 관련 팁
// @Component - record에는 불가 -> 빈으로 등록할 일이 있으면 클래스로 전환필요~
// @Serializable - 객체를 파일로 저장하거나 네트워크 전송할 경우
// @Builder, @Getter - 필요없음! record는 이미 all-args 생성자 + getter 내장!!

/**
    record 쓸 때 주의점
  ㅇ 필드가 고정되고 불변입니다 (setter 없음 )
  ㅇ 직렬화, 역직렬화 할때 문제가 생기면 @JsonProperty로 명시가능 (Jackson 사용시)
     예)
     @JsonProperty("user_id")
     String userId


 */