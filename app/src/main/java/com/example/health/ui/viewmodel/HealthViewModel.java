package com.example.health.ui.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.health.database.entity.HealthDataEntity;
import com.example.health.model.DateRange;
import com.example.health.model.TimeRange;
import com.example.health.model.enums.MotionType;
import com.example.health.model.enums.TimeRangeType;
import com.example.health.model.pojo.HealthBluetoothDevice;
import com.example.health.model.pojo.HealthData;
import com.example.health.model.repository.HealthRepository;
import com.example.health.sensor.BluetoothManager;
import com.example.health.sensor.BreathingDetector;
import com.example.health.sensor.SensorBackgroundService;
import com.example.health.sensor.SpeedDetector;
import com.example.health.sensor.StepDetector;
import com.example.health.utils.DateUtils;
import com.example.health.model.pojo.DailySummary;
import com.example.health.model.pojo.HourlySummary;
import com.example.health.model.pojo.MonthlySummary;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class HealthViewModel extends ViewModel {
    // 实时数据
    public final MutableLiveData<Integer> steps = new MutableLiveData<>(0);
    public final MutableLiveData<Float> speed = new MutableLiveData<>(0f);
    public final MutableLiveData<Integer> breathingRate = new MutableLiveData<>(0);
    public final MutableLiveData<Integer> exerciseDuration = new MutableLiveData<>(0);

    private final MutableLiveData<TimeRangeType> timeRangeType = new MutableLiveData<>(TimeRangeType.DAY);
    private final MutableLiveData<DateRange> dateRange = new MutableLiveData<>();
    public final MutableLiveData<List<Entry>> dailyStepsData = new MutableLiveData<>();
    public final MutableLiveData<List<Entry>> dailyBreathingData = new MutableLiveData<>();
    public final MutableLiveData<List<Entry>> dailyExerciseData = new MutableLiveData<>();
    public final MutableLiveData<List<Entry>> dailySpeedData = new MutableLiveData<>();

    public final MutableLiveData<List<BarEntry>> stepsChartData = new MutableLiveData<>(new ArrayList<>());
    public final MutableLiveData<List<BarEntry>> distanceChartData = new MutableLiveData<>(new ArrayList<>());
    public final MutableLiveData<List<BarEntry>> durationChartData = new MutableLiveData<>(new ArrayList<>());
    public final MutableLiveData<List<BarEntry>> countChartData = new MutableLiveData<>(new ArrayList<>());

    // 蓝牙数据
    public final MutableLiveData<Integer> heartRate = new MutableLiveData<>();
    public final MutableLiveData<String> bloodOxygen = new MutableLiveData<>();
    public final MutableLiveData<String> bloodPressure = new MutableLiveData<>();
    public final MutableLiveData<String> sleep = new MutableLiveData<>();
    public final MutableLiveData<String> bloodSugar = new MutableLiveData<>();
    public final MutableLiveData<String> temperature = new MutableLiveData<>();

    // 统计数据
    public final MutableLiveData<Float> totalDistance = new MutableLiveData<>(0f);
    public final MutableLiveData<Integer> totalExerciseTime = new MutableLiveData<>(0);
    public final MutableLiveData<Integer> totalExerciseCount = new MutableLiveData<>(0);

    // 时间范围
    public final MutableLiveData<Date> selectedDate = new MutableLiveData<>(new Date());
    public final MutableLiveData<TimeRangeType> rangeType = new MutableLiveData<>(TimeRangeType.DAY);
    public final MutableLiveData<DateRange> selectedRange = new MutableLiveData<>();

    // LiveData
    private final MutableLiveData<HealthData> currentHealthData = new MutableLiveData<>();
    private final HealthRepository repository;
    private BluetoothManager bluetoothManager;

    private final StepDetector stepDetector;
    private final SpeedDetector speedDetector;
    private final BreathingDetector breathingDetector;
    private final Application application;
    private final MutableLiveData<List<HealthBluetoothDevice>> discoveredDevices = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> connectedDevice = new MutableLiveData<>("未连接");
    private final MutableLiveData<String> bluetoothStatus = new MutableLiveData<>("就绪");
    private final MutableLiveData<String> lastError = new MutableLiveData<>("");

    public LiveData<List<Entry>> getDailyStepsData() { return dailyStepsData; }
    public LiveData<List<Entry>> getDailyBreathingData() { return dailyBreathingData; }
    public LiveData<List<Entry>> getDailyExerciseData() { return dailyExerciseData; }
    public LiveData<List<Entry>> getDailySpeedData() { return dailySpeedData; }

    private final BluetoothManager.BluetoothListener bluetoothListener =
            new BluetoothManager.BluetoothListener() {
                @Override
                public void onDeviceDiscovered(HealthBluetoothDevice device) {
                    // 去重处理并更新设备列表
                    List<HealthBluetoothDevice> currentList = discoveredDevices.getValue();
                    if (currentList == null) {
                        currentList = new ArrayList<>();
                    }

                    // 使用Set去重
                    Set<HealthBluetoothDevice> deviceSet = new LinkedHashSet<>(currentList);
                    deviceSet.add(device);

                    // 转换为有序列表
                    List<HealthBluetoothDevice> newList = new ArrayList<>(deviceSet);

                    // 按信号强度排序（需要设备类添加RSSI字段）
                    Collections.sort(newList, (d1, d2) ->
                            Integer.compare(d2.getRssi(), d1.getRssi()));

                    discoveredDevices.postValue(newList);
                }

                @Override
                public void onDeviceConnected(HealthBluetoothDevice device) {
                    // 更新连接状态
                    connectedDevice.postValue(device.getName());
                    bluetoothStatus.postValue("已连接: " + device.getName());

                    // 停止扫描以节省电量
                    bluetoothManager.stopDiscovery();
                }

                @Override
                public void onDeviceDisconnected(HealthBluetoothDevice device) {
                    // 更新连接状态
                    connectedDevice.postValue("未连接");
                    bluetoothStatus.postValue("连接断开");

                    // 自动重新开始扫描
                    bluetoothManager.startDiscovery();

                    // 显示断开提示
                    showToast(application, "设备已断开: " + device.getName());
                }

        @Override
        public void onHeartRateReceived(int heartRate) {
            HealthViewModel.this.heartRate.postValue(heartRate);
        }

        @Override
        public void onBloodOxygenReceived(float spo2) {
            bloodOxygen.postValue(String.format(Locale.getDefault(), "%.1f%%", spo2));
        }

        @Override
        public void onBloodPressureReceived(String systolic) {
            bloodPressure.postValue(systolic);
        }

        @Override
        public void onTemperatureReceived(float temperature) {
            HealthViewModel.this.temperature.postValue(String.format(Locale.getDefault(), "%.1f℃", temperature));
        }

        @Override
        public void onBloodSugarReceived(float glucose) {
            bloodSugar.postValue(String.format(Locale.getDefault(), "%.1fmmol/L", glucose));
        }

        @Override
        public void onSleepDataReceived(String sleepData) {
            sleep.postValue(sleepData);
        }

        @Override
        public void onError(String message) {
            // 更新错误状态
            lastError.postValue(message);

            // 显示错误提示（使用Application上下文）
            showToast(application, "蓝牙错误: " + message);
        }
    };

    // 在工具类中添加Toast显示方法
    private static void showToast(Context context, String message) {
        if (context != null) {
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            );
        }
    }

    public HealthViewModel(HealthRepository healthRepository, BluetoothManager bluetoothManager, Application application) {
        this.repository = healthRepository;
        this.bluetoothManager = bluetoothManager;
        this.application = application;

        // 初始化传感器（修正 application 参数）
        stepDetector = new StepDetector();
        speedDetector = new SpeedDetector();
        breathingDetector = new BreathingDetector(stepDetector, application);

        this.bluetoothManager.setListener(bluetoothListener);
        // 初始化默认日期
        Date today = new Date();
        selectedDate.setValue(today);
        updateDateRange(today, TimeRangeType.DAY);
        // 监听传感器数据变化
    }

    // 添加传感器控制方法
    public void startSensors(Activity activity) {
        stepDetector.start(activity);
        speedDetector.start(activity);
        breathingDetector.startMonitoring(activity);
    }

    public void stopSensors() {
        stepDetector.stop();
        speedDetector.stop();
        breathingDetector.stopMonitoring();
    }

    // 添加 ViewModel 生命周期管理
    @Override
    protected void onCleared() {
        super.onCleared();
        stopSensors();
        cleanupBluetooth();
    }

    public void cleanupBluetooth() {
        bluetoothManager.cleanup();
    }

    // 添加后台服务启动方法
    public void startBackgroundMonitoring(Context context) {
        Intent serviceIntent = new Intent(context, SensorBackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }

    // 暴露枚举类型的 LiveData
    public LiveData<TimeRangeType> getTimeRangeType() {
        return timeRangeType;
    }

    // 修改设置方法使用枚举类型
    public void setTimeRangeType(TimeRangeType type) {
        timeRangeType.setValue(type);
        updateDateRangeByType(type);
        loadSummaryData();
    }

    private void updateDateRangeByType(TimeRangeType type) {
        Date today = new Date();
        switch (type) {
            case DAY:
                dateRange.setValue(new DateRange(
                        DateUtils.getStartOfDay(today),
                        DateUtils.getEndOfDay(today)
                ));
                break;
            case WEEK:
                dateRange.setValue(new DateRange(
                        DateUtils.getStartOfWeek(today),
                        DateUtils.getEndOfWeek(today)
                ));
                break;
            case MONTH:
                dateRange.setValue(new DateRange(
                        DateUtils.getStartOfMonth(today),
                        DateUtils.getEndOfMonth(today)
                ));
                break;
            case YEAR:
                dateRange.setValue(new DateRange(
                        DateUtils.getStartOfYear(today),
                        DateUtils.getEndOfYear(today)
                ));
        }
    }

    // 处理日期选择器范围变化
    public void setDateRange(Date startDate, Date endDate) {
        dateRange.setValue(new DateRange(startDate, endDate));
        loadSummaryData();
    }

    // 加载汇总数据
    public void loadSummaryData() {
        DateRange range = dateRange.getValue();
        TimeRangeType type = timeRangeType.getValue();

        if (range == null || type == null) return;

        switch (type) {
            case DAY:
                loadDailyData(range);
                break;
            case WEEK:
                loadWeeklyData(range);
                break;
            case MONTH:
                loadMonthlyData(range);
                break;
            case YEAR:
                loadYearlyData(range);
        }
    }

    // 日粒度数据加载
    private void loadDailyData(DateRange range) {
        if (range == null || range.getStartDate() == null || range.getEndDate() == null) return;

        repository.getDailyHourlySummary( // 修改为正确的方法名
                range.getStartDate().getTime(),
                range.getEndDate().getTime()
        ).observeForever(hourlySummaries -> {
            if (hourlySummaries == null || hourlySummaries.isEmpty()) {
                resetChartData();
                return;
            }

            processHourlySummaries(hourlySummaries);
        });
    }

    private void processHourlySummaries(List<HourlySummary> summaries) {
        List<BarEntry> stepsEntries = new ArrayList<>();
        List<BarEntry> distanceEntries = new ArrayList<>();
        List<BarEntry> durationEntries = new ArrayList<>();
        List<BarEntry> countEntries = new ArrayList<>();

        for (HourlySummary summary : summaries) {
            // 修复时间戳转换
            float xValue = DateUtils.getHourFromTimestamp(
                    summary.getHour() // 添加.getTime()
            );

            stepsEntries.add(new BarEntry(xValue, summary.getSteps()));
            distanceEntries.add(new BarEntry(xValue, summary.getDistance()));
            durationEntries.add(new BarEntry(xValue, summary.getDuration()));
            countEntries.add(new BarEntry(xValue, summary.getCount()));
        }

        stepsChartData.postValue(stepsEntries);
        distanceChartData.postValue(distanceEntries);
        durationChartData.postValue(durationEntries);
        countChartData.postValue(countEntries);
    }

    private void resetChartData() {
        stepsChartData.postValue(Collections.emptyList());
        distanceChartData.postValue(Collections.emptyList());
        durationChartData.postValue(Collections.emptyList());
        countChartData.postValue(Collections.emptyList());
    }

    // 周粒度数据加载
    private void loadWeeklyData(DateRange range) {
        if (range == null || range.getStartDate() == null || range.getEndDate() == null) return;

        repository.getWeeklyDailySummary(
                range.getStartDate().getTime(),
                range.getEndDate().getTime()
        ).observeForever(weeklySummaries -> {
            if (weeklySummaries == null || weeklySummaries.isEmpty()) {
                resetChartData();
                return;
            }

            List<BarEntry> stepsEntries = new ArrayList<>();
            List<BarEntry> distanceEntries = new ArrayList<>();
            List<BarEntry> durationEntries = new ArrayList<>();
            List<BarEntry> countEntries = new ArrayList<>();

            for (DailySummary summary : weeklySummaries) {
                // 将日期转换为周内天数 (0=周日, 1=周一...6=周六)
                float xValue = DateUtils.getDayOfWeekFromDate(summary.getDate());

                stepsEntries.add(new BarEntry(xValue, summary.getSteps()));
                distanceEntries.add(new BarEntry(xValue, summary.getDistance()));
                durationEntries.add(new BarEntry(xValue, summary.getDuration()));
                countEntries.add(new BarEntry(xValue, summary.getCount()));
            }

            stepsChartData.postValue(stepsEntries);
            distanceChartData.postValue(distanceEntries);
            durationChartData.postValue(durationEntries);
            countChartData.postValue(countEntries);
        });
    }

    // 月粒度数据加载
    private void loadMonthlyData(DateRange range) {
        if (range == null || range.getStartDate() == null || range.getEndDate() == null) return;

        repository.getMonthlyDailySummary(
                range.getStartDate().getTime(),
                range.getEndDate().getTime()
        ).observeForever(monthlySummaries -> {
            if (monthlySummaries == null || monthlySummaries.isEmpty()) {
                resetChartData();
                return;
            }

            List<BarEntry> stepsEntries = new ArrayList<>();
            List<BarEntry> distanceEntries = new ArrayList<>();
            List<BarEntry> durationEntries = new ArrayList<>();
            List<BarEntry> countEntries = new ArrayList<>();

            for (DailySummary summary : monthlySummaries) {
                // 将日期字符串转换为月份中的天数 (1-31)
                float xValue = DateUtils.getDayOfMonthFromDateString(summary.getDate());

                stepsEntries.add(new BarEntry(xValue, summary.getSteps()));
                distanceEntries.add(new BarEntry(xValue, summary.getDistance()));
                durationEntries.add(new BarEntry(xValue, summary.getDuration()));
                countEntries.add(new BarEntry(xValue, summary.getCount()));
            }

            stepsChartData.postValue(stepsEntries);
            distanceChartData.postValue(distanceEntries);
            durationChartData.postValue(durationEntries);
            countChartData.postValue(countEntries);
        });
    }

    // 年粒度数据加载
    private void loadYearlyData(DateRange range) {
        if (range == null || range.getStartDate() == null || range.getEndDate() == null) return;

        repository.getYearlyMonthlySummary(
                range.getStartDate().getTime(),
                range.getEndDate().getTime()
        ).observeForever(yearlySummaries -> {
            if (yearlySummaries == null || yearlySummaries.isEmpty()) {
                resetChartData();
                return;
            }

            List<BarEntry> stepsEntries = new ArrayList<>();
            List<BarEntry> distanceEntries = new ArrayList<>();
            List<BarEntry> durationEntries = new ArrayList<>();
            List<BarEntry> countEntries = new ArrayList<>();

            for (MonthlySummary summary : yearlySummaries) {
                // 将月份字符串转换为数字 (1-12)
                // 使用 getMonth() 方法获取月份字符串
                String monthStr = summary.getMonth();
                float xValue = DateUtils.getMonthNumberFromString(monthStr);

                stepsEntries.add(new BarEntry(xValue, summary.getSteps()));
                distanceEntries.add(new BarEntry(xValue, summary.getDistance()));
                durationEntries.add(new BarEntry(xValue, summary.getDuration()));
                countEntries.add(new BarEntry(xValue, summary.getCount()));
            }

            stepsChartData.postValue(stepsEntries);
            distanceChartData.postValue(distanceEntries);
            durationChartData.postValue(durationEntries);
            countChartData.postValue(countEntries);
        });
    }

    // 日期范围格式化（供布局显示）
    public String getFormattedDateRange() {
        DateRange range = dateRange.getValue();
        TimeRangeType type = timeRangeType.getValue();

        if (range == null || type == null) return "";

        switch (type) {
            case DAY:
                return DateUtils.formatDayRange(range.getStartDate());
            case WEEK:
                return DateUtils.formatWeekRange(range.getStartDate());
            case MONTH:
                return DateUtils.formatMonthRange(range.getStartDate());
            case YEAR:
                return DateUtils.formatYearRange(range.getStartDate());
            default:
                return "";
        }
    }

    public void loadDailyData() {
        Date date = selectedDate.getValue();
        if (date == null) return;

        long start = DateUtils.getStartOfDay(date).getTime();
        long end = DateUtils.getEndOfDay(date).getTime();

        repository.getDataByDateRange(start, end).observeForever(entities -> {
            List<Entry> stepsEntries = new ArrayList<>();
            List<Entry> breathingEntries = new ArrayList<>();
            List<Entry> exerciseEntries = new ArrayList<>();
            List<Entry> speedEntries = new ArrayList<>();

            for (HealthDataEntity entity : entities) {
                float hour = (entity.getTimestamp() - start) / (1000f * 3600); // 转换为小时
                stepsEntries.add(new Entry(hour, entity.getSteps()));
                breathingEntries.add(new Entry(hour, entity.getBreathingRate()));
                speedEntries.add(new Entry(hour, entity.getSpeed()));

                if (entity.getMotionType() == MotionType.RUNNING) { // 假设运动类型2是跑步
                    exerciseEntries.add(new Entry(hour, entity.getExerciseTime()));
                }
            }

            dailyStepsData.postValue(stepsEntries);
            dailyBreathingData.postValue(breathingEntries);
            dailyExerciseData.postValue(exerciseEntries);
            dailySpeedData.postValue(speedEntries);
        });
    }

    // 主界面数据
    public LiveData<Integer> getTodaySteps() {
        return repository.getTodaySteps();
    }

    public LiveData<Integer> getCurrentBreathingRate() {
        return repository.getCurrentBreathingRate();
    }

    public LiveData<Float> getCurrentSpeed() {
        return repository.getCurrentSpeed();
    }

    public LiveData<Integer> getTodayDuration() {
        return repository.getTodayExerciseDuration();
    }

    public LiveData<Float> getTotalDistance() {
        return repository.getTotalDistance();
    }

    public LiveData<Integer> getTotalExerciseCount() {
        return repository.getTotalExerciseCount();
    }

    public LiveData<Integer> getTotalExerciseDuration() {
        return repository.getTotalExerciseDuration();
    }

    // 每日数据界面
    public LiveData<List<HealthDataEntity>> getDailyLineChartData() {
        return Transformations.switchMap(selectedDate, date ->
                repository.getRawDataByDate(date)
        );
    }

    public LiveData<Integer> getSelectedDateSteps() {
        return Transformations.switchMap(selectedDate, date ->
                repository.getStepsByDate(date)
        );
    }

    public LiveData<Integer> getSelectedDateDuration() {
        return Transformations.switchMap(selectedDate, date ->
                repository.getExerciseDurationByDate(date)
        );
    }

    // 总数据界面
    public LiveData<List<HourlySummary>> getHourlySummary() {
        return Transformations.switchMap(selectedRange,
                range -> repository.getHourlySummary(range)
        );
    }

    public LiveData<List<DailySummary>> getDailySummary() {
        return Transformations.switchMap(selectedRange,
                range -> repository.getDailySummary(range)
        );
    }

    public LiveData<List<MonthlySummary>> getMonthlySummary() {
        return Transformations.switchMap(selectedRange,
                range -> repository.getMonthlySummary(range)
        );
    }

    public LiveData<Integer> getTotalSteps() {
        return Transformations.switchMap(selectedRange,
                range -> repository.getStepsByRange(range)
        );
    }

    public LiveData<Float> getTotalDistanceByRange() {
        return Transformations.switchMap(selectedRange,
                range -> repository.getDistanceByRange(range)
        );
    }

    public LiveData<Integer> getTotalDuration() {
        return Transformations.switchMap(selectedRange,
                range -> repository.getDurationByRange(range)
        );
    }

    public LiveData<Integer> getTotalExerciseCountByRange() {
        return Transformations.switchMap(selectedRange,
                range -> repository.getExerciseCountByRange(range)
        );
    }

    // 日期处理
    // 修改方法接收 Date 参数
    public void setSelectedDate(Date selectedDate) {
        this.selectedDate.setValue(selectedDate);
        loadDailyData(); // 自动触发数据加载
    }

    public LiveData<Date> getSelectedDate() {
        return selectedDate;
    }

    public void updateDateRange(Date date, TimeRangeType type) {
        DateRange range;
        switch (type) {
            case WEEK:
                range = new DateRange(
                        DateUtils.getStartOfWeek(date),
                        DateUtils.getEndOfWeek(date)
                );
                break;
            case MONTH:
                range = new DateRange(
                        DateUtils.getStartOfMonth(date),
                        DateUtils.getEndOfMonth(date)
                );
                break;
            case YEAR:
                range = new DateRange(
                        DateUtils.getStartOfYear(date),
                        DateUtils.getEndOfYear(date)
                );
                break;
            default: // DAY
                range = new DateRange(
                        DateUtils.getStartOfDay(date),
                        DateUtils.getEndOfDay(date)
                );
        }

        selectedRange.setValue(range);
        rangeType.setValue(type);
    }

    public LiveData<TimeRangeType> getRangeType() {
        return rangeType;
    }

    public enum NavigationEvent {
        DAILY_DATA, TOTAL_DATA, HEALTH, EXERCISE, PROFILE
    }

    private final MutableLiveData<NavigationEvent> navigationEvent = new MutableLiveData<>();

    public LiveData<NavigationEvent> getNavigationEvent() {
        return navigationEvent;
    }

    public void navigateToDailyData() {
        navigationEvent.setValue(NavigationEvent.DAILY_DATA);
    }

    public void navigateToTotalData() {
        navigationEvent.setValue(NavigationEvent.TOTAL_DATA);
    }

    public void navigateToExercise() {
        navigationEvent.setValue(NavigationEvent.EXERCISE);
    }

    public void navigateToHealth() {
        navigationEvent.setValue(NavigationEvent.HEALTH);
    }

    public void navigateToProfile() {
        navigationEvent.setValue(NavigationEvent.PROFILE);
    }

    // 重置导航事件
    public void onNavigationComplete() {
        navigationEvent.setValue(null);
    }
}