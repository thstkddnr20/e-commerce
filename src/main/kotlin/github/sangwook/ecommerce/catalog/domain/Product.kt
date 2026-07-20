package github.sangwook.ecommerce.catalog.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("product")
class Product(
    categoryId: Long,
    name: String,
    description: String,
    status: SaleStatus
) {

    @Id
    @Column("product_id")
    val id: Long? = null

    @Column("category_id")
    val categoryId: Long = categoryId

    @Column("name")
    var name: String = name
        private set

    @Column("description")
    var description: String = description
        private set

    @Column("status")
    var status: SaleStatus = status
        private set
}