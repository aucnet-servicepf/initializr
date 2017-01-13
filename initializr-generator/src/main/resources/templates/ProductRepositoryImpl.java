package ${packageName}.integration.repository;

import ${packageName}.integration.repository.ProductMapper;
import ${packageName}.business.repository.ProductRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepositoryImpl implements ProductRepository {
    private ProductMapper productMapper;

    public ProductRepositoryImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public ProductMapper getMapper() {
        return this.productMapper;
    }
}