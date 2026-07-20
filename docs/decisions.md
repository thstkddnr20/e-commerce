# 코드 작성에 대한 결정 이유를 적는 문서

- Entity의 protected set (7/14)
  - 코틀린 문법 공부 필요 - 일단 서칭 결과 이렇게 사용하는게 가장 지향적인 패턴이라고 하여 적용 - https://jhzlo.tistory.com/73 참고하면 좋을듯

- Member Entity와 Address Entity의 분리 (7/15)
  - Member에는 이메일, 암호화된 비밀번호, 이름과 같이 한번 지정되면 자주 바뀌지 않는 데이터가 존재한다.
  - 반면에 Address Entity는 새로 추가되거나, 바뀌기 쉬운 데이터들이 존재하므로 이를 별도의 엔티티로 분리하여 관리한다.

- MemberService에서 바로 Entity를 사용하는 패턴 (7/15)
  - 유저의 도메인 규칙이 현재로서는 복잡하지 않다고 판단하여 도메인 계층을 추가하여 관리하는 것이 아닌 Entity를 그대로 Service에서 사용한다.

- 도메인간의 협력이 필요한 부분에서 UseCase를 사용 (7/17)
  - 하나의 도메인만을 사용하는 부분에서는 Controller가 Service를 직접 참조한다.
  - 여러 도메인을 사용하는 부분에서는 Service의 상위 계층인 UseCase를 만들고 Service를 orchestrate 한다.
  - UseCase에는 흐름을 직관적으로 유추할 수 있는 이름을 지어준다. ex) PlaceOrderUseCase

- JPA -> Spring Data JDBC 사용과 도메인과 엔티티 겸용 (7/20)
  - JPA에서 Spring Data JDBC로 변경되면서 JPA 관련 어노테이션들이 없어졌다.
  - 클래스가 완전히 순수해졌다고 할 순 없지만 JPA가 제공하는 강력한 기능의 제약조건에 비해서는 조금 더 제약에서 벗어났다고 할 수 있을것 같다.
  - 또한 @MappedCollection을 이용하여 Order <-> OrderItem 처럼 Order에 종속적인 OrderItem의 관계를 Aggregate 단위로 나타낼 수 있다.
    - 단점은 영속성 컨텍스트와 같은 개념이 없기 때문에 이전 상태를 모르므로 객체 변경시 aggregate 관련 튜플을 모두 삭제하고 다시 insert 한다는 점.
  - 이 시점에서 Domain과 Entity를 겸용해서 사용한다면 어떨까?

- 도메인, 엔티티 겸용에 따른 도메인 클래스 생성 규칙 (7/20)
  ```kotlin
  @Table(name = "product")
  class Product(
  categoryId: Long,
  name: String,
  description: String,
  status: SaleStatus
  ) {
  
    @Id
    @Column("product_id")
    val id: Long? = null

    @Column("category_id")
    val categoryId: Long = categoryId

    @Column("name")
    var name: String = name
        private set

    @Column("description")
    var description: String = description
        private set

    @Column("status")
    var status: SaleStatus = status
        private set
  }
  ```
  1. data class가 아닌 class를 사용한다. (var + private set 사용 위함)
  2. 속성을 생성자에 선언하는 것이 아닌 클래스 내부에 선언한다.
  3. 테이블은 @Table 어노테이션으로 이름을 지정한다.
  4. 컬럼엔 모두 @Column 어노테이션으로 이름을 지정한다.
  5. 변경할 수 없는 데이터는 val로 선언한다. (기본키, 참조키)
  6. 변경할 수 있는 데이터는 var로 선언하고 private setter를 작성한다.