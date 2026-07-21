package github.sangwook.ecommerce.catalog.api

data class CategoryListResponse(
    val list: List<CategoryResponse>
)

data class CategoryResponse(
    val id: Long,
    val name: String,
    val children: List<CategoryResponse>
)
