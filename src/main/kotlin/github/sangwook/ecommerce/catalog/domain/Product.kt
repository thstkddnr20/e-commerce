package github.sangwook.ecommerce.catalog.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("product")
class Product(
    id : Long? = null,
    categoryId: Long,
    name: String,
    description: String,
    status: SaleStatus
) {

    @Id
    @Column("product_id")
    private val id: Long? = id

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

    fun id(): Long {
        return requireNotNull(id) {
            "저장되지 않은 Product의 ID에는 접근할 수 없습니다."
        }
    }
}