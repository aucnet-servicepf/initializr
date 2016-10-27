package jp.co.aucnet.test.util;

import org.dbunit.dataset.IDataSet;
import org.springframework.core.io.Resource;

import com.github.springtestdbunit.dataset.AbstractDataSetLoader;

public class YamlDataSetLoader extends AbstractDataSetLoader {

    @Override
    protected IDataSet createDataSet(Resource resource) throws Exception {

        return new YamlDataSet(resource);
    }

}
