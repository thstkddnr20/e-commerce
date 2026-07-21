package github.sangwook.ecommerce.admin

import github.sangwook.ecommerce.Money
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
class AdminProductService(
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
        if (!productRepository.existsById(productId)) throw IllegalArgumentException("상품을 찾을 수 없습니다.")
        if (skuRepository.existByProductIdAndOptionName(productId, optionName)) throw IllegalArgumentException("이미 존재하는 옵션입니다.")
        val sku = Sku(productId = productId, optionName = optionName, price = Money(price), status = SaleStatus.SELLING)
        skuRepository.save(sku)
    }

    @Transactional
    fun changeProductStatus(productId: Long, status: SaleStatus) {
        val product = productRepository.findByIdOrNull(productId) ?: throw IllegalArgumentException("상품을 찾을 수 없습니다.")
        if (status == SaleStatus.SELLING) {
            val sellingSkuCount = skuRepository.countByProductIdAndStatus(productId, SaleStatus.SELLING)
            if (sellingSkuCount == 0) throw IllegalArgumentException("판매 가능한 SKU가 없습니다.")
        }
        product.changeStatus(status)
        productRepository.save(product)
    }

    private fun isLeaf(categoryId: Long): Boolean {
        return categoryRepository.countDescendants(categoryId) == 0
    }
}