package tables;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import records.FieldInfo;
import records.Record;

public class MemoryTable implements Table{
	
	private class DirectRecord implements Record{
		private final int index;
		private DirectRecord(int index) {
			super();
			this.index = index;
		}
		private DirectRecord(Integer index) {
			super();
			this.index = index;
		}
		@Override
		public Object get(String name) {
			if(name.contains("."))
				return fields.get(name.substring(name.lastIndexOf(".")+1)).get(index);
			else
				return fields.get(name).get(index);
		}
		public String toString() {
			StringBuilder result = new StringBuilder();
			result.append('[');
			getFieldsNames().stream().forEach(field ->{
				  result.append(getTableName());
				  result.append('.');				
				  result.append(field);
				  result.append(':');
				  result.append(get(field));
				  result.append(',');
				});
			result.setLength(result.length()-1);
			result.append(']');
			return result.toString();
		}
		@Override
		public Set<String> getFieldsNames(){
			return MemoryTable.this.getFieldsNames();
		}
		@Override
		public String getTableName() {
			return MemoryTable.this.getName();
		}
		@Override
		public int hashCode() {
			return 31 * Objects.hashCode(MemoryTable.this)+index;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DirectRecord other = (DirectRecord) obj;
			return Objects.equals(getTableName(), getTableName()) && (index == other.index);
		}
		@Override
		public FieldInfo getFieldInfo(String name) {
			if(name.contains("."))
				return MemoryTable.this.getFieldInfo((name.substring(name.lastIndexOf(".")+1)));
			else
				return MemoryTable.this.getFieldInfo(name);
		}
	}
	private static final int DEFAULT_TABLE_SIZE = 32;
	protected BitSet isUsed = new BitSet(DEFAULT_TABLE_SIZE);
	protected Map<String,List<Object>> fields = new LinkedHashMap<>();
	protected Map<String,FieldInfo> fieldsInfo = new HashMap<>();
	protected Map<String,Map<Object,BitSet>> indices  = new HashMap<>();
	private Map<String, Set<Object>> uniques = new HashMap<>();
	//protected Set<String> uniques = new HashSet<>();
	//protected Set<Object> notNulls = new HashSet<>();;
	protected String name;
	
	
	
	public MemoryTable(String name) {
		this.name = name;
	}
	@Override
	public Set<String> getFieldsNames(){
		return fields.keySet();
	}
	@Override
	public Map<String, FieldInfo> getFieldsTypes(){
		return Collections.unmodifiableMap(fieldsInfo);
	}
	private void validate(String[] names, Object[] values) {
		for(int i=0;i<names.length;i++){
			String name = names[i];
			Object value = i<values.length ? values[i] : null;
			FieldInfo fieldInfo = fieldsInfo.get(name);
			if(fieldInfo.isNotNull() && (value == null)){
				throw new IllegalArgumentException(
						String.format("Field '%s' is NOT NULL but inserted value is null", name));
			}
			if(fieldInfo.isUnique() && (uniques.get(name).contains(value))){
				throw new IllegalArgumentException(
						String.format("Dublicated values for unique field '%s'. Value is %s", name, value));
			}
			if((value != null) && (value.getClass() != fieldInfo.getType())){
				throw new IllegalArgumentException(
						String.format("Field '%s' type and value type are different (%s, %s)", name, 
								value.getClass().getSimpleName(), fieldInfo.getType().getSimpleName()));
			}
			try{
				if(!fieldInfo.getConstraint().test(value)){
					throw new IllegalArgumentException(
							String.format("Constraint in field '%s' not valid for %s", name, value));
				}
			} catch (Exception e) {
				throw new IllegalArgumentException(
						String.format("Constraint in field %s not valid for %s", name, value));
			}
		}
	}
	@Override
	public synchronized void insert(String[] names, Object[] values) {
		if ((names ==null)||(names.length == 0))
			names = getFieldsNames().toArray(names);
		validate(names, values);
		int pos = isUsed.nextClearBit(0);
		boolean isLast = pos == isUsed.length();
		for(int i=0;i<names.length;i++){
			String name = names[i];
			Object value = i<values.length ? values[i] : null;
			if(isLast)
				fields.get(name).add(pos,values[i]);
			else
				fields.get(name).set(pos,values[i]);
			if(indices.containsKey(name)){
				addToIndex(name,value,pos);
			}
			if(uniques.containsKey(name)){
				uniques.get(name).add(value);
			}
		}
		isUsed.set(pos);
	}
	private void addToIndex(String name, Object value,int pos) {
		BitSet fields = indices.get(name).get(value);
		if(fields == null){
			fields = new BitSet();
			indices.get(name).put(value, fields);
		}
		fields.set(pos);
	}
	@Override
	public boolean contains(String name, Object value) {
		if(isIndex(name))
			return indices.get(name).containsKey(value);
		return fields.get(name).contains(value);
	}
	@Override
	public int hashCode() {
		return 31 * Objects.hashCode(name);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MemoryTable other = (MemoryTable) obj;
		return Objects.equals(name, other.name);
	}
	@Override
	public TableStream<Record> getRecords() {
		return new TableStream<Record>(isUsed.stream().mapToObj(DirectRecord::new),getNullField());		
	}
	@Override
	public TableStream<Record> getRecordsForIndex(String field) {
		Map<?, BitSet> index = indices.get(field);
		if(index != null)
			return new TableStream<Record>(index.values().stream().flatMapToInt(list -> list.stream())
					.mapToObj(DirectRecord::new),getNullField());
		return null;		
	}
	@Override
	public TableStream<Record> getRecordsForIndex(String field,Object value) {
		Map<?, BitSet> index = indices.get(field);
		if(index != null){
			BitSet fields = index.get(value);
			if(fields != null)
				return new TableStream<Record>(fields.stream().mapToObj(DirectRecord::new), getNullField());
			else
				return new TableStream<Record>(Stream.empty(), getNullField());
		}
		return null;		
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public boolean isIndex(String index) {
		return indices.containsKey(index);
	}
	@Override
	public FieldInfo getFieldInfo(String name) {
		return fieldsInfo.get(name);
	}
	@Override
	public void createIndex(String name) {
		Map<Object, BitSet> index = new TreeMap<>();
		indices.put(name,index);
		List<Object> field = fields.get(name);
		isUsed.stream().forEach((pos) -> addToIndex(name, field, pos));
	}
	@Override
	public void dropIndex(String name) {
		indices.remove(name);
	}
	@Override
	public synchronized long delete(Predicate<Record> where) {
		return isUsed.stream()
				.mapToObj(DirectRecord::new)
				.filter(where)
				.mapToInt(r -> r.index)
				.peek(i -> isUsed.clear(i))
				.peek(i -> deleteFromIndex(i))
				.count();
	}
	private void deleteFromIndex(int i) {
		for(String indexName:indices.keySet()){
			Object value = fields.get(indexName).get(i);
			indices.get(indexName).get(value).clear(i);
		}
	}
	@Override
	public long update(Function<Record,Map<String,Object>> update) {
		isUsed.stream()
			.mapToObj(DirectRecord::new)
			.forEach(r ->updateRecord(r.index,update.apply(r)));
		return isUsed.cardinality();
	}
	@Override
	public long update(Function<Record, Map<String, Object>> update, Predicate<Record> where) {
		return isUsed.stream()
				.mapToObj(DirectRecord::new)
				.filter(where)
				.peek(r ->updateRecord(r.index,update.apply(r))).count();
	}
	private void updateRecord(int pos, Map<String, Object> newValues) {
		for(String fieldName:newValues.keySet()){
			Object newValue = newValues.get(fieldName);
			if(indices.containsKey(fieldName)){
				Object oldValue = fields.get(fieldName).get(pos);
				indices.get(fieldName).get(oldValue).clear(pos);
				indices.get(fieldName).get(newValue).set(pos);
			}
			fields.get(fieldName).set(pos, newValue);
		}
	}
	@Override
	public void deleteField(String name) {
		fields.remove(name);
		indices.remove(name);
		fieldsInfo.remove(name);
	}
	@Override
	public void addField(FieldInfo fieldInfo) {
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		String name = fieldInfo.getName();
		fields.put(name, fieldValues);
		fieldsInfo.put(name, fieldInfo);
		if(fieldInfo.isIndex()){
			if(Arrays.asList(fieldInfo.getType().getInterfaces()).contains(Comparable.class)){
				indices.put(name, new TreeMap<>());
			} else {
				throw new IllegalArgumentException(
						String.format("Field %s cannot be indexed, because is not comparable", name));
			}
		}	
		if(fieldInfo.isUnique())
			uniques.put(name, new HashSet<>());
		/*if(field.isNotNull())
			notNulls.add(name);*/
		//isUsed.stream().mapToObj(DirectRecord::new).
		//	forEach(r -> fieldValues.add(r.index, valueProducer.apply(r)));
	}
	@Override
	public Stream<Record> getData() {
		return isUsed.stream().mapToObj(DirectRecord::new);
	}

}
