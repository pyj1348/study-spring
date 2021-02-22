package hello.hellospring.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable // Embeddable 클래스의 속성값들이 전부 상위 엔티티의 속성값으로 삽입된다
@Getter  // Setter 는 가급적 안하는 게 좋다
public class Address {
    private String city;
    private String street;
    private String zipcode;

    protected Address() {  // JPA은 Entity와 Embeddable은 기본 생성자를 필요로 함 (리플렉션, 프록시 등을 위해)
        // 대신 protected로 패키지 내에서 보호
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

}
