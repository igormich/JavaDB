package select;

import java.util.stream.Stream;
import records.Record;

interface Remapper {
	Stream<? extends Record> remap(Stream<? extends Record> data);
}
