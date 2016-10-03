package database;

import tables.Table;

public class TableValueInserter {

	private Table table;
	private String[] fields;

	public TableValueInserter(Table table, String[] fields) {
		this.table = table;
		this.fields = fields;
	}
	public void values(Object ... values){
		table.insert(fields, values);
	}
}
