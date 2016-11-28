package ${packageName}.business.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

import ${packageName}.business.domain.Product;
import jp.co.aucnet.test.util.ExcelDataSetLoader;


@RunWith(SpringRunner.class)
@SpringBootTest

@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })

@DbUnitConfiguration(dataSetLoader = ExcelDataSetLoader.class)
public class ProductServiceTests {

    @Autowired
    ProductService productService;

    @Test
    @DatabaseSetup("products.xlsx")
    @DatabaseTearDown("products.xlsx")
    public void testData() {
        Product product = productService.findProductByName("Dell");

        assertThat(product.getName(), is("Dell"));

    }

}
