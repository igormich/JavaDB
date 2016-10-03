package select;

import java.util.stream.Stream;
import records.Record;

interface Remapper {
	Stream<Record> remap(Stream<Record> data);
}
