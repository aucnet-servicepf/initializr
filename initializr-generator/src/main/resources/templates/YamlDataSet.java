package jp.co.aucnet.test.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableIterator;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.RowOutOfBoundsException;
import org.dbunit.dataset.datatype.DataType;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;

public class YamlDataSet implements IDataSet {

    Map<String, MyTable> tables = new HashMap<String, MyTable>();


    /**
     * load data.
     * @param resource resource
     * @throws IOException IOException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public YamlDataSet(Resource resource) throws IOException  {

        Yaml load = new Yaml();

        Object loadedData = load.load(resource.getInputStream());

        Map<String, List<Map>> data = (Map<String, List<Map>>) loadedData;

        for (Map.Entry<String, List<Map>> ent : data.entrySet()) {
            String tableName = ent.getKey();
            List<Map> rows = ent.getValue();
            createTable(tableName, rows);

        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    class MyTable implements ITable {
        String name;
        List<Map> data;
        ITableMetaData meta;

        MyTable(String name, List<String> columnNames) {
            this.name = name;
            this.data = new ArrayList<Map>();
            meta = createMeta(name, columnNames);
        }

        ITableMetaData createMeta(String name, List<String> columnNames) {
            Column[] columns = null;
            if (columnNames != null) {
                columns = new Column[columnNames.size()];
                for (int i = 0; i < columnNames.size(); i++) {
                    columns[i] = new Column(columnNames.get(i), DataType.UNKNOWN);
                }
            }
            return new DefaultTableMetaData(name, columns);
        }

        public int getRowCount() {
            return data.size();
        }

        public ITableMetaData getTableMetaData() {
            return meta;
        }

        public Object getValue(int row, String column) throws DataSetException {
            if (data.size() <= row) {
                throw new RowOutOfBoundsException("" + row);
            }
            return data.get(row).get(column.toUpperCase());
        }

        public void addRow(Map values) {
            data.add(convertMap(values));
        }

        Map convertMap(Map<String, Object> values) {
            Map ret = new HashMap();
            for (Map.Entry<String, Object> ent : values.entrySet()) {
                ret.put(ent.getKey().toUpperCase(), ent.getValue());
            }
            return ret;
        }

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    MyTable createTable(String name, List<Map> rows) {

        MyTable table = new MyTable(name, rows.size() > 0
                        ? new ArrayList(rows.get(0).keySet()) : null);

        for (Map values : rows) {
            table.addRow(values);
        }
        tables.put(name, table);
        return table;
    }

    public ITable getTable(String tableName) throws DataSetException {
        return tables.get(tableName);
    }

    public ITableMetaData getTableMetaData(String tableName) throws DataSetException {
        return tables.get(tableName).getTableMetaData();
    }

    public String[] getTableNames() throws DataSetException {
        return (String[]) tables.keySet().toArray(new String[tables.size()]);
    }

    public ITable[] getTables() throws DataSetException {
        return (ITable[]) tables.values().toArray(new ITable[tables.size()]);
    }

    public ITableIterator iterator() throws DataSetException {
        return new DefaultTableIterator(getTables());
    }

    public ITableIterator reverseIterator() throws DataSetException {
        return new DefaultTableIterator(getTables(), true);
    }

    @Override
    public boolean isCaseSensitiveTableNames() {
        return false;
    }
}
