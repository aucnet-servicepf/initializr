package ${packageName}.business.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import jp.co.aucnet.common.exception.BusinessException;
import ${packageName}.business.domain.Product;
import ${packageName}.business.domain.ProductKey;
import ${packageName}.business.domain.ProductExample;
import ${packageName}.business.repository.ProductRepository;


@Service
public class ProductServiceImpl implements ProductService {

<% if(hasMybatis) { %>
@Autowired
private ProductRepository productRepository;

@Override
public Product findProductByName(String name) {

    ProductExample condition = new ProductExample();
    condition.createCriteria().andNameEqualTo(name);

    if(name.equals("404")){
        throw new BusinessException("not good");
    }

    return productRepository.findOneBy(condition);
}

@Override
public Product findById(Long id) {
    return productRepository.findOneBy(ProductKey.create(id));
}

@Override
public Page<Product> pageByName(String name, Pageable pageable){
    ProductExample condition = new ProductExample();
    condition.createCriteria().andNameLike(name);

    return productRepository.findPageBy(condition, pageable);
}

@Override
public boolean create(Product p) {

    return productRepository.create(p);
}

@Override
public boolean update(Product p) {

    return productRepository.update(p);
}
@Override
public boolean delete(Product p) {
  return  productRepository.delete(p);
}

<% } else {%>
    @Override
    public Product findProductByName(String name) {
        return null;
    }
<%}%>

}