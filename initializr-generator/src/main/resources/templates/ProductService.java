package ${packageName}.business.service;

import ${packageName}.business.domain.Product;

public interface ProductService {

    Product findProductByName(String name);

    Product findById(int id);
}
