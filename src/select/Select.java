package select;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import database.Database;
import records.JoinRecord;
import records.Record;
import records.RenameRecord;
import tables.ReadonlyTable;
import tables.TableStream;

public class Select implements Remapper{

	private Database database;
	private Function<Record, ?>[] fields;
	private String[] names;

	public Select(Database database,String[] names ,Function<Record, ?>[] fields) {
		this.database = database;
		this.names = names;
		this.fields = fields;
	}

	public Select(Database database) {
		this(database, null, null);
	}
	public From from(ReadonlyTable... tables) {
		return new From(this, tables, database);
	}
	public From from(String... tables) {
		 return from(Arrays.stream(tables).map(database::getTableOrView).toArray(ReadonlyTable[]::new));
	}
	
	public Stream<? extends Record> remap(Stream<? extends Record> data) {
		if(fields!=null)
			return data.map(r -> new RenameRecord(r, names, fields));
		return data;
	}
}
