[Product 생명주기]
1. 상품 최초 생성 시 DRAFT 상태가 된다.
2. SKU가 최소 하나 있으면 SELLING으로 전환 가능하다.
   2-1. SELLING <-> STOPPED 전환 가능, DRAFT로는 역행 불가.
   2-2. DRAFT 상품은 구매자에게 노출되지 않는다.

[Product-SKU 관계]
3. SKU는 반드시 하나의 상품에 속한다 (productId 필수).
4. 한 상품 안에서 optionName은 중복 불가 (앱 검증 + DB unique).
   4-1. 옵션 없는 상품도 SKU를 가진다 (optionName="기본" 등).

[판매 판정]
5. 상품 SELLING && SKU SELLING이어야 판매 가능 (주문 유스케이스에서 조합 검증).
6. 상품 상태 변경은 SKU 상태를 건드리지 않는다.

[가격/생명주기]
7. 가격은 SKU가 소유한다 (Product엔 가격 없음).
8. 판매 이력 있는 SKU는 삭제 불가, STOPPED로만. (DRAFT 상품 SKU는 삭제 가능)

[카테고리]
9. 상품은 최하위 카테고리에만 속한다.