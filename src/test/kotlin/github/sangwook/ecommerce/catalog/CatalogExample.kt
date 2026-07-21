package github.sangwook.ecommerce.catalog

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

//============================================= Domain

class Category(
    val id: Long,
    var name: String
    //도메인 레벨에서는 트리구조를 드러내지 않는다.
)

enum class SaleStatus {
    SELLING,
    STOPPED,
}

@JvmInline
value class Money(val amount: Int) {
    init {
        require(amount >= 0) { "금액은 음수일 수 없습니다." }
    }
    operator fun plus(other: Money) = Money(amount + other.amount)
    operator fun times(quantity: Int) = Money(amount * quantity)
    operator fun minus(other: Money) = Money(amount - other.amount)
}

class Product(
    val id: Long,
    var name: String,
    var description: String,
    val categoryId: Long,
    var status: SaleStatus,
) {
    fun isDisplayable() = status == SaleStatus.SELLING

    fun stopSelling() { status = SaleStatus.STOPPED }
}

class Sku( //Stock Keeping Unit - 최소 분류 단위 (동일한 상품이라도 색상, 사이즈, 규격, 포장단위가 다르면 각각 별개의 SKU로 간주), 재고 단위의 관리를 위함
    val id: Long,
    val productId: Long,
    val optionName: String,
    var price: Money,
    var status: SaleStatus //sku 에서도 판매 상태를 검토 - 재고 관리 단위가 SKU이기 때문.
) {
    fun isSellable() = status == SaleStatus.SELLING
    fun stopSelling() { status = SaleStatus.STOPPED }
}

//============================================= Domain End

class CatalogDomainTest {

    // ===== Product 단독 =====

    @Test
    fun `판매중인 상품은 노출 가능하다`() {
        val product = Product(1L, "반팔 티셔츠", "면 100%", 3L, SaleStatus.SELLING)

        assertTrue(product.isDisplayable())
    }

    @Test
    fun `판매중지하면 노출 불가능해진다`() {
        val product = Product(1L, "반팔 티셔츠", "면 100%", 3L, SaleStatus.SELLING)

        product.stopSelling()

        assertFalse(product.isDisplayable())
    }

    @Test
    fun `Product는 SKU를 모른다 - 가격도 없다`() {
        val product = Product(1L, "반팔 티셔츠", "면 100%", 3L, SaleStatus.SELLING)

        // product.skus   ← 없음. 분리 구조
        // product.price  ← 없음. 가격은 SKU 소유
        // Product는 "무엇을 노출할지"만 안다
        assertEquals(3L, product.categoryId)
    }

    // ===== Sku 단독 =====

    @Test
    fun `SKU는 자신이 어느 상품 것인지 productId로 안다`() {
        val sku = Sku(10L, productId = 1L, "검정 / M", Money(19000), SaleStatus.SELLING)

        // sku.product  ← 객체 참조는 없음 (방향 불필요)
        // 관계는 productId 값으로만 저장
        assertEquals(1L, sku.productId)
    }

    @Test
    fun `SKU 판매중지는 그 SKU만 바꾼다`() {
        val sku = Sku(10L, 1L, "검정 / M", Money(19000), SaleStatus.SELLING)

        sku.stopSelling()

        assertFalse(sku.isSellable())
        assertEquals(SaleStatus.STOPPED, sku.status)
    }

    // ===== 규칙 4: 판매 가능 = Product SELLING AND SKU SELLING (서비스가 조합) =====

    @Test
    fun `Product와 SKU 모두 판매중이어야 구매 가능하다`() {
        val product = Product(1L, "반팔 티셔츠", "면 100%", 3L, SaleStatus.SELLING)
        val sku = Sku(10L, 1L, "검정 / M", Money(19000), SaleStatus.SELLING)

        // 규칙 4를 서비스가 조합으로 검증 — 애그리게이트 없이
        val sellable = product.isDisplayable() && sku.isSellable()

        assertTrue(sellable)
    }

    @Test
    fun `SKU가 중지면 Product가 판매중이어도 구매 불가`() {
        val product = Product(1L, "반팔 티셔츠", "면 100%", 3L, SaleStatus.SELLING)
        val sku = Sku(10L, 1L, "검정 / M", Money(19000), SaleStatus.STOPPED)

        assertFalse(product.isDisplayable() && sku.isSellable())
    }

    @Test
    fun `Product가 중지면 SKU가 판매중이어도 구매 불가`() {
        val product = Product(1L, "반팔 티셔츠", "면 100%", 3L, SaleStatus.STOPPED)
        val sku = Sku(10L, 1L, "검정 / M", Money(19000), SaleStatus.SELLING)

        assertFalse(product.isDisplayable() && sku.isSellable())
    }

    // ===== 규칙 5: Product 상태 변경이 SKU를 안 건드린다 =====

    @Test
    fun `Product를 내렸다 올려도 SKU는 원래 상태를 유지한다`() {
        val product = Product(1L, "반팔 티셔츠", "면 100%", 3L, SaleStatus.SELLING)
        val skus = listOf(
            Sku(10L, 1L, "검정 / M", Money(19000), SaleStatus.SELLING),
            Sku(11L, 1L, "흰색 / XL", Money(21000), SaleStatus.STOPPED)   // 원래 단종
        )

        product.stopSelling()                    // 상품 전체 내림
        product.status = SaleStatus.SELLING      // 다시 올림

        // SKU들은 product가 안 건드렸으니 그대로
        assertEquals(SaleStatus.SELLING, skus[0].status)
        assertEquals(SaleStatus.STOPPED, skus[1].status)   // 여전히 단종
    }

    // ===== 규칙 6: 최저가는 판매중인 SKU 중에서 (서비스가 계산) =====

    @Test
    fun `최저가는 판매중인 SKU 중 최솟값이다`() {
        val skus = listOf(
            Sku(10L, 1L, "검정 / M", Money(19000), SaleStatus.SELLING),
            Sku(11L, 1L, "한정판 / L", Money(9000), SaleStatus.STOPPED),   // 더 싸지만 중지
            Sku(12L, 1L, "흰색 / XL", Money(21000), SaleStatus.SELLING)
        )

        val lowestPrice = skus
            .filter { it.isSellable() }
            .minOfOrNull { it.price.amount }

        assertEquals(19000, lowestPrice)   // 9000원이 아님 (중지된 SKU 제외)
    }

    // ===== 규칙 3: 한 상품 안에서 optionName 중복 불가 (서비스가 검증) =====

    @Test
    fun `같은 상품에 옵션명이 중복되면 안 된다`() {
        val skus = listOf(
            Sku(10L, 1L, "검정 / M", Money(19000), SaleStatus.SELLING),
            Sku(11L, 1L, "검정 / M", Money(21000), SaleStatus.SELLING)   // 중복!
        )

        val names = skus.map { it.optionName }
        val hasDuplicate = names.size != names.distinct().size

        assertTrue(hasDuplicate)   // 등록 시 이걸로 거부해야 함
    }

    // ===== Money 값 객체 =====

    @Test
    fun `Money는 값이 같으면 같다`() {
        assertEquals(Money(19000), Money(19000))
    }

    // ===== 도메인 경계 =====

    @Test
    fun `재고는 SKU를 skuId로만 참조한다`() {
        val sku = Sku(10L, 1L, "검정 / M", Money(19000), SaleStatus.SELLING)

        // 재고 도메인이 아는 것: skuId뿐
        val skuIdForInventory: Long = sku.id
        // Stock(skuId = 10L, quantity = 30) — 옵션명도 가격도 모름

        assertEquals(10L, skuIdForInventory)
    }

    @Test
    fun `상품 상세 조합 - 서로 다른 조회 결과를 서비스가 합친다`() {
        val product = Product(1L, "반팔 티셔츠", "면 100%", 3L, SaleStatus.SELLING)
        val skus = listOf(
            Sku(10L, 1L, "검정 / M", Money(19000), SaleStatus.SELLING),
            Sku(11L, 1L, "검정 / L", Money(19000), SaleStatus.SELLING)
        )

        // Product 조회 + SKU 조회(findByProductId)를 서비스가 조합
        assertTrue(skus.all { it.productId == product.id })
        assertEquals(listOf("검정 / M", "검정 / L"), skus.map { it.optionName })
        assertEquals(19000, skus.filter { it.isSellable() }.minOf { it.price.amount })
    }
}