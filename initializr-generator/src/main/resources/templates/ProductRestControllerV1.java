package ${packageName}.api.v1;


import ${packageName}.business.domain.Product;
import ${packageName}.business.service.ProductService;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/v1/products")
public class ProductRestControllerV1 {

    @Autowired
    ProductService productService;

    @Autowired
    private ProductServiceV1 productServiceV1;

    @GetMapping(value = "/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public Product findProductByName(@PathVariable String name) {
        return productService.findProductByName(name);
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Product findProductById(@PathVariable int id) {
        return productService.findById(id);
    }


    @GetMapping(value = "/random/{ret}")
    @ResponseStatus(HttpStatus.OK)
    public String random(@PathVariable String ret) {
        return productServiceV1.random(ret);
    }

    @GetMapping(value = "/remote/{ret}")
    @ResponseStatus(HttpStatus.OK)
    public String remote(@PathVariable String ret) {
        return productServiceV1.remoteInfo(ret);

    }
}