package select;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import records.Record;

public class Where implements Query{

	private Remapper remapper;
	private Query query;
	private Predicate<Record> where;


	public Where(Remapper remapper, Query query, Predicate<Record> where) {
		this.remapper = remapper;
		this.query = query;
		this.where = where;
	}

	public Stream<Record> getData() {
		return remapper.remap(getFullData());
	}
	@Override
	public Stream<Record> getFullData() {
		return query.getFullData().filter(where);
	}
	public GroupBy groupBy(String field) {
		return new GroupBy(remapper,this, field);
	}


}
