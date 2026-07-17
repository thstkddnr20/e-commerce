package github.sangwook.ecommerce.catalog

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

//============================================= Entity

class ProductEntity(
    val id: Long,
    var name: String,
    var description: String,
    var category: Long,
    var status: SaleStatus
)

class SkuEntity(
    val id: Long,
    val catalogId: Long,
    var optionName: String,
    var price: Int
)

class CategoryEntity(
    val id: Long,
    var name: String
)

class CategoryClosure(
    val ancestor: Long,
    val descendant: Long,
    val depth: Int
)


//============================================= Entity End
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
    var status: SaleStatus
) {
    fun isSellable() = status == SaleStatus.SELLING
    fun stopSelling() { status = SaleStatus.STOPPED }
}

class Sku( //Stock Keeping Unit - 최소 분류 단위 (동일한 상품이라도 색상, 사이즈, 규격, 포장단위가 다르면 각각 별개의 SKU로 간주), 재고 단위의 관리를 위함
    val id: Long,
    val productId: Long,
    val optionName: String,
    var price: Money
)

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

    // ===== Product =====

    @Test
    fun `판매중인 상품은 판매 가능하다`() {
        val product = Product(1L, "반팔 티셔츠", "면 100%", 3L, SaleStatus.SELLING)

        assertTrue(product.isSellable())
    }

    @Test
    fun `판매중지하면 판매 불가능해진다`() {
        val product = Product(1L, "반팔 티셔츠", "면 100%", 3L, SaleStatus.SELLING)

        product.stopSelling()

        assertFalse(product.isSellable())
        assertEquals(SaleStatus.STOPPED, product.status)
    }

    @Test
    fun `상품은 재고를 모른다`() {
        val product = Product(1L, "반팔 티셔츠", "면 100%", 3L, SaleStatus.SELLING)

        // product.quantity  ← 컴파일 안 됨. 재고는 Inventory 도메인 소유
        // 상품이 아는 건 "무엇을 파는가"뿐
        assertEquals(3L, product.categoryId)
    }

    // ===== Sku =====

    @Test
    fun `하나의 상품은 여러 SKU를 가진다`() {
        val product = Product(100L, "반팔 티셔츠", "면 100%", 3L, SaleStatus.SELLING)

        val skus = listOf(
            Sku(1L, product.id, "검정 / M", Money(19000)),
            Sku(2L, product.id, "검정 / L", Money(19000)),
            Sku(3L, product.id, "흰색 / M", Money(21000))
        )

        assertEquals(3, skus.size)
        assertTrue(skus.all { it.productId == product.id })
    }

    @Test
    fun `SKU마다 가격이 다를 수 있다`() {
        val skus = listOf(
            Sku(1L, 100L, "검정 / M", Money(19000)),
            Sku(2L, 100L, "흰색 / XL", Money(21000))
        )

        // 상품 목록에 보여줄 최저가
        val lowestPrice = skus.minOf { it.price.amount }
        assertEquals(19000, lowestPrice)
    }

    @Test
    fun `Product에는 가격이 없다 - 가격은 SKU가 소유`() {
        val product = Product(1L, "반팔 티셔츠", "면 100%", 3L, SaleStatus.SELLING)

        // product.price  ← 컴파일 안 됨
        // "티셔츠 19000원"이 아니라 "검정 M 티셔츠 19000원"이 진짜 판매 단위
        val sku = Sku(1L, product.id, "검정 / M", Money(19000))
        assertEquals(Money(19000), sku.price)
    }

    // ===== Category =====

    @Test
    fun `카테고리는 계층 구조를 드러내지 않는다`() {
        val category = Category(3L, "티셔츠")

        // category.children  ← 없음
        // category.parent    ← 없음
        // 계층은 DB(클로저 테이블)에 있고, 리포지토리에게 물어본다
        assertEquals("티셔츠", category.name)
    }

    // ===== 도메인 경계 확인 =====

    @Test
    fun `상품 상세 조합 - 카탈로그만으로 구성`() {
        val product = Product(100L, "반팔 티셔츠", "면 100%", 3L, SaleStatus.SELLING)
        val category = Category(3L, "티셔츠")
        val skus = listOf(
            Sku(1L, 100L, "검정 / M", Money(19000)),
            Sku(2L, 100L, "검정 / L", Money(19000))
        )

        // 화면에 필요한 것: 상품명 + 카테고리 + 옵션 목록 + 최저가
        assertEquals("반팔 티셔츠", product.name)
        assertEquals("티셔츠", category.name)
        assertEquals(listOf("검정 / M", "검정 / L"), skus.map { it.optionName })
        assertEquals(19000, skus.minOf { it.price.amount })

        // 재고("품절 여부")는 여기 없음 → 재고 포트로 별도 조회 후 상위에서 조합
    }
}