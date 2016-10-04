package records;

import java.util.function.Predicate;

import tables.Table;

public class ForeignKey<T> implements Predicate<T> {

	private Table table;
	private String field;

	public ForeignKey(Table table, String field) {
		this.table = table;
		this.field = field;
	}

	@Override
	public boolean test(T value) {
		return table.contains(field, value);
	}

}
