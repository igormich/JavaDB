package select;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import database.Database;
import records.JoinRecord;
import records.Record;
import tables.ReadonlyTable;

public class Join implements Query {

	private Query query;
	private Remapper remapper;
	private ReadonlyTable otherTable;
	private String tableField;
	private String otherField;
	private Database database;
	public Join(Database database, Remapper remapper, Query query, ReadonlyTable otherTable, String tableField, String otherField) {
		this.database = database;
		this.query = query;
		this.remapper = remapper;
		this.otherTable = otherTable;
		this.tableField = tableField;
		this.otherField = otherField;

	}

	@Override
	public Stream<? extends Record> getData() {
		return remapper.remap(getFullData());
	}

	@Override
	public Stream<Record> getFullData() {
		if (otherTable.isIndex(otherField))
			return joinWithIndex();
		else {
			Stream<Record> result = query.getFullData().flatMap(lr -> otherTable.getData()
					.filter(rr -> Objects.equals(
							lr.get(tableField),
							rr.get(otherField)))
					.map(rr -> new JoinRecord(lr, rr)));
			return result;
		}
	}
	private Stream<Record> joinWithIndex() {
		Stream<Record> result =query.getFullData()
				.flatMap(lr -> otherTable.getDataForIndex(otherField, lr.get(tableField))
				.map(rr -> new JoinRecord(lr, rr)));
		return result;
	}

	public Where where(Predicate<Record> where) {
		 return new Where(remapper, this, where);
	}
	public GroupBy groupBy(String field) {
		return new GroupBy(remapper, this, field);
	}

	public Join join(String otherTable, String field, String otherField) {
		return join(database.getTableOrView(otherTable), field, otherField);
	}

	public Join join(ReadonlyTable otherTable, String field, String otherField) {
		String tableField = field;
		return new Join(database, remapper, this, otherTable, tableField, otherField);
	}
	
}
