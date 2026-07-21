package github.sangwook.ecommerce.catalog.api

import github.sangwook.ecommerce.catalog.domain.SaleStatus

data class ProductDetailResponse(
    val productId: Long,
    val categoryId: Long,
    val name: String,
    val description: String,
    val status: SaleStatus,
    val skus: List<SkuDetailResponse>
)

data class SkuDetailResponse(
    val skuId: Long,
    val optionName: String,
    val price: Int,
    val status: SaleStatus,
)
