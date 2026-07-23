package github.sangwook.ecommerce.catalog.service

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ProductServiceTest {

    @Nested
    inner class getProductDetail_상품_상세_조회 {
        @Test fun `상품이 없으면 예외가 발생한다`() {

        }
        @Test fun `DRAFT 상품은 조회할 수 없다`() {

        }
        @Test fun `STOPPED 상품은 조회할 수 없다`() {

        }
        @Test fun `판매중인 상품은 SKU와 함께 조회된다`() {

        }
        @Test
        fun `STOPPED SKU는 옵션에서 제외된다`() {

        }
    }
}