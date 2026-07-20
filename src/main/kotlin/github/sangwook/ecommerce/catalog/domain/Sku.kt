package github.sangwook.ecommerce.catalog.domain

import github.sangwook.ecommerce.Money
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("sku")
class Sku(
    productId: Long,
    optionName: String,
    price: Money,
    status: SaleStatus,
) {
    @Id
    @Column("sku_id")
    val id: Long? = null

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
}