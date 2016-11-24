package select;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

import database.Database;
import records.JoinRecord;
import records.Record;
import tables.ReadonlyTable;

public class From implements Query {

	private Remapper remapper;
	private ReadonlyTable[] tables;
	private Database database;

	public From(Remapper remapper, ReadonlyTable[] tables, Database database) {
		this.remapper = remapper;
		this.tables = tables;
		this.database = database;
	}

	public Where where(Predicate<Record> where) {
		 return new Where(remapper, this, where);
	}
	
	public GroupBy groupBy(String field) {
		return new GroupBy(remapper, this, field);
	}
	
	public Stream<? extends Record> getData() {
			return remapper.remap(getFullData());
	}
	@Override
	public Stream<? extends Record> getFullData() {
		Stream<? extends Record> result = tables[0].getData();
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
		return new Join(database, remapper, this, otherTable, tableField, otherField);
	}
}
