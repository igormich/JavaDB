package select;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import database.Database;
import records.Record;
import records.RenameRecord;
import tables.ReadonlyTable;

public class Select implements Query, Remapper{

	private Database database;
	private Function<Record, ?>[] fields;
	private ReadonlyTable[] tables;
	private String[] names;

	public Select(Database database,String[] names ,Function<Record, ?>[] fields) {
		this.database = database;
		this.names = names;
		this.fields = fields;
	}

	public Select(Database database) {
		this(database, null, null);
	}
	public Select from(ReadonlyTable... tables) {
		this.tables = tables;
		return this;
	}
	public Select from(String... tables) {
		 return from(Arrays.stream(tables).map(database::getTableOrView).toArray(ReadonlyTable[]::new));
	}
	public Where where(Predicate<Record> where) {
		 return new Where(this, where);
	}

	public Stream<Record> getData() {
		if(fields!=null)
			return remap(getFullData());//
		else
			return getFullData();
	}
	
	public Stream<Record> remap(Stream<Record> data) {
		return data.map(r -> new RenameRecord(r, names, fields));
	}

	public Stream<Record> getFullData() {
		return tables[0].getData();
	}

	public GroupBy groupBy(String field) {
		return new GroupBy(this, this, field);
	}
}
