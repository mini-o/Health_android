package com.example.health.binding;

import android.location.Location;
import android.view.View;
import android.widget.ImageView;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.model.LatLng;
import com.example.health.R;
import com.example.health.adapter.ExerciseRecordAdapter;
import com.example.health.model.enums.ExerciseType;
import com.example.health.model.pojo.ExerciseRecord;
import com.example.health.widgets.ExerciseMapView;

import java.util.List;

public class ExerciseBindings {
    @BindingAdapter("exerciseTypeIcon")
    public static void setExerciseTypeIcon(ImageView view, ExerciseType type) {
        if (type == null) return;

        switch (type) {
            case OUTDOOR_RUNNING:
                view.setImageResource(R.drawable.outdoor);
                break;
            case INDOOR_RUNNING:
                view.setImageResource(R.drawable.indoor);
                break;
            default:
                view.setImageResource(R.drawable.ic_default_sport);
        }
    }

    @BindingAdapter("recordsData")
    public static void setRecordsData(RecyclerView recyclerView, List<ExerciseRecord> records) {
        if (recyclerView.getAdapter() instanceof ExerciseRecordAdapter) {
            ((ExerciseRecordAdapter) recyclerView.getAdapter()).updateData(records);
        }
    }

    @BindingAdapter("pathData")
    public static void setPathData(ExerciseMapView mapView, List<LatLng> points) {
        if (points != null) {
            mapView.drawPath(points);
        }
    }

    @BindingAdapter("currentPosition")
    public static void updatePosition(ExerciseMapView mapView, Location location) {
        if (location != null) {
            mapView.updatePosition(
                    new LatLng(location.getLatitude(), location.getLongitude()),
                    location.getBearing()
            );
        }
    }

    @BindingAdapter("exerciseStateIcon")
    public static void setExerciseStateIcon(ImageView view, Boolean isRunning) {
        if (isRunning == null) return;

        view.setImageResource(isRunning ?
                R.drawable.pause : R.drawable.start);
    }

    @BindingAdapter("mapVisibility")
    public static void setMapVisibility(View mapView, ExerciseType type) {
        if (type == null) return;
        mapView.setVisibility(type == ExerciseType.OUTDOOR_RUNNING ?
                View.VISIBLE : View.GONE);
    }
}