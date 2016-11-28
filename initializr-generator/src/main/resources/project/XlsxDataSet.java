package jp.co.aucnet.test.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.excel.XlsDataSet;

/**
 * 出力を新型Excel(2007以降のxlsx型)に固定するDataSet。
 */
public class XlsxDataSet extends XlsDataSet {

    public XlsxDataSet(File file) throws IOException, DataSetException {
        super(file);
    }

    public static void write(IDataSet dataSet, OutputStream out) throws IOException, DataSetException {
        new XlsxDataSetWriter().write(dataSet, out);
    }

}
