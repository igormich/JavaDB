package queries;

import java.util.HashMap;

import records.MapRecord;
import tables.Table;

public class Insert {

	private Table table;
	private String[] fields;

	public Insert(Table table, String[] fields) {
		this.table = table;
		this.fields = fields;
	}
	
	public void values(Object... values) {
		if ((fields == null) || (fields.length == 0)) {
			HashMap<String, Object> data = new HashMap<String, Object>();
			int i = 0;
			for (String name : table.getFieldsNames())
				if ((i < values.length) && (!table.getFieldInfo(name).isAutoIncrement()))
					data.put(name, values[i++]);
			table.insert(new MapRecord(data));
		} else {
			HashMap<String, Object> data = new HashMap<String, Object>();
			for (int i = 0; i < fields.length; i++)
				data.put(fields[i], values[i]);
			table.insert(new MapRecord(data));
		}
	}
}
