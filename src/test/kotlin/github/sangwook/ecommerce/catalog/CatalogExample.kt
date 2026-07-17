package github.sangwook.ecommerce.catalog

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

//============================================= Entity

class CatalogEntity(
    val id: Long,
    var name: String,
    var description: String,
    var price: Int,
    var category: Long,
    var status: SaleStatus
)

class CategoryEntity(
    val id: Long,
    var name: String
)

class CategoryClosure(
    var ancestor: Long,
    var descendant: Long,
    var depth: Int
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
    var price: Money,
    val categoryId: Long,
    var status: SaleStatus
) {
    fun isSellable() = status == SaleStatus.SELLING
    fun stopSelling() { status = SaleStatus.STOPPED }
}

//============================================= Domain End

fun CatalogEntity.toDomain() = Product(
    id = id,
    name = name,
    description = description,
    price = Money(price),
    categoryId = category,
    status = status
)

fun Product.toEntity() = CatalogEntity(
    id = id,
    name = name,
    description = description,
    price = price.amount,
    category = categoryId,
    status = status
)

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

class CatalogTest {

    @Test
    fun `엔티티를 도메인으로 변환하면 price가 Money로 감싸진다`() {
        val entity = CatalogEntity(
            id = 1L,
            name = "흰 반팔티",
            description = "면 100%",
            price = 19000,
            category = 3L,
            status = SaleStatus.SELLING
        )

        val product = entity.toDomain()

        assertEquals(Money(19000), product.price)
        assertEquals(3L, product.categoryId)
    }

    @Test
    fun `도메인을 엔티티로 변환하면 Money가 Int로 풀린다`() {
        val product = Product(
            id = 1L,
            name = "흰 반팔티",
            description = "면 100%",
            price = Money(19000),
            categoryId = 3L,
            status = SaleStatus.SELLING
        )

        val entity = product.toEntity()

        assertEquals(19000, entity.price)
    }

    @Test
    fun `Money는 값이 같으면 같다`() {
        assertEquals(Money(1000), Money(1000))
        assertTrue(Money(1000) == Money(1000))
    }

    @Test
    fun `Money 연산`() {
        assertEquals(Money(3000), Money(1000) + Money(2000))
        assertEquals(Money(6000), Money(2000) * 3)
        assertEquals(Money(500), Money(1000) - Money(500))
        assertEquals(Money(0), Money(1000) - Money(1000))   // 0원 허용
    }

    @Test
    fun `음수 금액은 만들 수 없다`() {
        assertThrows<IllegalArgumentException> { Money(-1) }
        assertThrows<IllegalArgumentException> { Money(500) - Money(1000) }
    }

    @Test
    fun `판매중지된 상품은 판매 불가`() {
        val product = Product(1L, "티셔츠", "설명", Money(19000), 3L, SaleStatus.SELLING)

        assertTrue(product.isSellable())

        product.stopSelling()

        assertFalse(product.isSellable())
    }

//    @Test
//    fun `클로저 테이블로 카테고리 트리를 조립한다`() {
//        val categories = listOf(
//            CategoryEntity(1L, "의류"),
//            CategoryEntity(2L, "상의"),
//            CategoryEntity(3L, "티셔츠"),
//            CategoryEntity(4L, "셔츠"),
//            CategoryEntity(5L, "하의"),
//            CategoryEntity(6L, "청바지")
//        )
//        val closures = listOf(
//            CategoryClosure(1L, 1L, 0), CategoryClosure(1L, 2L, 1),
//            CategoryClosure(1L, 3L, 2), CategoryClosure(1L, 4L, 2),
//            CategoryClosure(1L, 5L, 1), CategoryClosure(1L, 6L, 2),
//            CategoryClosure(2L, 2L, 0), CategoryClosure(2L, 3L, 1),
//            CategoryClosure(2L, 4L, 1),
//            CategoryClosure(3L, 3L, 0), CategoryClosure(4L, 4L, 0),
//            CategoryClosure(5L, 5L, 0), CategoryClosure(5L, 6L, 1),
//            CategoryClosure(6L, 6L, 0)
//        )
//
//        val tree = assembleTree(1L, categories, closures)
//
//        assertEquals("의류", tree.name)
//        assertEquals(2, tree.children.size)                    // 상의, 하의
//        assertEquals("상의", tree.children[0].name)
//        assertEquals(2, tree.children[0].children.size)        // 티셔츠, 셔츠
//        assertEquals("티셔츠", tree.children[0].children[0].name)
//        assertEquals(0, tree.children[0].children[0].children.size)  // 잎사귀
//    }

    @Test
    fun `상의 아래 상품을 모두 조회한다 - IN절 흉내`() {
        val closures = listOf(
            CategoryClosure(2L, 2L, 0),
            CategoryClosure(2L, 3L, 1),
            CategoryClosure(2L, 4L, 1)
        )
        val products = listOf(
            CatalogEntity(1L, "흰 반팔티", "", 19000, 3L, SaleStatus.SELLING),
            CatalogEntity(2L, "검정 셔츠", "", 39000, 4L, SaleStatus.SELLING),
            CatalogEntity(3L, "청바지", "", 59000, 6L, SaleStatus.SELLING)   // 하의 소속
        )

        // WHERE ancestor = 2 → descendant 목록
        val descendantIds = closures.filter { it.ancestor == 2L }.map { it.descendant }

        // WHERE category_id IN (...)
        val found = products.filter { it.category in descendantIds }.map { it.toDomain() }

        assertEquals(2, found.size)   // 청바지는 제외됨
        assertEquals(listOf("흰 반팔티", "검정 셔츠"), found.map { it.name })
    }
}