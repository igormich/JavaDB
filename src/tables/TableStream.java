package tables;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import records.GroupByRecord;
import records.JoinRecord;
import records.Record;
import records.RenameRecord;

public class TableStream<T extends Record> {

	public static <T extends Record> Stream<Record> isEmpty(Stream<T> stream, Record obj) {
		Iterator<T> iterator = stream.iterator();
		if (iterator.hasNext())
			return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
		else
			return Stream.of(obj);
	}

	public static int nullSafeRecordComparator(Record r1, Record r2, Comparator<Record> comparator) {
		if (r1 == null ^ r2 == null) {
			return (r1 == null) ? -1 : 1;
		}
		if (r1 == null && r2 == null) {
			return 0;
		}
		return comparator.compare(r1, r2);
	}

	private Stream<T> stream;
	private Record nullField;

	TableStream(Stream<T> stream, Record nullField) {
		this.stream = stream;
		this.nullField = nullField;
	}

	public TableStream<T> filter(Predicate<? super T> predicate) {
		return new TableStream<T>(stream.filter(predicate), nullField);
	}

	public TableStream<JoinRecord> outerJoin(String field, MemoryTable other, String otherField) {
		Set<Object> set = new HashSet<>();
		String fullOtherField = other.getName()+ "." +otherField;
		Stream<JoinRecord> tempResult = leftJoin(field, other, otherField).stream
				.peek(rr -> {if(rr.get(fullOtherField) != null){set.add(rr.get(fullOtherField));}});
		Stream<JoinRecord> result = (Stream<JoinRecord>) Stream.concat(tempResult, other.getRecords().stream
				.filter(r -> !set.contains(r.get(otherField))).map(rr -> new JoinRecord(nullField, rr)));
		return new TableStream<JoinRecord>(result, new JoinRecord(this.nullField, other.getNullField()));
	}

	public TableStream<JoinRecord> join(String field, ReadonlyTable other, String otherField) {// inner
		if (other.isIndex(otherField))
			return joinWithIndex(field, other, otherField);
		Stream<JoinRecord> result = stream.flatMap(lr -> other.getRecords()
				.filter(rr -> Objects.equals(lr.get(field), rr.get(otherField))).map(rr -> new JoinRecord(lr, rr)));
		return new TableStream<JoinRecord>(result, new JoinRecord(nullField, other.getNullField()));
	}

	private TableStream<JoinRecord> joinWithIndex(String field, ReadonlyTable other, String otherField) {
		Stream<JoinRecord> result = stream.flatMap(
				lr -> other.getRecordsForIndex(otherField, lr.get(field)).stream.map(rr -> new JoinRecord(lr, rr)));
		return new TableStream<JoinRecord>(result, new JoinRecord(nullField, other.getNullField()));
	}

	public TableStream<JoinRecord> leftJoin(String field, ReadonlyTable other, String otherField) {
		if (other.isIndex(otherField))
			return leftJoinWithIndex(field, other, otherField);
		Record nullField = other.getNullField();
		Stream<JoinRecord> result = stream.flatMap(
				lr -> isEmpty(other.getRecords().stream.filter(rr -> Objects.equals(lr.get(field), rr.get(otherField))),
						nullField).map(rr -> new JoinRecord(lr, rr)));
		return new TableStream<JoinRecord>(result, new JoinRecord(this.nullField, other.getNullField()));
	}

	private TableStream<JoinRecord> leftJoinWithIndex(String field, ReadonlyTable other, String otherField) {
		Record nullField = other.getNullField();
		Stream<JoinRecord> result = stream
				.flatMap(lr -> isEmpty(other.getRecordsForIndex(otherField, lr.get(field)).stream, nullField)
						.map(rr -> new JoinRecord(lr, rr)));
		return new TableStream<JoinRecord>(result, new JoinRecord(this.nullField, other.getNullField()));
	}

	public TableStream<JoinRecord> joinRight(String field, ReadonlyTable other, String otherField) {
		Set<Object> set = new HashSet<>();
		Stream<JoinRecord> tempResult = stream
				.flatMap(lr -> other.getRecords().stream.filter(rr -> Objects.equals(lr.get(field), rr.get(otherField)))
						.peek(rr -> set.add(rr.get(otherField))).map(rr -> new JoinRecord(lr, rr)));
		Stream<JoinRecord> result = (Stream<JoinRecord>) Stream.concat(tempResult, other.getRecords().stream
				.filter(r -> !set.contains(r.get(otherField))).map(rr -> new JoinRecord(nullField, rr)));
		return new TableStream<JoinRecord>(result, new JoinRecord(this.nullField, other.getNullField()));
	}

	public TableStream<Record> join(ReadonlyTable other) {// outer
		Stream<Record> result = stream.flatMap(lr -> other.getRecords().map(rr -> new JoinRecord(lr, rr)));
		return new TableStream<Record>(result, new JoinRecord(nullField, other.getNullField()));
	}

	public void forEach(Consumer<T> object) {
		stream.forEach(object);
	}

	protected <N extends Record> Stream<N> map(Function<T, N> func) {
		return stream.map(func);
	}

	public TableStream<Record> uniform(TableStream<Record> tableStream) {
		return new TableStream<Record>(Stream.concat(stream, tableStream.stream), nullField);
	}

	public TableStream<T> orderBy(String name) {
		Function<Record, Object> getField = r -> r.get(name);
		@SuppressWarnings("rawtypes")
		Function _getField=getField;
		@SuppressWarnings("unchecked")
		Comparator<Record> comparator = Comparator.comparing(_getField,
				Comparator.nullsLast(Comparator.naturalOrder()));
		return new TableStream<T>(stream.sorted(comparator), nullField);
	}

	public TableStream<GroupByRecord> groupBy(String name) {
		Map<Object, List<Record>> map = stream.filter(r -> r.get(name) != null)
				.collect(Collectors.groupingBy(r -> r.get(name)));
		//return new TableStream<GroupByRecord>(map.values().stream().map(name,GroupByRecord::new), nullField);
		return null;
	}

	public GroupByRecord group() {
		//return new ListRecord(stream.collect(Collectors.toList()));
		return null;
	}

	public TableStream<T> top(int limit) {
		return new TableStream<T>(stream.limit(limit), nullField);
	}

	public TableStream<RenameRecord> remap(String[] names, Function<Record, ?>[] values) {
		return new TableStream<RenameRecord>(stream.map(r -> new RenameRecord(r, names, values)), nullField);
	}

	public TableStream<JoinRecord> _remap(String[] names, Function<Record, ?>[] values) {
		return new TableStream<JoinRecord>(stream.map(r -> new JoinRecord(r, nullField)), nullField);
	}

	public TableStream<T> peep(Consumer<T> consumer) {
		stream = stream.peek(consumer);
		return this;
	}

	public long count() {
		return stream.count();
	}

	public List<T> asList() {
		return stream.collect(Collectors.toList());
	}
}
