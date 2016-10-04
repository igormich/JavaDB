package select;

import java.util.Iterator;
import java.util.stream.Stream;

import records.Record;

public interface Query {
	
	public default Iterator<Record> execute() {
		return getData().iterator();
	}

	Stream<Record> getData();
	Stream<Record> getFullData();
}
