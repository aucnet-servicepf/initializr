package ${packageName}.business.repository;

import jp.co.aucnet.common.business.repository.BaseRepository;
import ${packageName}.business.domain.Product;
import ${packageName}.business.domain.ProductKey;
import ${packageName}.business.domain.ProductExample;

public interface ProductRepository extends BaseRepository<ProductKey, Product, ProductExample> {
}