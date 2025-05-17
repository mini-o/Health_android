// ExerciseViewModel.java
package com.example.health.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import com.amap.api.maps.model.LatLng;
import com.example.health.database.entity.ExerciseRecordEntity;
import com.example.health.model.enums.ExerciseType;
import com.example.health.model.pojo.ExerciseRecord;
import com.example.health.model.repository.ExerciseRepository;
import com.example.health.sensor.LocationTracker;
import com.example.health.utils.ExerciseUtils;
import com.example.health.utils.MapUtils;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExerciseViewModel extends AndroidViewModel {
    private final ExerciseRepository repository;
    private final LocationTracker locationTracker;

    private MutableLiveData<ExerciseRecord> currentExercise = new MutableLiveData<>();
    private MutableLiveData<List<LatLng>> pathPoints = new MutableLiveData<>();
    private MutableLiveData<Boolean> isOutdoor = new MutableLiveData<>(true);
    private MutableLiveData<LatLng> currentPosition = new MutableLiveData<>();
    private final MutableLiveData<Float> currentDirection = new MutableLiveData<>(0f);

    public LiveData<Float> getCurrentDirection() {
        return currentDirection;
    }

    public LiveData<LatLng> getCurrentPosition() {
        return currentPosition;
    }

    public ExerciseViewModel(@NonNull Application application) {
        super(application);
        repository = new ExerciseRepository(application);
        locationTracker = new LocationTracker(application, new LocationListenerImpl());
    }

    public LiveData<ExerciseRecord> getCurrentExercise() {
        return currentExercise;
    }

    public LiveData<List<LatLng>> getPathPoints() {
        return pathPoints;
    }

    public LiveData<List<ExerciseRecordEntity>> getLatestRecords() {
        return repository.getLatestRecords(4);
    }

    public LiveData<List<ExerciseRecordEntity>> getAllRecords() {
        return repository.getAllRecords();
    }

    public LiveData<Float> getTotalDistance() {
        return repository.getTotalDistance();
    }

    public LiveData<HallOfFameData> getHallOfFameData() {
        MediatorLiveData<HallOfFameData> mediator = new MediatorLiveData<>();
        HallOfFameData data = new HallOfFameData();

        mediator.addSource(repository.getMaxDistance(), distance -> {
            data.setMaxDistance(distance != null ? distance : 0f);
            mediator.setValue(data);
        });

        mediator.addSource(repository.getMinPace(), pace -> {
            data.setBestPace(pace != null ? pace : 0f);
            mediator.setValue(data);
        });

        mediator.addSource(repository.getMaxDuration(), duration -> {
            data.setLongestDuration(duration != null ? duration : 0L);
            mediator.setValue(data);
        });

        return mediator;
    }

    public LiveData<Boolean> isOutdoor() {
        return isOutdoor;
    }

    public void selectExerciseType(boolean isOutdoor) {
        this.isOutdoor.setValue(isOutdoor);
    }

    public void startExercise() {
        ExerciseType type = isOutdoor.getValue() ?
                ExerciseType.OUTDOOR_RUNNING : ExerciseType.INDOOR_RUNNING;

        ExerciseRecord record = new ExerciseRecord();
        record.setExerciseType(type);
        record.setStartTime(new Date());
        record.setRunning(true);
        currentExercise.setValue(record);

        if (type == ExerciseType.OUTDOOR_RUNNING) {
            locationTracker.startTracking();
            pathPoints.setValue(new ArrayList<>());
        }
    }

    public void pauseExercise() {
        ExerciseRecord record = currentExercise.getValue();
        if (record != null) {
            record.setRunning(false);
            currentExercise.setValue(record);
            if (record.getExerciseType() == ExerciseType.OUTDOOR_RUNNING) {
                locationTracker.stopTracking();
            }
        }
    }

    public void resumeExercise() {
        ExerciseRecord record = currentExercise.getValue();
        if (record != null) {
            record.setRunning(true);
            currentExercise.setValue(record);
            if (record.getExerciseType() == ExerciseType.OUTDOOR_RUNNING) {
                locationTracker.startTracking();
            }
        }
    }

    public void stopExercise() {
        ExerciseRecord record = currentExercise.getValue();
        if (record != null) {
            record.setEndTime(new Date());
            record.setRunning(false);

            long duration = record.getEndTime().getTime() - record.getStartTime().getTime();
            record.setDuration(duration);

            if (record.getExerciseType() == ExerciseType.OUTDOOR_RUNNING) {
                List<LatLng> points = pathPoints.getValue();
                float distance = MapUtils.calculateDistance(points);
                record.setDistance(distance);
                record.setAveragePace(ExerciseUtils.calculatePace(distance, duration));
                record.setPathData(new Gson().toJson(points));
            }

            repository.saveExercise(record);
            currentExercise.setValue(null);
            locationTracker.stopTracking();
            pathPoints.setValue(null);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        locationTracker.release();
    }

    private class LocationListenerImpl implements LocationTracker.LocationUpdateListener {
        @Override
        public void onLocationChanged(LatLng newPoint, float speed, float direction) {
            ExerciseRecord record = currentExercise.getValue();
            currentDirection.postValue(direction);
            if (record != null && record.isRunning()) {
                List<LatLng> points = pathPoints.getValue();
                if (points == null) points = new ArrayList<>();
                points.add(newPoint);
                pathPoints.postValue(points);

                long duration = System.currentTimeMillis() - record.getStartTime().getTime();
                float distance = MapUtils.calculateDistance(points);
                float pace = ExerciseUtils.calculatePace(distance, duration);

                record.setDistance(distance);
                record.setDuration(duration);
                record.setAveragePace(pace);
                currentExercise.postValue(record);
            }
        }

        @Override
        public void onError(int errorCode, String errorInfo) {
            // Handle error
        }
    }

    public static class HallOfFameData {
        private float maxDistance;
        private float bestPace;
        private long longestDuration;

        public float getMaxDistance() { return maxDistance; }
        public void setMaxDistance(float maxDistance) { this.maxDistance = maxDistance; }

        public float getBestPace() { return bestPace; }
        public void setBestPace(float bestPace) { this.bestPace = bestPace; }

        public long getLongestDuration() { return longestDuration; }
        public void setLongestDuration(long longestDuration) { this.longestDuration = longestDuration; }
    }

    public enum NavigationEvent {
        EXERCISE_RECORD, RUNNING, HEALTH, EXERCISE, PROFILE
    }

    private final MutableLiveData<ExerciseViewModel.NavigationEvent> navigationEvent = new MutableLiveData<>();

    public LiveData<ExerciseViewModel.NavigationEvent> getNavigationEvent() {
        return navigationEvent;
    }

    public void navigateToExerciseRecord() {
        navigationEvent.setValue(NavigationEvent.EXERCISE_RECORD);
    }

    public void navigateToRunning() {
        navigationEvent.setValue(NavigationEvent.RUNNING);
        startExercise();
    }

    public void navigateToExercise() {
        navigationEvent.setValue(ExerciseViewModel.NavigationEvent.EXERCISE);
    }

    public void navigateToHealth() {
        navigationEvent.setValue(ExerciseViewModel.NavigationEvent.HEALTH);
        stopExercise();
    }

    public void navigateToProfile() {
        navigationEvent.setValue(ExerciseViewModel.NavigationEvent.PROFILE);
    }

    // 重置导航事件
    public void onNavigationComplete() {
        navigationEvent.setValue(null);
    }
}