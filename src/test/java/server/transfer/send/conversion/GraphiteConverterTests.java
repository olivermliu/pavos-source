package server.transfer.send.conversion;

import org.junit.Test;

import server.transfer.convert.GraphiteConverter;

public class GraphiteConverterTests {

	@Test
	public void addFloatMetric_nullValue_skip() {
		GraphiteConverter.addObservations(null, null);
	}

}
