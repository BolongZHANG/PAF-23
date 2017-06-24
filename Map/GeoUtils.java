import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GeoUtils{
	
	public static double pas = 0.0001;
	
	public static boolean isVisible(double myLongitude,
			 						double myLatitude,
			 						double poiLongitude,
			 						double poiLatitude,
			 						ArrayList<Polygon> polyList) {
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
		  double hypotenuse = Math.sqrt((endY - startY) * (endY - startY) + (endX - startX) * (endX - startX));
		  double x_pas = pas * Math.abs(endX - startX) / hypotenuse;
		  double y_pas = pas * Math.abs(endY - startY) / hypotenuse;
		  double x = startX;
		  double y = startY;
		  double process = 0;
		  
		  while(process < hypotenuse){
			  pointList.add(new Point2D.Double(x, y));
			  x += x_pas;
			  y += y_pas;
			  process += pas;
		  }
		  
		  return pointList;	  
  }
  
}
