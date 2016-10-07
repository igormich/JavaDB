package records;

import java.util.function.Predicate;

import tables.Table;

public class ForeignKey<T> implements Predicate<T> {

	private final Table table;
	private final String field;

	public ForeignKey(Table table, String field) {
		this.table = table;
		this.field = field;
	}
	public void onUpdate(){
	//cascade	
	}
	public void onDelete(){
		
	}
	@Override
	public boolean test(T value) {
		return getTable().contains(getField(), value);
	}
	
	public Table getTable() {
		return table;
	}
	
	public String getField() {
		return field;
	}

}
