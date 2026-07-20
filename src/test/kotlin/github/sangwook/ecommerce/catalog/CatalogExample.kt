package github.sangwook.ecommerce.catalog

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * 규칙 1. 상품은 최소 하나의 SKU를 가진다.
 * 규칙 2. SKU는 반드시 하나의 상품에 속한다.
 * 규칙 3. 한 상품 안에서 SKU의 옵션 이름은 중복될 수 없다.
 * 규칙 4. 상품도 SELLING 상태, SKU도 SELLING 상태여야 판매 가능하다.
 * 규칙 5. 상품 판매 상태 변경은 SKU의 판매 상태를 건드리지 않는다.
 * 규칙 6. 가격은 SKU가 소유한다.
 * 규칙 7. SKU 단종 시 삭제하지 않고 판매 상태를 변경한다.
 */
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
    val skus: List<Sku>
) {
    fun isDisplayable() = status == SaleStatus.SELLING

    fun isSellable(skuId: Long): Boolean {
        if (!isDisplayable()) return false
        return findSku(skuId)?.isSellable() ?: false
    }

    fun sellableSkus(): List<Sku> = if (isDisplayable()) skus.filter { it.isSellable() } else emptyList()

    fun lowestPrice(): Money? = sellableSkus().minByOrNull { it.price.amount }?.price

    fun stopSelling() { status = SaleStatus.STOPPED }

    private fun findSku(skuId: Long) = skus.find { it.id == skuId }
}

class Sku( //Stock Keeping Unit - 최소 분류 단위 (동일한 상품이라도 색상, 사이즈, 규격, 포장단위가 다르면 각각 별개의 SKU로 간주), 재고 단위의 관리를 위함
    val id: Long,
    val optionName: String,
    var price: Money,
    var status: SaleStatus //sku 에서도 판매 상태를 검토 - 재고 관리 단위가 SKU이기 때문.
) {
    fun isSellable() = status == SaleStatus.SELLING
    fun stopSelling() { status = SaleStatus.STOPPED }
}

//============================================= Domain End

//fun assembleTree(
//    rootId: Long,
//    categories: List<CategoryEntity>,
//    closures: List<CategoryClosure>
//): Category {
//    val byId = categories.associateBy { it.id }
//    val childrenOf = closures
//        .filter { it.depth == 1 }
//        .groupBy({ it.ancestor }, { it.descendant })
//
//    fun build(id: Long): Category {
//        val entity = byId.getValue(id)
//        return Category(
//            id = entity.id,
//            name = entity.name,
//            children = childrenOf[id].orEmpty().map { build(it) }
//        )
//    }
//    return build(rootId)
//}

class CatalogDomainTest {

    private fun product(
        productStatus: SaleStatus = SaleStatus.SELLING,
        skus: List<Sku> = listOf(
            Sku(1L, "검정 / M", Money(19000), SaleStatus.SELLING),
            Sku(2L, "검정 / L", Money(19000), SaleStatus.SELLING),
            Sku(3L, "흰색 / XL", Money(21000), SaleStatus.STOPPED)
        )
    ) = Product(100L, "반팔 티셔츠", "면 100%", 3L, productStatus, skus)

    // ===== 핵심 규칙: Product와 SKU 둘 다 SELLING이어야 판매 가능 =====

    @Test
    fun `Product와 SKU 모두 판매중이면 구매 가능하다`() {
        val product = product()

        assertTrue(product.isSellable(1L))
    }

    @Test
    fun `SKU가 중지면 Product가 판매중이어도 구매 불가`() {
        val product = product()

        assertFalse(product.isSellable(3L))   // 흰색 XL은 STOPPED
    }

    @Test
    fun `Product가 중지면 SKU가 판매중이어도 구매 불가`() {
        val product = product(productStatus = SaleStatus.STOPPED)

        assertFalse(product.isSellable(1L))   // SKU는 SELLING인데도
    }

    @Test
    fun `없는 SKU는 구매 불가`() {
        val product = product()

        assertFalse(product.isSellable(999L))
    }

    // ===== Product 상태가 SKU를 덮어쓰지 않는다 =====

    @Test
    fun `Product를 중지했다가 재개하면 SKU는 원래 상태를 유지한다`() {
        val product = product()

        product.stopSelling()
        assertTrue(product.sellableSkus().isEmpty())   // 전부 안 보임

        product.status = SaleStatus.SELLING            // 재개

        // 검정 M, L은 살아나고 흰색 XL은 여전히 단종
        assertEquals(listOf(1L, 2L), product.sellableSkus().map { it.id })
    }

    // ===== 판매 가능한 SKU만 노출 =====

    @Test
    fun `판매중지된 SKU는 목록에서 빠진다`() {
        val product = product()

        val sellable = product.sellableSkus()

        assertEquals(2, sellable.size)
        assertEquals(listOf("검정 / M", "검정 / L"), sellable.map { it.optionName })
    }

    @Test
    fun `Product가 중지면 SKU 목록이 비어있다`() {
        val product = product(productStatus = SaleStatus.STOPPED)

        assertTrue(product.sellableSkus().isEmpty())
    }

    // ===== 최저가 =====

    @Test
    fun `최저가는 판매중인 SKU 중에서 계산한다`() {
        val product = product(
            skus = listOf(
                Sku(1L, "검정 / M", Money(19000), SaleStatus.SELLING),
                Sku(2L, "한정판 / L", Money(9000), SaleStatus.STOPPED)   // 더 싸지만 중지
            )
        )

        assertEquals(Money(19000), product.lowestPrice())   // 9000원이 아님
    }

    @Test
    fun `판매 가능한 SKU가 없으면 최저가도 없다`() {
        val product = product(
            skus = listOf(Sku(1L, "검정 / M", Money(19000), SaleStatus.STOPPED))
        )

        assertNull(product.lowestPrice())
    }

    // ===== 애그리게이트 경계 =====

    @Test
    fun `SKU는 Product를 통해서만 판단된다`() {
        val product = product()

        // product.findSku(1L)  ← private. 밖에서 SKU를 꺼낼 수 없음
        // SKU를 직접 꺼내 상태를 바꾸면 Product의 규칙을 우회하게 됨

        // 판단은 항상 Product에게 묻는다
        assertTrue(product.isSellable(1L))
        assertFalse(product.isSellable(3L))
    }

    @Test
    fun `SKU 하나를 중지해도 다른 SKU는 영향 없다`() {
        val skus = listOf(
            Sku(1L, "검정 / M", Money(19000), SaleStatus.SELLING),
            Sku(2L, "검정 / L", Money(19000), SaleStatus.SELLING)
        )
        val product = product(skus = skus)

        skus[0].stopSelling()   // 검정 M만 단종

        assertFalse(product.isSellable(1L))
        assertTrue(product.isSellable(2L))
        assertEquals(1, product.sellableSkus().size)
    }
}