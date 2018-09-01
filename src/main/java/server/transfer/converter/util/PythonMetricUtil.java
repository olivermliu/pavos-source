package server.transfer.converter.util;


import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyList;
import org.python.core.PyString;
import org.python.core.PyTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerFactory;

import server.transfer.converter.GraphiteConverter;
import server.transfer.data.ObservationData;
import server.transfer.data.util.GridTopicTranslator;

/**
 * Provides the functionality to create python metrics and log the results
 */
public final class PythonMetricUtil {
	
	private static final Logger logger = new Log4jLoggerFactory().getLogger(PythonMetricUtil.class.toString());
	
	private PythonMetricUtil() {
		
	}
	
    /**
     * Transforms a property into a Graphite-readable format with python
     * @param record The record of data that will be sent
     * @param list The list of metrics that were created from our data with python
     * @param observations Maps each set observed property to a value
     * @param graphTopic 
     * @param logger The logger documents 
     */
    public static void addFloatMetric(ConsumerRecord<String, ObservationData> record, 
    		PyList list, Map<String, String> observations) {
		for (Map.Entry<String, String> entry : observations.entrySet()) {
			String value = entry.getValue();
			if (value != null) {
				
				LocalDateTime ldc = LocalDateTime.parse(record.value().observationDate, DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
				PyString metricName = new PyString(record.topic() + "." + entry.getKey());
				PyInteger timestamp = new PyInteger((int) (ldc.toDateTime(DateTimeZone.UTC).getMillis() / 1000));
				PyFloat metricValue = new PyFloat(Double.parseDouble(value));
				PyTuple metric = new PyTuple(metricName, new PyTuple(timestamp, metricValue));
				list.append(metric);
				logMetric(metric);
			}
		}
    }
    
    /**
     * Transforms a property into a Graphite-readable format with python
     * @param record The record of data that will be sent
     * @param list The list of metrics that were created from our data with python
     * @param observations Maps each set observed property to a value
     * @param graphTopic 
     * @param logger The logger documents 
     */
    public static void addFloatMetric(ObservationData record, 
    		PyList list, Map<String, String> observations) {
		for (Map.Entry<String, String> entry : observations.entrySet()) {
			String value = entry.getValue();
			if (value != null) {
				LocalDateTime ldc = LocalDateTime.parse(record.observationDate, DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
				PyString metricName = new PyString(GridTopicTranslator.getTopic(record.sensorID, record.clusterID) + "." + entry.getKey());
				PyInteger timestamp = new PyInteger((int) (ldc.toDateTime(DateTimeZone.UTC).getMillis() / 1000));
				PyFloat metricValue = new PyFloat(Double.parseDouble(value));
				PyTuple metric = new PyTuple(metricName, new PyTuple(timestamp, metricValue));
				list.append(metric);
				logMetric(metric);
			}
		}
    }

    private static void logMetric(PyTuple metric) {
    	if (logger != null) {
    		logger.info("Added metric: " + metric.toString());
    	}
    }
	
}