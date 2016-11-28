package jp.co.aucnet.test.util;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dbunit.dataset.excel.XlsDataSetWriter;

/**
 * 出力を新型Excel(2007以降のxlsx型)に固定するDataSetWriter。
 */
public class XlsxDataSetWriter extends XlsDataSetWriter {

    /**
     * workbookオブジェクト作るところを「XSSFWorkbook(xlsx型)」固定に。
     */
    @Override
    protected Workbook createWorkbook() {
        return new XSSFWorkbook();
    }

}
