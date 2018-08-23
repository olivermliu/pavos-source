package server.core.grid;

import static org.junit.Assert.fail;

import java.awt.geom.Point2D;
import java.util.Collection;

import org.junit.Test;

import server.core.grid.config.Seperators;
import server.core.grid.config.WorldMapData;
import server.core.grid.exceptions.PointNotOnMapException;
import server.core.grid.polygon.GeoPolygon;
import server.transfer.data.ObservationData;
import server.transfer.sender.util.TimeUtil;

public class GeoRectangleGridTest {

	@Test
	public void test() {
		GeoGrid grid = new GeoRectangleGrid(new Point2D.Double(WorldMapData.lngRange * 2, WorldMapData.latRange * 2),  2, 2, 3, "testGrid");
		
		ObservationData data = new ObservationData();
		data.observationDate = TimeUtil.getUTCDateTimeString();
		data.sensorID = "testSensorID1";
		String property = "temperature_celsius";
		data.observations.put(property, "14.0");
		
		Point2D.Double location1 = new  Point2D.Double(300.0, 80.0);
		grid.addObservation(location1, data);
		
		data = new ObservationData();
		data.sensorID = "testSensorID2";
		data.observations.put(property, "28.0");
		
		Point2D.Double location2 = new  Point2D.Double(260.0, 80.0);
		grid.addObservation(location2, data);
		
		data = new ObservationData();
		data.sensorID = "testSensorID3";
		data.observations.put(property, "28.0");
		
		Point2D.Double location3 = new  Point2D.Double(260.0, 80.0);
		grid.addObservation(location3, data);
		
		String clusterID = null;
		GeoPolygon poly = null;
		try {
			poly = grid.getPolygonContaining(location1, grid.MAX_LEVEL);
			clusterID = poly.ID;
		} catch (PointNotOnMapException e) {
			fail("Location out of map bounds.");
		}
		System.out.println("ClusterID: " + clusterID);
		System.out.println("Total sensors: " + poly.getNumberOfSensors());
		System.out.println("Sensors with property '" + property + "': " + poly.getNumberOfSensors(property));
		System.out.println(observationsToString(poly.getSensorDataList()));
		
		for (GeoPolygon poly0 : grid.polygons) {
			System.out.println(observationToString(poly0.cloneObservation()));
			Collection<GeoPolygon> subPolygons0 = poly0.getSubPolygons();
			for (GeoPolygon poly1 : subPolygons0) {
				Collection<GeoPolygon> subPolygons1 = poly1.getSubPolygons();
				System.out.println(observationToString(poly1.cloneObservation()));
				for (GeoPolygon poly2 : subPolygons1) {
					System.out.println(observationToString(poly2.cloneObservation()));
				}
			}
		}
		System.out.println();
		grid.updateObservations();
		
		for (GeoPolygon poly0 : grid.polygons) {
			System.out.println(observationToString(poly0.cloneObservation()));
			Collection<GeoPolygon> subPolygons0 = poly0.getSubPolygons();
			for (GeoPolygon poly1 : subPolygons0) {
				Collection<GeoPolygon> subPolygons1 = poly1.getSubPolygons();
				System.out.println(observationToString(poly1.cloneObservation()));
				for (GeoPolygon poly2 : subPolygons1) {
					System.out.println(observationToString(poly2.cloneObservation()));
				}
			}
		}
		
		System.out.println(grid.getPolygon(grid.GRID_ID + Seperators.GRID_CLUSTER_SEPERATOR + "0_1").getJson(property));
	}
	
	private String observationToString(ObservationData data) {
		return "ObservationData: " + data.observationDate + ", " + data.sensorID + ", " + data.clusterID + ", " + data.observations;
	}
	
	private String observationsToString(Collection<ObservationData> collection) {
		String result = "";
		for (ObservationData data : collection) {
			result = result + observationToString(data) + "\n";
		}
		return result;
	}

}
