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
		 return new Where(this, this, where);
	}
	public GroupBy groupBy(String field) {
		return new GroupBy(this, this, field);
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
		Stream<Record> result = tables[0].getData();
		for(int i=1;i<tables.length;i++) {
			ReadonlyTable otherTable = tables[i];
			result = result.flatMap(lr -> otherTable.getData().map(rr -> new JoinRecord(lr, rr)));
		}
		return result;
	}

	public Join join(String otherTable, String field, String otherField) {
		return join(database.getTableOrView(otherTable), field, otherField);
	}

	public Join join(ReadonlyTable otherTable, String field, String otherField) {
		String tableField = field;
		if(!Arrays.stream(tables).anyMatch(t -> field.startsWith(t.getName())))
			if(tables.length==1)
				tableField = tables[0].getName() + '.' + field;
			else
				throw new IllegalArgumentException(String.format("Can'not locate field '%s'",tableField));
		return new Join(database , this, this, otherTable, tableField, otherField);
	}
}
