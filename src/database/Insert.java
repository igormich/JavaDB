package database;

import java.util.HashMap;

import tables.Table;

public class Insert {

	private Table table;
	private String[] fields;

	Insert(Table table, String[] fields) {
		this.table = table;
		this.fields = fields;
	}
	
	public void values(Object ... values){
		if((fields == null)||(fields.length==0))
			table.insert(values);
		else {
			HashMap<String, Object> data = new HashMap<String, Object>();
			for(int i=0;i<fields.length; i++)
				data.put(fields[i], values[i]);
			table.insert(data);
		}
	}
}
