package database;

import java.util.stream.Stream;

import records.Record;

public interface DataSourse {
	Stream<? extends Record> getData();
}
