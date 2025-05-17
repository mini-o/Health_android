package com.example.health.utils;

import android.location.Location;
import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import java.util.List;

public class MapUtils {
    public static void drawPath(AMap aMap, List<LatLng> points) {
        if (aMap == null || points == null || points.size() < 2) return;

        aMap.addPolyline(new PolylineOptions()
                .addAll(points)
                .width(10f)
                .color(0xff0099ff));
    }

    public static float calculateDistance(List<LatLng> points) {
        if (points == null || points.size() < 2) return 0;

        float totalDistance = 0;
        for (int i = 1; i < points.size(); i++) {
            totalDistance += calculateDistance(points.get(i-1), points.get(i));
        }
        return totalDistance;
    }

    private static float calculateDistance(LatLng start, LatLng end) {
        float[] results = new float[1];
        Location.distanceBetween(
                start.latitude, start.longitude,
                end.latitude, end.longitude,
                results
        );
        return results[0] / 1000f; // 转换为公里
    }
}