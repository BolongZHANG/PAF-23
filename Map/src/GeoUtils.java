import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GeoUtils{
	
	public static double SEUIL = 0.00001;
	public static double pas = 0.00001;
	
	 public static boolean isVisible(double myLongitude
			  , double myLatitude
			  , double poiLongitude
			  , double poiLatitude
			  , ArrayList<Polygon> polyList) {
		  Polygon poiPoly = null; 
		  
		  Set<Polygon> polySet = new HashSet<>();	  
		  List<Point2D.Double> pointList = divideLine(myLongitude, myLatitude, poiLongitude, poiLatitude, pas);
	  	
		  for(Polygon poly : polyList){
		    	if(poly.contains(poiLongitude, poiLatitude)) poiPoly = poly;
		    	
		    	for(Point2D.Double point : pointList){
		    		if(poly.contains(point)){
		    			polySet.add(poly);
		    			break;
		    		}
		    	}
		  }
		  
		  polySet.remove(poiPoly);
		  return polySet.isEmpty();
	
	  }
  
  private static ArrayList<Point2D.Double> divideLine(double startX, double startY, double endX, double endY, double pas){
	  ArrayList<Point2D.Double> pointList = new ArrayList<>();
	  double distance = Math.sqrt((endY - startY) * (endY - startY) + (endX - startX) * (endX - startX));
	  double x_pas = ;
	  double y_pas = ;
	  double x = startX;
	  double y = startY;
	  double process = 0;
	  
	  while( Math.abs(process - distance) > pas){
		  pointList.add(new Point2D.Double(x, y));
		  x += x_pas;
		  y += y_pas;
		  process += pas;
	  }
	  
	  return pointList;
	  
  }
  
  
  public static void main(String[] args) {
		// TODO Auto-generated method stub
//		final Polygon polygon1 = new Polygon();
//	    polygon1.addPoint(-10, -10);
//	    polygon1.addPoint(-terqt10, 10);
//	    polygon1.addPoint(10, 10);
//	    polygon1.addPoint(10, -10);
//	    
//	    final Polygon polygon2 = new Polygon();
//	    polygon2.addPoint(12, 12);
//	    polygon2.addPoint(12, 18);
//	    polygon2.addPoint(18, 18);
//	    polygon2.addPoint(18, 12);
//	    
//	    ArrayList<Polygon> polygonList = new ArrayList<>();	    
//	    polygonList.add(polygon1);
//	    polygonList.add(polygon2);
	  	
	  String x = "3.1214926979887656987654321";
	  System.out.println(x);
	  System.out.println(Double.parseDouble(x)+0.000001);
			  
	  
	  
  }
}
