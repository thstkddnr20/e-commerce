package github.sangwook.ecommerce.catalog.domain

class Product(
    val id: Long,
    var name: String,
    var description: String,
    val categoryId: Long,
    var status: SaleStatus,
    val skus: List<Sku>
)