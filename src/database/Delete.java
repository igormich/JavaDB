package database;

import java.util.function.Predicate;

import records.Record;
import tables.Table;

public class Delete {

	private Table table;

	public Delete(Table table) {
		this.table = table;
	}

	public long where(Predicate<Record> predicate) {
		return table.delete(predicate);
	}

}
