package com.graphql.code_challenge;

//import java.awt.geom.Point2D;


import com.util.geometry.Point2D;
import com.util.geometry.Polygon2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

;

public class Geometry {

    public static double calculateDistance(PDV pdv, double x1, double y1) {
        Point2D addressPoint = getAddressPoint(pdv.getAddress());
        Point2D locationPoint = new Point2D( x1, y1);

        double xDistance = Math.abs(addressPoint.getX() - locationPoint.getX());
        double yDistance = Math.abs(addressPoint.getY() - locationPoint.getY());

        return Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
    }

    private static Point2D getAddressPoint(Address address) {
        return new Point2D(address.getCoordinates().get(0), address.getCoordinates().get(1));
    }

    public static List<PDV> sortPDVsByDistance(List<PDV> pdvs, double lng, double lat) {
        List<PDV> sortedPDVs = new ArrayList<>(pdvs);
        List<Double> distances = new ArrayList<>();

        // Calculate all distances
        for(int i = 0; i < sortedPDVs.size(); i++) {
            distances.add(calculateDistance(sortedPDVs.get(i), lng, lat));
        }

        // Sort pdv list
        for(int i = 0; i < (sortedPDVs.size() - 1); i++) {
            double currentDistance = distances.get(i);
            int tradeWith = i;

            for(int j = (i+1); j< sortedPDVs.size(); j++) {
                if(currentDistance > distances.get(j)) {
                    tradeWith = j;
                    currentDistance = distances.get(j);
                }
            }

            // Trade if tradeWith variable is different than the current index
            if(tradeWith != i) {
                // Trade distances
                Collections.swap(distances, i, tradeWith);
                Collections.swap(sortedPDVs, i, tradeWith);
            }
        }

        return sortedPDVs;
    }

    public static boolean isPointInPDV(PDV pdv, Point2D point) {
        List<List<List<List<Double>>>> multiPolygonCoordinatesList = pdv.getCoverageArea().getCoordinates();

        for(List<List<List<Double>>> currentPolygon : multiPolygonCoordinatesList) { // Iterates through polygons in Multipolygon
            if(isPointInPolygon(currentPolygon, point))
                return true;
        }

        return false;
    }

    public static boolean isPointInPolygon(List<List<List<Double>>> polygonList, Point2D point) {
        int innerLayer = 0;
        boolean isInside = false;

        for(List<List<Double>> currentLayerList: polygonList) {
            if(isPointInPolygonLayer(currentLayerList, point)) {
                if(innerLayer == 0) {
                    isInside = true;    // Keep looping because if point is inside inner layer of polygon it isnt inside it
                }
                else {
                    return false;
                }
            }
            else if(innerLayer == 0) {
                return false;
            }

            innerLayer++;
        }

        return isInside;
    }

    public static boolean isPointInPolygonLayer(List<List<Double>> polygonLayerList, Point2D point) {
        Polygon2D polygon2D = convertListToPolygon(polygonLayerList);
        return polygon2D.contains(point);
    }

    public static Polygon2D convertListToPolygon(List<List<Double>> polygonList) {
        Polygon2D polygon2D = new Polygon2D();

        for(List<Double> pointList: polygonList) {
            Point2D newPoint = new Point2D(pointList.get(0), pointList.get(1));
            polygon2D.addPoint(newPoint);
        }

        return polygon2D;
    }
}
