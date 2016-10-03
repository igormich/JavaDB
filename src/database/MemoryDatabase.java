package database;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import records.FieldInfo;
import records.Record;
import tables.MemoryTable;
import tables.ReadonlyTable;
import tables.Table;
import tables.TableStream;

public class MemoryDatabase implements Database {

	protected Map<String, Table> tables = new HashMap<>();
	protected Map<String, ReadonlyTable> views = new HashMap<>();

	@Override
	public Table createTable(String name, FieldInfo... fields) {
		if(tables.containsKey(name))
			throw new IllegalStateException("Table "+ name + " exist");
		MemoryTable table = new MemoryTable(name);
		for(FieldInfo field:fields)
			table.addField(field);
		tables.put(name, table);
		return table;
	}

	@Override
	public ReadonlyTable createView(String name,
			Supplier<TableStream<? extends Record>> dataSourse) {
		//if (tables.containsKey(name))
		//	throw new IllegalStateException("View " + name + " exist");
		//SelectView view = new SelectView(name, dataSourse);
		//views.put(name, view);
		//return view;
		return null;
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

}