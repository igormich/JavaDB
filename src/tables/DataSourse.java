package tables;

import java.util.stream.Stream;

import records.Record;

public interface DataSourse {
	Stream<Record> getData();
}
