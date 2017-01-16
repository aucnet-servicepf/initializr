package ${packageName}.business.service;

import ${packageName}.business.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    Product findProductByName(String name);

    Product findById(Long id);

    Page<Product> pageByName(String name, Pageable page);

    boolean create(Product product);

    boolean update(Product product);

    boolean delete(Product product);

}
