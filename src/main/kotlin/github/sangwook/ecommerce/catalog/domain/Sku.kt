package github.sangwook.ecommerce.catalog.domain

import github.sangwook.ecommerce.Money
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("sku")
class Sku(
    id: Long? = null,
    productId: Long,
    optionName: String,
    price: Money,
    status: SaleStatus,
) {
    @Id
    @Column("id")
    private val id: Long? = id

    @Column("product_id")
    val productId: Long = productId

    @Column("option_name")
    val optionName: String = optionName

    @Column("price")
    var price: Money = price
        private set

    @Column("status")
    var status: SaleStatus = status
        private set

    fun id(): Long {
        return requireNotNull(id) {
            "저장되지 않은 Sku의 ID에는 접근할 수 없습니다."
        }
    }

    fun changeStatus(newStatus: SaleStatus) {
        require(newStatus == SaleStatus.SELLING || newStatus == SaleStatus.STOPPED) {
            "SKU는 판매중/중지만 가능합니다."
        }
        this.status = newStatus
    }
}