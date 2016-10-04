package database;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import records.Record;
import tables.Table;
import utils.Pair;

public class Update {

	private Table table;
	private Predicate<Record> predicate;

	public Update(Table table, Predicate<Record> predicate) {
		this.table = table;
		this.predicate = predicate;
	}

	public Update where(Predicate<Record> other) {
		return new Update(table, predicate.and(other));
	}

	public void set(Function<Record, Map<String, Object>> function) {
		table.update(function, predicate);
	}
	public void  set(String field, Function<Object,Object> function) {
		table.update(r -> new Pair<String,Object>(field, function.apply(r.get(field))), predicate);
	}

	public void set(String field, Object value) {
		table.update(r -> new Pair<String,Object>(field, value), predicate);	
	}
}
