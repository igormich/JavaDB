package select;

import java.util.function.Predicate;
import java.util.stream.Stream;

import records.Record;

public class Where implements Query{

	private Select select;
	private Predicate<Record> where;

	Where(Select select, Predicate<Record> where) {
		this.select = select;
		this.where = where;
	}

	public Stream<Record> getData() {
		return select.remap(getFullData());
	}
	@Override
	public Stream<Record> getFullData() {
		return select.getFullData().filter(where);
	}
	public GroupBy groupBy(String field) {
		return new GroupBy(select,this, field);
	}


}
