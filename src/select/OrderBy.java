package select;

import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Stream;

import records.Record;

public class OrderBy implements Query {

	private Query query;
	private Function<Record,?> keyExtractor;
	public OrderBy(Remapper remapper, Query query, String field) {
		this.query = query;
		keyExtractor = r-> r.get(field);
	}

	@Override
	public Stream<Record> getData() {
		@SuppressWarnings("rawtypes")
		Function _keyExtractor = keyExtractor;
		@SuppressWarnings("unchecked")
		Stream<Record> result = query.getData().sorted(Comparator.comparing(_keyExtractor));
		return result;
	}

	@Override
	public Stream<Record> getFullData() {
		@SuppressWarnings("rawtypes")
		Function _keyExtractor = keyExtractor;
		@SuppressWarnings("unchecked")
		Stream<Record> result = query.getData().sorted(Comparator.comparing(_keyExtractor));
		return result;
	}


}
