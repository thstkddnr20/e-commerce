package github.sangwook.ecommerce.catalog.service

import github.sangwook.ecommerce.catalog.api.ProductDetailResponse
import github.sangwook.ecommerce.catalog.api.ProductSummaryResponse
import github.sangwook.ecommerce.catalog.api.SkuDetailResponse
import github.sangwook.ecommerce.catalog.domain.SaleStatus
import github.sangwook.ecommerce.catalog.infrastructure.CategoryRepository
import github.sangwook.ecommerce.catalog.infrastructure.ProductRepository
import github.sangwook.ecommerce.catalog.infrastructure.SkuRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository,
    private val skuRepository: SkuRepository
) {

    @Transactional(readOnly = true)
    fun getProductDetail(productId: Long): ProductDetailResponse {
        val product = productRepository.findByIdOrNull(productId)?.takeIf { it.status == SaleStatus.SELLING } ?: throw IllegalArgumentException("상품을 찾을 수 없습니다.")
        val skus = skuRepository.findByProductId(productId).filter { it.status == SaleStatus.SELLING }
        return ProductDetailResponse(
            product.id(),
            product.categoryId,
            product.name,
            product.description,
            product.status,
            skus.map { s -> SkuDetailResponse(s.id(), s.optionName, s.price.amount, s.status) }
        )
    }

    @Transactional(readOnly = true)
    fun getProductList(categoryId: Long): List<ProductSummaryResponse> {
        return productRepository.findSellableSummaries(categoryId)
    }
}