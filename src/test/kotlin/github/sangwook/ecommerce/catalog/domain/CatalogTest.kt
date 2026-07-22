package github.sangwook.ecommerce.catalog.domain

import github.sangwook.ecommerce.Money
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CatalogTest {

    @Test
    fun `상품 판매 상태 변경 시 DRAFT 상태로는 역행할 수 없다`(){
        val product = Product(1L, 4L, "헤드셋", "설명", SaleStatus.SELLING)
        assertThrows<IllegalArgumentException> {
            product.changeStatus(SaleStatus.DRAFT)
        }
    }

    @Test
    fun `Sku 판매 상태 변경 시 판매중, 판매중지만 지정할 수 있다`() {
        val sku = Sku(1L, 1L, "옵션이름", Money(1000), SaleStatus.SELLING)
        assertThrows<IllegalArgumentException> {
            sku.changeStatus(SaleStatus.DRAFT)
        }
    }

}