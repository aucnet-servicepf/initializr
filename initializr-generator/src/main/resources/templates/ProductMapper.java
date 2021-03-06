package ${packageName}.integration.repository;


import jp.co.aucnet.common.business.repository.BaseMapper;
import ${packageName}.business.domain.Product;
import ${packageName}.business.domain.ProductKey;
import ${packageName}.business.domain.ProductExample;

public interface ProductMapper extends BaseMapper<ProductKey, Product, ProductExample> {
}