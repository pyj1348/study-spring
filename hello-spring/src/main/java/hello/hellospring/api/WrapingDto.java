package hello.hellospring.api;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 리스트 반환을 피하기 위한 포장 DTO
 * */
@Data
@AllArgsConstructor
public class WrapingDto<T> {

    private T data;

}
