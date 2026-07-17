# 코드 작성에 대한 결정 이유를 적는 문서

- Entity의 protected set (7/14)
  - 코틀린 문법 공부 필요 - 일단 서칭 결과 이렇게 사용하는게 가장 지향적인 패턴이라고 하여 적용 - https://jhzlo.tistory.com/73 참고하면 좋을듯

- Member Entity와 Address Entity의 분리 (7/15)
  - Member에는 이메일, 암호화된 비밀번호, 이름과 같이 한번 지정되면 자주 바뀌지 않는 데이터가 존재한다.
  - 반면에 Address Entity는 새로 추가되거나, 바뀌기 쉬운 데이터들이 존재하므로 이를 별도의 엔티티로 분리하여 관리한다.

- MemberService에서 바로 Entity를 사용하는 패턴 (7/15)
  - 유저의 도메인 규칙이 현재로서는 복잡하지 않다고 판단하여 도메인 계층을 추가하여 관리하는 것이 아닌 Entity를 그대로 Service에서 사용한다. 