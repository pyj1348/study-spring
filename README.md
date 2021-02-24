# Spring / Spirng-boot / JPA / Spring-data-JPA

## 주요 개념 정리

### 1. Spring Framework

    JVM 기반의 애플리케이션을 개발할 때 필요한 많은 기능을 편리하게 제공하는 강력한 프레임워크
    객체를 생성하고 해지하는 등의 모든 객체 "생명주기"를 알아서 관리
    IOC, DL, DI 등의 기술과 개념 적용
    Bean, Repository, Service 등등을 이용
    Entity Manager의 생명주기 관리


### 2. Spring-boot

    Spring이 제공하는 기능들을 사용하기 위해서는 많은 복잡한 환경설정을 진행해야하는데 이를 스프링부트가 많은 부분 자동 셋업해주고
    dependencies 등등을 자동으로 설정해준다


### 3. Persistence Framework

    SQL Mapper : SQL 문장으로 직접적으로 데이터베이스를 다룸 (MyBatis, JdbcTemplate)
    ORM : 객체를 통한 메소드로 간접적으로 데이터베이스를 다룸(JPA, Hibernate, EclipseLink, Spring-Data-JPA..)

    ORM (Object-Relational Mapping) :
    DB 테이블을 Java 객체로 매핑하고 객체의 메소드만으로도 DB 조작 가능, SQL 자동 생성


### 4. JPA (Java Persistence API)

    객체 지향 프로그래밍(ORM)을 지원하기 위한 Java 표준 API (javax.persistence)
    이를 구현체로 만든 것이 Hibernate, EclipseLink ...
    관계형 데이터베이스를 사용하는 방법에 대한 명시를 해준 Interface
    JPQL(Java Persistence Query Language) 사용
    내부적으로 JDBC API 사용

    객체 중심적 개발 가능 - SQL의 늪에서 벗어날 수 있다
    생산성과 유지보수 증가 - 간단한 메소드와 필드값으로 CRUD 가능
    RDB와 Object 사이의 관계 불일치 해결


### 5. Sping-Data-JPA

    JPA를 한단계 더 추상화시켜 더 간편하게 JPA 기술을 사용할 수 있게 만드는 모듈
    기존에 사용하던 Entity Manager 또한 사용하지 않아도 되는 Repository라는 인터페이스 제공
    JPA의 반복적인 코드를 자동화 해준다
    Repositroy의 기본 구현체인 @SimpleJpaRepository는 내부적으로 EntityManager를 사용하고 있음



### 6. Persistence Context (영속성 컨텍스트)

    엔티티를 담고 있는 객체의 집합 덩어리 (논리적인 개념)
    JPA는 영속성 컨텍스트에 속한 엔티티를 DB에 반영한다
    만약 영속성 컨텍스트에 속한 엔티티에 대해 변경이 일어나면 JPA는 이를 더티체킹으로 감지하여
    이를 내부적으로 자동으로 DB에 업데이트 해주기 때문에 따로 쿼리를 날릴 필요가 없다 (@Transactional 내에서)
    Entity Manager는 이 영속성 컨텍스트에 대한 Interface 역할을 한다
    Entity Manager를 통해 엔티티를 CRUD

    persist() : 영속성 컨텍스트로 등록
    flush() : 영속성 컨텍스트 내의 엔티티를 DB에 반영
    find

   * 1차 캐시 : 영속성 컨텍스트 내에 존재하는 캐시
        
        Entity Manager로 접근할 수있다
        
        엔티티를 영속성 컨텍스트로 저장하는 순간(persist()) 엔티티를 캐시에 저장
        
        @Id로 선언한 값과 엔티티를 (key, value)로 메모리에 저장
        
        하나의 트랜젝션 범위 안에서만 사용하는 캐시
        
        즉, 트랜젝션 범위 내에서 같은 ID 값의 객체는 같은 객체로 인식한다


   * 2차 캐시 : 전체 범위에서 쓰는 글로벌 캐시


### 7. 준영속 엔티티

    준영속 엔티티
    영속성 컨텍스트가 더이상 관리하지 않는 엔티티
    이미 DB에 한번 저장되어서 식별자가 존재하는 객체를 가지고 임의로 만들어 낸 복사본 객체는 기존 식별자를 가지고 있지만
    변경을 해도 JPA가 자동으로 commit 해주지 않는 엔티티다. 즉, 영역에서 벗어나 있는 객체다.

    따라서 이 준영속 엔티티를 가지고 DB를 수정하는 방법은 변경감지와 병합 2가지 방법이 있다.
    

### 8. Dirty Checking (변경 감지) 와 Merger(병합)
    
       
    기본적으로 Entity Manager는 영속성 컨텍스트를 관리한다. 이는 어떤 변화가 생기면 자동으로 DB에 SQL을 날려 Commit하여 관리한다.
    원래는 데이터를 변경하면 해당 SQL문을 Repository에 다 날려줘야하는데
    JPA를 활용하면 변경된 내역이 있을 때 자동으로 SQL이 날라가서 DB에 반영된다
    이것을 변경감지라고 한다.
 
1. **변경 감지 기능 (Dirty Checking)**

    준영속 엔티티의 ID값을 이용하여 Repository나 Service를 통해 find를 사용하고 실제 영속성 컨텍스트를 반환받는다.
    그리고 이 영속성 컨텍스트의 값을 준영속 컨텍스트의 값으로 일일이 변경해준다.
    그러면 영속성 컨텍스트의 값이 바뀌었기 때문에 직접 save하지 않아도 JPA가 알아서 Commit 해준다.

2. **병합 (Merge)**

    기본적으로 변경 감지 기능을 이용하는 것이다.
    하지만 Merge는 속성의 선택권이 없이 모든 속성들을 다 준영속 엔티티의 값으로 바꾸어 버리기 때문에
    Null에 대해 안전하지 않고, 원하지 않는 속성들이 변경될 위험이 높다.
    따라서 귀찮더라도 변경감지 기능을 이용하는 것이 좋다.

## Spring의 핵심 기술 개념

* **IoC (Inversion of Control) 제어의 역전**

    프레임워크가 객체를 '경량 컨테이너'에 담아 객체의 모든 생명주기를 관리
    
    객체에 대한 모든 제어권을 프레임워크에 넘기는 것


* **DL(Dependency Lookup) 의존성 검색**

    객체들을 관리하기 위해 컨테이너 내 별도의 저장소에 객체를 빈(Bean) 형태로 저장해놓는데
    이를 API를 통해 사용하고자 하는 빈을 검색하는 방법


* **DI (Dependency Injection) 의존성 주입**

    객체의 의존관계를 빈 설정 정보를 바탕으로 컨테이너가 자동으로 설정해주는 것
    

* **POJO (Plain Old Java Object) 평범한 예전 자바 오브젝트**

    스프링은 불필요하고 복잡한 새로운 EJB(Enterprise JavaBeans)를 사용하지 않고
    
    예전 스타일 그대로 Getter/Setter만 지닌 단순 자바 오브젝트를 사용한다
    
    POJO는 의존성이 없고 테스트 및 유지보수에 좀 더 유연하다


    1. 생성자 주입 - App 조립시점 (스프링 컨테이너에 올라갈 때)
    2. Setter 주입 - 생성 후에 나중에 setter로 주입, 불필요하게 호출될(의존관계가 임의로 변경될) 위협이 있다
    3. 필드 주입 - 수정이 어려워 잘 쓰이지 않는다

    DI는 실행 중에 동적으로 변하는 경우가 거의 없으므로 생성자 주입을 권장


* **AOP (Aspect Oriented Programming) 관점 지향 프로그래밍**

    공통 관심사항 (cross-cutting concern) vs 핵심 관심사항 (core concern)
    
    공통 관심사항과 핵심 관심사항을 분리하여 관리하는 기술이다
    
    무분별하게 중복되는 코드를 한 곳에 모아 중복 되는 코드를 제거할 수 있고
    공통 관심사항 하나만 변경/제거함으로써 유지보수성, 재사용성 증가
    
    핵심 관심 사항 변경 없이 깔끔하게 유지 가능

    AOP로 분리한 영역만 변경하면 됨
    
    적용대상 선택 가능
    
    
    스프링 컨테이너가 DI를 기반으로 하기 때문에 작동한다 (프록시를 DI해줘도 아무튼 진행되니까)

    1. 적용된 대상은 프록시라는 가짜 대상을 만든다
    2. 가짜 프록시를 호출
    3. joinPoint.proceed() 호출 후에 진짜 대상으로 이동


* **OCP (Open-Closed Principle) 개방-폐쇠 원칙**

    " 확장에는 열려있고, 수정에는 닫혀있다. "
    
    객체지향, 인터페이스의 다형성을 기반으로 구현체만 바꾸고
    나머지 코드는 손을 아예 대지 않아도 설정만 변경하여 정상 동작할 수 있게 하는 것


* **MVC**
    
    Model
    View
    Controller

## 스프링 빈을 등록하는 방법
* **1. 컴포넌트 스캔 방식(어노테이션 명시) - 제일 많이 사용**
    
    @Component(Service, Repository, Controller)
    
    하위 패키지들의 클래스들에 어노테이션이 되어 있으면
    스프링 부트가 생성될 때 해당 클래스들을 싱글톤객체(스프링 빈)로 가지고 있는다. (공유&연결)

    @Autowired

    생성자에 어노테이션을 달아놓으면 객체 생성 시점에 스프링 컨테이너에 등록된 해당 스프링 빈(컴포넌트 스캔 때 만들어 둔 것들)을 찾아서 주입


* **2. 설정파일 자바 코드로 직접 스프링 빈 등록**
    
    Controller 빼고 나머지 Service와 Repository는 Config 클래스를 하나 만들어서
    @Configuration / @Bean으로 등록

    
    의존관계가 명확하게 보인다.
    나중에 구현체를 바꾸거나 의존관계를 수정할 때 코드 변경이 편리하다
    즉, 스프링은 다형성이 매우 좋다


* **3. XML 방식**
    
    요즘엔 거의 잘 사용하지 않음

## 프로젝트

### Build tools

* **Gradle, Maven**  

    빌드툴로서 라이브러리 의존 관계도 다 관리해줌  
    필요한 것들을 가져오면, 그것과 의존관계가 있는 것들도 다 땡겨줌
    

* **logback, slf4j**  

    log와 관련된 라이브러리, logback를 더 많이 쓴다
    

* **junit, assertj, mockito**  

    test library
    
### Library Dependency

* **thymeleaf - HTML 생성**  
	
	- spring-boot-starter-thymeleaf


* **Spring Web - REST ful, Spring MVC, Apache Tomcat 내장**
	- spring-boot-starter-web
		- spring-boot-starter-tomcat
		- spring-boot-webmvc


* **Spring boot 공통**  
	- spring-boot-starter
		- spring-core
	- spring-boot-starter-logging
		- slf4j : 로그를 찍는 인터페이스의 모음
		- logback : slf4j의 구현체

* **JPA (Java Persistence API)**  
	- spring-boot-starter-data-jpa
        - spring-boot-starter-aop
        - spring-boot-starter-jdbc : DB 연결
        	- HikariCP : JDBC 2.0버전 이후로 기본적인 DB Connection Pool
        - spring-jdbc
        - hibernate : 자바 표준 JPA의 구현체

* **Test**  
	- spring-boot-starter-test
		- junit : 테스트 프레임워크
		- mockito : 목 라이브러리
		- assertj : 테스트 코드를 좀 더 편하게 도와주는 라이브러리
		- spring-test : 스프링 통합 테스트 지원

* **lombok - getter, setter 생성해줌**  

    환경설정에서 build -> compiler -> annotation processor -> Enable 해줘야함


### Release

    1. 해당 프로젝트 디렉토리까지 이동
    2. gradlew.bat build (Win) / gradlew build (Mac)
        잘 안 될 경우 clean build
    3. ./build/libs 까지 이동
    4. jar 파일 있는지 확인
    5. java -jar 해당파일.jar
    6. 실행 여부 확인
    7. 잘 동작할 시에 jar 파일 서버에 복사

## 웹 개발
    MVC와 템플릿 엔진 = HTML에 동적행위를 추가해줌 (JSP, PHP)
    데이터를 JSON, XML 포맷으로 API로 전달 / 보통 요새는 JSON을 많이 씀
    JSON : {key : value}로 이루어진 데이터 구조
    
### 웹 애플리케이션 계층 구조

   * 컨트롤러 : 웹 MVC의 컨트롤러 역할
   * 서비스 : 핵심 비즈니스 로직 구현 ex) 가입
   * 도메인 : 비즈니스 도메인 객체 ex) 회원, 구매 등
   * 리포지토리 : 메모리에 저장, DB에 접근, 도메인 객체를 DB에 저장하고 관리

    컨트롤러  ->  서비스  ->  리포지토리  -> DB
             \      |          /
              \     v         /
                >  도메인    <
                
### Controller 동작 방식


#### 정적 컨텐츠
    1. 해당 url & html과 mapping된 controller가 있는지 확인
    2. 있으면 controller에게 임무 넘김
    3. 없으면 resources/static 폴더에 해당 url & html이 있는지 확인
    4. 있으면 정적컨텐츠 반환


#### 동적 컨텐츠 (MVC)
    1. Controller가 url을 통해 신호를 받음
    2. Controller가 받은 동적입력값, 키값을 Model에 전달
    3. Controller가 연결된 html 이름을 Spring에 반환
    4. 이를 Spring이 받아서 viewResolver를 호출 후에 html 템플릿 엔진과 연결
    5. 템플릿 엔진이 Model의 값을 참조하여 HTML을 렌더링 변환 후에 반환


#### API 방식 (ResponseBody)
    1. Controller가 url을 통해 신호를 받음
    2. ResponseBody 어노테이션이 붙어있으면 위와 다른 방식으로 전개됨
    3. 즉, Controller가 받은 동적 입력값을 그냥 Body값으로 Spring에 전달
    4. Spring이 viewResolver를 호출하지 않고 HTTP Response에 데이터를 그대로 넘기려고 함
    5. 그러므로 HttpMessageConverter를 호출하고 객체 타입 조사
    6. 만약에 데이터가 String이 아니라 객체면 JSON 방식으로 준다
    7. String -> StringHttpMessageConverter / Object -> MappingJackson2HttpMessageConverter

* HTTP Accept Header에 반환 타입을 명시하지 않았을 경우 !!
* 그러나 요즘엔 JSON을 많이 쓴다
* API를 쓸 때는 절대 엔티티를 반환하면 안 된다


## JDBC 활용

* **1. 순수 JDBC**

    커넥션, 쿼리, 결과셋, 릴리즈 모두 직접 해야한다

    동작원리를 파악할 수 있지만 코드의 중복이 많아지고 생산성이 약하다


* **2. JDBC Template**

    코드 중복을 상당부분 줄여준다. 커넥션, insert 구문 생성 등 많은 부분 지원해주지만 쿼리는 대부분 직접 짜야한다


* **3. JPA**

    객체 지향 프로그래밍과 ORM(Object-Relational Mapping)을 장려하는 DB 생성/활용을 위해 자바 표준으로 지정된 **인터페이스**

    코드 중복 제거 뿐 아니라 SQL 쿼리도 JPA가 다 직접 만들어서 해준다
 
    생산성이 크게 높아지며, 데이터 중심 설계에서 객체 중심 설계로 패러다임을 바꿔준다

    JPA를 자바 표준 인터페이스로 **Hibernate**가 구현체로 사용

    Spring이 EntityManager를 생성해서 가지고 있다가 주입해줌
    
    EntityManager가 내부적으로 DataSource의 정보를 들고 있음

    EntityManager를 통한 모든 데이터 변경은 Transaction 안에서 이루어져야 한다 (@Transactional)

    원래 EntityManager는 @PersistenceContext를 써줘야하는데
    Spring-data-JAP에서 @Autowired로 써도 되도록 지원해줌
    
    즉, 일반 DI처럼 사용해도 무방
    
    (+심지어 생성자주입 때 생략가능하다)
    
    (+lombok의 @RequiredArgsConstructor를 쓰면 final 요소는 생성자구문도 생략가능)


* **4. Spring-data-JPA**

    자바 표준 JPA <- Hibernate <- Spring-data-JPA

    JPA와 Spring-boot에 Spring-data-JPA를 추가

    리포지터리의 구현 클래스 없이 인터페이스만으로도 개발 가능
    
    기본 CRUD 기능 제공 - 공통(PK 기반)이 아닌 것은 규칙에 준거하여 메소드 인터페이스만 정의해 놓으면
    알아서 JPQL 생성해줌
    
    복잡한 동적 쿼리는 **Querydsl**를 사용
    
    ex) findByXAndY => select m from Member m where m.X and m.Y

    JpaRepository 라는 인터페이스를 상속하는 클래스가 있다면 (+ 내가 만든 Repository도 같이 상속)

    Spring-data-JPA가 자동으로 프록시를 이용해 해당 인터페이스의 구현체를 만들어서 Bean으로 등록해준다

    그러면 내가 만들고 상속했던 인터페이스(MemberRepository)를 생성자로 DI해준다

    

## JPA에서 동적 쿼리 해결

    MyBatis는 동적 쿼리 생성하는 데에 굉장한 이점이 있다
    JPA의 JPQL 동적 생성(JPA Criteria)은 이런 부분에서 많은 단점이 있다
    이를 QueryDSL이 강력하게 서포팅해준다

* JPA Criteria
    
    JPA에서 제공하는 JPQL 동적쿼리 생성 표준이지만 치명적인 단점 보유
    
    -> 가독성, 유지보수, 쿼리를 예측하는 것이 어렵다

* QueryDSL
    
    1. SQL(JPQL)과 모양이 유사하면서 자바 코드로 동적 쿼리를 편리하게 생성할 수 있는 오픈소스
    2. 직관적인 문법
    3. 높은 개발 생산성
    4. 컴파일 시점에 빠른 문법 오류 발견
    5. 코드 자동완성
    6. 코드 재사용
    7. 깔끔한 DTO 조회


## API 개발

* **Entity를 API에 직접 매핑**

    엔티티는 여러 곳에서 같이 쓰는 객체이므로 DTO를 만들어서 JSON으로 넘겨주고 받는 것이 좋다
    
    엔티티를 외부에 바로 노출하는 것은 정말 안 좋다. 파라미터로 받는 것도 안 좋다.
    
    * 단점
        1. 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
        2. 엔티티에 API 검증을 위한 로직이 들어간다. (@NotEmpty 등등)
        3. 실무에서는 회원 엔티티를 위한 다양한 API가 만들어지는데 한 엔티티 객체에 각각의 API를 위한 모든 요청 요구사항을 담기는 어렵고 지저분하다.
        4. 엔티티가 변경되면 모든 API 스펙이 변한다. side-effect가 예측 불가능할 정도로 크다
        5. 엔티티에 대한 모든 정보가 노출된다. (jackson의 @JsonIgnore가 없는 모든 변수)
        6. 반환값의 형태가 엔티티 구조로 굳어버려서 새로운 속성을 추가하는 식의 유연성이 매우 떨어진다  

    * 장점
        1. API DTO만 봐도 어떤 값만 주고 받는지 알 수 있다
        2. Validation을 엔티티 계층에서 하지 않고 DTO에서 할 수 있다

   **그렇지만 엔티티를 직접 매핑하는 것은 절대 하지마라 !!**

   **DTO를 API에 매핑**

   **Entity와 API 사이에 DTO 생성**


* **쿼리 최적화**

    API를 만들 때 다량 조회를 해서 만드는 경우 조회 최적화를 해야한다
    이때 양방향 연관관계 문제 + 지연로딩 프록시 객체 반환 문제 + N + 1 문제가 발생

    양방향 관계 문제 발생 -> @JsonIgnore
    
    LAZY 프록시 객체 조회 문제 발생 -> Hibernate5Module 모듈 등록 -> 프록시 객체는 null 값으로 출력
    
    N + 1 문제 -> Fetch join

    Fetch join
    JPA 문법 : inner join으로 속성값 연달아 붙여서 한 번의 쿼리로 조회
    

* **Paging**

    1:다 (컬렉션 엔티티) 관계에서는 (fetch) join하는 순간 페이징이 불가능하다
    
    데이터가 1 x N이기 때문에 N을 기준으로 튜플수가 늘기 때문에
    
    우리가 원하는 1을 기준으로 하여 DB단에서 페이징할 수 없다
    
    그래서 메모리단에서 페이징하는데 이는 매우 위험한 상황이 초래된다
    
    1:1 관계는 row 수가 증가하지 않기 때문에 상관없다
    

* **N+1 문제 해결법**

    1. 우선 1:1 관계 (XToOne)는 모두 fetch join한다
    2. 1:다 관계 (XtoMany)는 지연로딩으로 조회한다
    3. batch_size를 이용해서 프록시 객체(지연로딩)는 where절의 in 쿼리로 가져온다

    글로벌 설정: hibernate.default_batch_fetch_size
    
    엔티티 설정:@BatchSize


## 폼 객체(DTO) vs 엔티티 직접이용
    MVC에서 model을 이용할 때 폼객체를 쓰느냐 엔티티를 직접 쓰느냐의 문제에서 가장 중요한 문제는
    View의 Validation 때문에 엔티티를 지저분하게 만들면 안 된다는 것이다
    JPA에서 객체지향을 가장 잘 유지하고 나중에 유지보수를 깔끔하게 하기 위해서는
    엔티티를 가장 순수한 형태로 핵심 비즈니스 로직만 지닌 채로 보존하는 것이 제일 중요하다
    그래야만 엔티티를 여러 곳에서 다양하게 가져다 쓸 수 있기 때문이다
    Simple is the best
    따라서 오직 View 처리만을 하기 위한 Form 객체 같은 DTO(Data Transfer Object)를 따로 만들어서 쓰는 것이 좋다

## OSIV (Open Session In View) - Hibernate
## OEIV (Open EntityManager In View) - JPA

1. spring.jpa.open-in-view : true -> default
    기본적으로 영속성 컨텍스트는 실제로 트랜젝션을 시작할 때 DB와 커넥션을 시작한다
    open-in-view 설정값이 켜져있으면 영속성 컨텍스트는 실제로 API 반환되거나 View가 화면에 렌더링 될 때까지
    DB 커넥션을 놓지 않고 가지고 있는다
    그렇기 때문에 우리는 지연 로딩을 사용할 수 있는 것이다. 지연 로딩은 영속성 컨텍스트가 살아있어야 하기 때문인다.

    하지만 이 전략은 너무 오랫동안 DB 커넥션 리소스를 사용한다는 단점도 있다.
    만약 컨트롤러가 외부 API를 호출한다면 영속성 컨텍스트는 자기 할 일이 다 끝났음에도
    외부 API의 응답이 오기 전까지 DB 커넥션을 유지하고 있어야 한다. 이는 실시간 트래픽이 중요한 앱에서 장애를 일으킬 수 있다

2. spring.jpa.open-in-view : false

    이 설정값을 끄면 트랜젝션이 끝나자마자 커넥션을 반환하고 영속성 컨텍스트를 닫는다
    그렇기 때문에 OSIV를 끄면 모든 지연로딩을 트랜잭션 안에서 처리해야 한다
    따라서 지금까지 작성한 많은 지연 로딩 코드를 트랜잭션 안으로 넣어야 하는 단점이 있다
    그리고 view template에서 지연로딩이 동작하지 않는다
    결론적으로 트랜잭션이 끝나기 전에 지연 로딩을 강제로 호출해 두어야 한다

### 해결법
1. QueryService 하나를 만들어서 조회 트랜젝션을 생성하고 지연 로딩 초기화 수행
2. 트랜젝션 내에서 모든 지연로딩 초기화 후 반환


OrderService | OrderQueryService
핵심 비즈니스와 화면 조회용, API 반환용을 따로 분리하는 것이 유지보수가 훨씬 좋다
핵심 비즈니스는 라이프사이클이 길지만 화면은 자주 바뀌기 때문에 라이프사이클이 짧다

고객 서비스의 실시간 API는 OSIV를 끄고
ADMIN 처럼 커넥션을 많이 사용하지 않는 곳에서는 OSIV를 켠다


```python

```
