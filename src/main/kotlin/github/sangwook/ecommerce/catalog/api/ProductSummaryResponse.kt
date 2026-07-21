package github.sangwook.ecommerce.catalog.api

data class ProductSummaryResponse(
    val id: Long,
    val name: String,
    val lowestPrice: Int
)
