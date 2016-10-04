package records;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JoinRecord implements Record{
	
		private Record l;
		private Record r;
		private Map<String,Record> namesMapping=new HashMap<>();

		public JoinRecord(Record l,Record r){
			this.l = l;
			this.r = r;
			remap(l);
			remap(r);
		}

		private void remap(Record record) {
			if(record instanceof JoinRecord){
				namesMapping.putAll(((JoinRecord)record).namesMapping);
			} else {
			String tableName = record.getTableName();
			record.getFieldsNames().forEach(name ->
				namesMapping.put(tableName+'.'+name, record));
			}
		}

		@Override
		public Object get(String name) {
			Record record = namesMapping.get(name);
			if(record!=null)
				return record.get(name);
			System.out.println(namesMapping.keySet());
			throw new RuntimeException(
					String.format("Can not found data for '%s'", name));
		}
		@Override
		public Set<String> getFieldsNames() {
			return namesMapping.keySet();
		}
		public String toString() {
			StringBuilder result = new StringBuilder();
			result.append(l.toString());
			result.setLength(result.length()-1);
			result.append(',');
			result.append(r.toString().substring(1));
			return result.toString();
		}
		@Override
		public String getTableName() {
			return "TEMPORALLY TABLE: JOIN " + l.getTableName() + " WITH " + r.getTableName();
		}

		@Override
		public FieldInfo getFieldInfo(String name) {
			Record record = namesMapping.get(name);
			return record.getFieldInfo(name);
		}
	}