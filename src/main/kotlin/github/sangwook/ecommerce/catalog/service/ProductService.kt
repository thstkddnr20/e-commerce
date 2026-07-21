package github.sangwook.ecommerce.catalog.service

import github.sangwook.ecommerce.Money
import github.sangwook.ecommerce.catalog.api.ProductDetailResponse
import github.sangwook.ecommerce.catalog.api.ProductSummaryResponse
import github.sangwook.ecommerce.catalog.api.SkuDetailResponse
import github.sangwook.ecommerce.catalog.domain.Product
import github.sangwook.ecommerce.catalog.domain.SaleStatus
import github.sangwook.ecommerce.catalog.domain.Sku
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

    @Transactional
    fun registerProduct(categoryId: Long, name: String, description: String) {
        if (!categoryRepository.existsById(categoryId)) throw IllegalArgumentException("카테고리를 찾을 수 없습니다.")
        if (!isLeaf(categoryId)) throw IllegalArgumentException("상품은 최하위 카테고리에만 등록할 수 있습니다.")
        val product = Product(categoryId = categoryId, name = name, description = description, status = SaleStatus.DRAFT)
        productRepository.save(product)
    }

    @Transactional
    fun registerSku(productId: Long, optionName: String, price: Int) {
        productRepository.findByIdOrNull(productId) ?: throw IllegalArgumentException("상품을 찾을 수 없습니다.")
        if (skuRepository.existByProductIdAndOptionName(productId, optionName)) throw IllegalArgumentException("이미 존재하는 옵션입니다.")
        val sku = Sku(productId = productId, optionName = optionName, price = Money(price), status = SaleStatus.SELLING)
        skuRepository.save(sku)
    }

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

    private fun isLeaf(categoryId: Long): Boolean {
        return categoryRepository.countDescendants(categoryId) == 0
    }
}