package ${packageName}.api.v1;

import ${packageName}.business.domain.Product;
import ${packageName}.business.service.ProductService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "v1")
public class ProductControllerV1 {

    @Autowired
    ProductService productService;

    @RequestMapping(value = "/products/{name}")
    public Product findProductByName(@PathVariable String name) {
        return productService.findProductByName(name);
    }

}