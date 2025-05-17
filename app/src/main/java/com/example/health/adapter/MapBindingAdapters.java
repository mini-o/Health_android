// MapBindingAdapters.java
package com.example.health.adapter;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import com.amap.api.maps.model.LatLng;
import com.example.health.widgets.ExerciseMapView;
import java.util.List;

public class MapBindingAdapters {

    @BindingAdapter("app:currentPosition")
    public static void bindCurrentPosition(
            ExerciseMapView mapView,
            LiveData<LatLng> positionLiveData
    ) {
        if (positionLiveData != null) {
            positionLiveData.observe((LifecycleOwner) mapView.getContext(), position -> {
                if (position != null) {
                    // 调用地图控件的原生方法
                    mapView.updatePosition(position, 0f); // 方向参数需要从其他LiveData获取
                }
            });
        }
    }

    @BindingAdapter("app:direction")
    public static void bindDirection(
            ExerciseMapView mapView,
            LiveData<Float> directionLiveData
    ) {
        if (directionLiveData != null) {
            directionLiveData.observe((LifecycleOwner) mapView.getContext(), direction -> {
                if (direction != null && mapView.positionMarker != null) {
                    mapView.directionMarker.setRotateAngle(direction);
                }
            });
        }
    }

    @BindingAdapter("app:pathData")
    public static void bindPathData(
            ExerciseMapView mapView,
            LiveData<List<LatLng>> pathLiveData
    ) {
        if (pathLiveData != null) {
            pathLiveData.observe((LifecycleOwner) mapView.getContext(), path -> {
                if (path != null && !path.isEmpty()) {
                    mapView.drawPath(path);
                }
            });
        }
    }
}