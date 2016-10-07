package database;

import java.util.HashMap;
import java.util.Map;

import records.FieldInfo;
import select.Query;
import tables.MemoryTable;
import tables.QueryView;
import tables.ReadonlyTable;
import tables.Table;

public class MemoryDatabase implements Database {

	protected Map<String, Table> tables = new HashMap<>();
	protected Map<String, ReadonlyTable> views = new HashMap<>();

	@Override
	public Table createTable(String name, FieldInfo... fields) {
		if(tables.containsKey(name))
			throw new IllegalStateException("Table "+ name + " exist");
		if (views.containsKey(name))
			throw new IllegalStateException("View " + name + " exist");
		MemoryTable table = new MemoryTable(name);
		for(FieldInfo field:fields)
			table.addField(field);
		tables.put(name, table);
		return table;
	}

	@Override
	public Table getTable(String name) {
		if(tables.containsKey(name))
			return tables.get(name);
		throw new IllegalArgumentException(String.format("Table %s not exist", name));
	}

	@Override
	public ReadonlyTable getTableOrView(String name) {
		if (tables.containsKey(name))
			return tables.get(name);
		if(views.containsKey(name))
			return views.get(name);
		throw new IllegalArgumentException(String.format("Table or view %s not exist", name));
		
	}
	@Override
	public ReadonlyTable createView(String name, Query query) {
		if (tables.containsKey(name))
					throw new IllegalStateException("View " + name + " exist");
		if (views.containsKey(name))
			throw new IllegalStateException("View " + name + " exist");
		QueryView view = new QueryView(name, query);
		views.put(name, view);
		return view;
	}

	public Delete delete(String table) {
		return new Delete(getTable(table));
	}

	public Update update(String table) {
		return new Update(getTable(table),(r) -> true);
	}

}
