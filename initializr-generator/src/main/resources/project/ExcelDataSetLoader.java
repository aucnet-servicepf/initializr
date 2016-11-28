package jp.co.aucnet.test.util;



import org.dbunit.dataset.IDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.github.springtestdbunit.dataset.AbstractDataSetLoader;

/**
 * spring-test-dbunit本家にはない「Excelのデータを読み込むLoader」クラス。
 */
public class ExcelDataSetLoader extends AbstractDataSetLoader {

    private final Logger logger = LoggerFactory.getLogger(ExcelDataSetLoader.class);

    @Override
    protected IDataSet createDataSet(Resource resource) throws Exception {
        logger.trace("Load excel file : " + resource.getFile().getPath());
        return new XlsxDataSet(resource.getFile());
    }

}
