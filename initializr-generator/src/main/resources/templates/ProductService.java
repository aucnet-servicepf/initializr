package ${packageName}.business.service;

import jp.co.aucnet.business.domain.Product;

public interface ProductService {

    Product findProductByName(String name);
}
