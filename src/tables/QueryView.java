package tables;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import fields.FieldInfo;
import records.Record;
import select.Query;

public class QueryView implements ReadonlyTable {

	private class ViewRecord implements Record{

		private final Record record;
		
		ViewRecord(Record record){
			this.record = record;
		}
		@Override
		public Object get(String name) {
			if(name.startsWith(getTableName())){
					return record.get(name.substring(getTableName().length()+1));
			}
			throw new IllegalArgumentException(String.format("Field name must start with view name '%s'", name));
		}

		@Override
		public Set<String> getFieldsNames() {
			return record.getFieldsNames();
		}

		@Override
		public FieldInfo getFieldInfo(String name) {
			if(name.startsWith(getTableName())){
				return record.getFieldInfo(name.substring(getTableName().length()+1));
			}
			throw new IllegalArgumentException(String.format("Field name must start with view name '%s'", name));
		}

		@Override
		public String getTableName() {
			return QueryView.this.getName();
		}
		
	}
	private String name;
	private Query query;

	public QueryView(String name, Query query) {
		this.name = name;
		this.query = query;

	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Set<String> getFieldsNames() {
		return null;
	}

	@Override
	public Map<String, FieldInfo> getFieldsInfo() {
		throw new UnsupportedOperationException("Cat not get field info before execution");
	}

	@Override
	public FieldInfo getFieldInfo(String name) {
		throw new UnsupportedOperationException("Cat not get field info before execution");
	}

	@Override
	public boolean isIndex(String index) {
		return false;
	}

	@Override
	public boolean contains(String name, Object value) {
		return false;
	}

	@Override
	public Stream<Record> getData() {
		return query.getData().map(ViewRecord::new);
	}

	@Override
	public Stream<Record> getDataForIndex(String index, Object value) {
		throw new IllegalArgumentException(String.format("Field '%s' is not index", name));
	}

}
