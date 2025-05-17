package com.example.health.sensor;

import android.content.Context;
import android.location.Location;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;

public class LocationTracker implements AMapLocationListener {
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationOption;
    private List<LatLng> pathPoints = new ArrayList<>();
    private LocationUpdateListener listener;

    public interface LocationUpdateListener {
        void onLocationChanged(LatLng newPoint, float speed, float direction);
        void onError(int errorCode, String errorInfo);
    }

    public LocationTracker(Context context, LocationUpdateListener listener) {
        this.listener = listener;
        initLocation(context);
    }

    private void initLocation(Context context) {
        try {
            locationClient = new AMapLocationClient(context.getApplicationContext());
            locationOption = new AMapLocationClientOption();

            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            locationOption.setInterval(2000);
            locationOption.setNeedAddress(false);
            locationOption.setLocationCacheEnable(false);

            locationClient.setLocationOption(locationOption);
            locationClient.setLocationListener(this);
        } catch (Exception e) {
            if (listener != null) {
                listener.onError(-1, "Location client init failed: " + e.getMessage());
            }
        }
    }

    public void startTracking() {
        if (locationClient != null) {
            pathPoints.clear();
            try {
                locationClient.startLocation();
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError(-2, "Start tracking failed: " + e.getMessage());
                }
            }
        }
    }

    public void stopTracking() {
        if (locationClient != null) {
            try {
                locationClient.stopLocation();
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError(-3, "Stop tracking failed: " + e.getMessage());
                }
            }
        }
    }

    public List<LatLng> getPathPoints() {
        return new ArrayList<>(pathPoints);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            LatLng point = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            pathPoints.add(point);

            if (listener != null) {
                listener.onLocationChanged(
                        point,
                        aMapLocation.getSpeed(),
                        aMapLocation.getBearing()
                );
            }
        } else if (listener != null) {
            listener.onError(
                    aMapLocation != null ? aMapLocation.getErrorCode() : -1,
                    aMapLocation != null ? aMapLocation.getErrorInfo() : "Unknown error"
            );
        }
    }

    public void release() {
        if (locationClient != null) {
            try {
                locationClient.stopLocation();
                locationClient.onDestroy();
            } catch (Exception e) {
                // Ignore destroy errors
            }
        }
    }
}