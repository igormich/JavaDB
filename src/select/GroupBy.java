package select;


import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import records.GroupByRecord;
import records.Record;

public class GroupBy implements Query{

	private Remapper remapper;
	private String field;
	List<Function<Record, ?>> groupingFields;
	private Query query;

	GroupBy(Remapper remapper,Query query, String field) {
		this.remapper = remapper;
		this.query = query;
		//groupingFields = Arrays.stream(fields).map(GroupBy::groupField).collect(Collectors.toList());
		this.field = field;
	}
	@SuppressWarnings("unused")
	private static Function<Record, ?> groupField(String name){
		return r -> r.get(name);
	}

	@Override
	public Stream<? extends Record> getData() {
		//Map<Object, List<Record>> map = query.getFullData().filter(r -> r.get(field) != null)
			//	.collect(Collectors.groupingBy(r -> r.get(field)));
		return remapper.remap(getFullData());
		//return select.remap(map.values().stream().map(ListRecord::new));
	}
	@Override
	public Stream<Record> getFullData() {
		Map<Object, List<Record>> map = query.getFullData().filter(r -> r.get(field) != null)
				.collect(Collectors.groupingBy(r -> r.get(field)));
		return map.entrySet().stream().map(e -> new GroupByRecord(field,e.getKey(),e.getValue()));
	}
	public OrderBy orderBy(String field) {
		return new OrderBy(remapper, this, field);
	}

}
