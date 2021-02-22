package hello.hellospring.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    // IDENTITY는 DB가 알아서 ID를 생성해주는 모드
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;


//    @Column(name = "username") DB의 컬럼명 지정, 변수와 같을 경우에는 어노테이션 생략가능
    private String name;

    @Embedded
    private Address address;

    @JsonIgnore
    @OneToMany(mappedBy = "member") // 거울일 뿐, 값이 변경되지 않음
    private List<Order> orders = new ArrayList<>();

}
