package com.example.health.widgets;

import android.content.Context;
import android.util.AttributeSet;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.example.health.R;
import java.util.List;

public class ExerciseMapView extends TextureMapView {
    private AMap aMap;
    public Marker positionMarker;
    public Marker directionMarker;
    // 添加地图可见性控制方法
    public void setMapVisibility(int visibility) {
        setVisibility(visibility);
    }

    public ExerciseMapView(Context context) {
        this(context, null);
    }

    public ExerciseMapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExerciseMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initMapView(context);
    }

    private void initMapView(Context context) {
        // 必须调用父类的onCreate方法
        super.onCreate(null);

        // 高德地图10.0.600版本获取地图对象的方式
        try {
            aMap = getMap();
            setupMapSettings();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupMapSettings() {
        if (aMap != null) {
            UiSettings settings = aMap.getUiSettings();
            settings.setZoomControlsEnabled(false);
            settings.setCompassEnabled(false);
            settings.setMyLocationButtonEnabled(false);
            settings.setScrollGesturesEnabled(true);
            settings.setZoomGesturesEnabled(true);
        }
    }

    public void updatePosition(LatLng position, float direction) {
        if (aMap == null || position == null) return;

        try {
            // 更新位置标记
            if (positionMarker == null) {
                MarkerOptions positionOptions = new MarkerOptions()
                        .position(position)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.runner_position));
                positionMarker = aMap.addMarker(positionOptions);
            } else {
                positionMarker.setPosition(position);
            }

            // 更新方向标记
            if (directionMarker == null) {
                MarkerOptions directionOptions = new MarkerOptions()
                        .position(position)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.direction_arrow))
                        .rotateAngle(direction);
                directionMarker = aMap.addMarker(directionOptions);
            } else {
                directionMarker.setPosition(position);
                directionMarker.setRotateAngle(direction);
            }

            // 移动相机到当前位置
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 18f));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawPath(List<LatLng> pathPoints) {
        if (aMap == null || pathPoints == null || pathPoints.size() < 2)
            return;

        try {
            aMap.clear();
            positionMarker = null;
            directionMarker = null;

            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(pathPoints)
                    .width(10f)
                    .color(0xff0099ff);

            aMap.addPolyline(polylineOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            super.onResume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        try {
            super.onPause();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDetachedFromWindow();
    }

    public void release() {
        try {
            if (aMap != null) {
                aMap.clear();
            }
            super.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}