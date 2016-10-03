package database;

import java.util.function.Function;
import java.util.function.Supplier;

import records.FieldInfo;
import records.Record;
import select.Select;
import tables.ReadonlyTable;
import tables.Table;
import tables.TableStream;

public interface Database {
	
	public static final Object ALL = null;
	
	Table createTable(String name, FieldInfo... fields);
	Table getTable(String name);
	ReadonlyTable getTableOrView(String name);
	ReadonlyTable createView(String name, Supplier<TableStream<? extends Record>> dataSourse);
	
	default <T extends Record> Select select(){
		return new Select(this);
	}
	@SuppressWarnings("unchecked")
	default <T extends Record> Select select(String name,Function<T, ?> field){
		return new Select(this, new String[]{name}, new Function[]{field});
	}
	@SuppressWarnings("unchecked")
	default <T extends Record> Select select(String name1, Function<T, ?> function1,
			String name2, Function<T, ?> function2) {
		return new Select(this, new String[]{name1,name2}, new Function[]{function1,function2});
	}
	@SuppressWarnings("unchecked")
	default <T extends Record> Select select(String name1, Function<T, ?> function1,
			String name2, Function<T, ?> function2, String name3, Function<T, ?> function3) {
		return new Select(this, new String[]{name1,name2,name3}, new Function[]{function1,function2,function3});
	}
	default <T extends Record> Select select(String field){
		return select(field, r -> r.get(field));
	}
	default TableValueInserter insertInto(String table, String... fields){
		return new TableValueInserter(getTable(table), fields);
	}

	
}
