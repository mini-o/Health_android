package com.example.health.sensor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.health.database.HealthDatabase;
import com.example.health.database.dao.BreathBaselineDao;
import com.example.health.database.entity.BreathBaseline;
import com.example.health.model.enums.AnomalyLevel;
import com.example.health.model.enums.MotionType;
import com.example.health.model.pojo.HealthData;
import com.example.health.utils.PermissionUtils;
import com.example.health.utils.VibrationUtils;
import com.example.health.utils.SmsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

// 呼吸检测类
public class BreathingDetector {
    private static final String TAG = "BreathingDetector";
    private static final String PREFS_NAME = "BreathingDetectorPrefs";
    private static final String KEY_FIRST_RUN = "first_run";

    private static final int SAMPLE_RATE = 44100;    // 音频采样率
    private static final int FREQ_MIN = 18000;    // 最小频率
    private static final int FREQ_MAX = 22000;    // 最大频率
    // 音频记录的缓冲区大小
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

    // 基线持续时间（30秒）
    private static final long BASELINE_DURATION = 30000;
    // 单日基线最大变化比例（±10%）
    private static final double MAX_DAILY_CHANGE = 0.1;

    // 默认的基线数据，不同运动类型对应的呼吸基线
    private static final Map<Integer, Double> DEFAULT_BASELINES = new HashMap<Integer, Double>() {{
        put(0, 12.0); // 静止
        put(1, 18.0); // 步行
        put(2, 24.0); // 跑步
        put(3, 20.0); // 其他运动
    }};


    private AudioRecord audioRecord;    // 音频记录对象
    private AudioTrack audioTrack;    // 音频播放对象
    private boolean isMonitoring = false;    // 是否正在监测呼吸
    private boolean isBaselineReady = false;    // 基线是否准备好
    private boolean isFirstRun = true;    // 是否是首次运行
    private long baselineStartTime = 0;    // 基线开始时间

    private final Context context; // 添加上下文字段

    // 呼吸数据的LiveData，用于存储和通知数据变化
    private final MutableLiveData<HealthData> breathingData = new MutableLiveData<>();
    // 存储历史呼吸频率
    private LinkedList<Double> historyFreq = new LinkedList<>();
    // 步数检测器对象
    private final StepDetector stepDetector;
    // 呼吸基线数据访问对象
    private final BreathBaselineDao baselineDao;
    // 不同运动类型对应的呼吸基线数据
    private final Map<Integer, Double> motionBaselines = new HashMap<>();

    private int currentMotionType = 0;    // 当前的运动类型
    private double currentBaseline = 12.0;    // 当前的呼吸基线
    private double currentThreshold = 3.0;    // 当前的阈值
    private double dailyBaselineMin = 8.0;    // 每日呼吸基线的最小值
    private double dailyBaselineMax = 30.0;    // 每日呼吸基线的最大值

    // 数据库操作的线程池
    private final Executor dbExecutor = Executors.newSingleThreadExecutor();
    // 主线程Handler，用于在主线程执行操作
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // 呼吸数据变化的监听器接口
    public interface BreathingListener {
        void onBreathingDataUpdate(HealthData data);
        // 当基线测量有指令时调用，showProgress表示是否显示进度
        void onBaselineInstruction(String instruction, boolean showProgress);
        // 当检测到呼吸异常时调用，level表示异常等级，message表示异常信息
        void onAnomalyAlert(int level, String message);
    }
    private BreathingListener listener;

    public BreathingDetector(StepDetector stepDetector, Context context) {
        this.stepDetector = stepDetector;
        this.context = context; // 初始化上下文
        this.baselineDao = HealthDatabase.getDatabase(context).breathBaselineDao();
        // 从数据库加载呼吸基线数据
        loadBaselinesFromDB();

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.isFirstRun = prefs.getBoolean(KEY_FIRST_RUN, true);

        // 初始化呼吸数据
        HealthData initialData = new HealthData();
        initialData.setMotionType(MotionType.STILL);
        initialData.setBreathAnomaly(AnomalyLevel.NORMAL);
        breathingData.setValue(initialData);
    }

    public void setBreathingListener(BreathingListener listener) {
        this.listener = listener;
    }

    // 获取呼吸数据
    public LiveData<HealthData> getBreathingData() {
        return breathingData;
    }

    // 开始监测呼吸，传入Activity用于权限检查
    public void startMonitoring(Activity activity) {
        if (isMonitoring) return;

        // 检查音频权限
        if (!PermissionUtils.checkAudioPermission(activity)) {
            PermissionUtils.requestAudioPermission(activity);
            return;
        }

        // 创建音频播放对象
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                BUFFER_SIZE, AudioTrack.MODE_STREAM);

        // 创建音频记录对象
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);

        // 在新线程中执行监测操作
        new Thread(() -> {
            isMonitoring = true;
            short[] sweepSignal = generateSweepSignal();
            audioTrack.play();
            audioRecord.startRecording();

            if (isFirstRun) {
                // 开始首次运行的基线测量
                startFirstRunBaseline(activity);
            }

            // 当正在监测时循环处理音频数据
            while (isMonitoring) {
                audioTrack.write(sweepSignal, 0, sweepSignal.length);
                short[] buffer = new short[BUFFER_SIZE];
                int read = audioRecord.read(buffer, 0, BUFFER_SIZE);
                if (read > 0) {
                    processAudioData(buffer);
                }
            }
        }).start();
    }

    // 首次运行时开始基线测量，传入Activity用于显示提示
    private void startFirstRunBaseline(Activity activity) {
        // 设置基线开始时间
        baselineStartTime = System.currentTimeMillis();
        historyFreq.clear();
        isBaselineReady = false;

        // 在主线程更新UI提示
        mainHandler.post(() -> {
            if (listener != null) {
                listener.onBaselineInstruction("请保持静止30秒以测量基础呼吸频率", true);
            }
            Toast.makeText(activity, "首次使用需要测量基础呼吸频率，请保持静止", Toast.LENGTH_LONG).show();
        });

        // 延迟一段时间后检查基线测量是否完成
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // 如果历史呼吸频率列表中的数据足够
            if (historyFreq.size() >= 5) {
                finishBaselineSetup(0);
                saveFirstRunComplete(activity);
                // 在主线程更新UI提示
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onBaselineInstruction("基线测量完成！", false);
                    }
                    Toast.makeText(activity, "基线测量完成", Toast.LENGTH_SHORT).show();
                });
            } else {
                // 在主线程更新UI提示
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onBaselineInstruction("测量失败，请重试", false);
                    }
                    Toast.makeText(activity, "测量失败，数据不足", Toast.LENGTH_SHORT).show();
                });
            }
        }, BASELINE_DURATION);
    }

    // 保存首次运行已完成，传入Context用于保存标志
    private void saveFirstRunComplete(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_FIRST_RUN, false).apply();
        isFirstRun = false;
    }

    // 从数据库加载呼吸基线数据
    private void loadBaselinesFromDB() {
        dbExecutor.execute(() -> {
            List<BreathBaseline> baselines = baselineDao.getAllBaselinesSync();
            // 将数据库中的数据存入运动类型对应的基线数据map中
            for (BreathBaseline entity : baselines) {
                motionBaselines.put(entity.getMotionType().getValue(), entity.getBaselineValue());
            }
            // 将默认基线数据存入运动类型对应的基线数据map中
            for (Map.Entry<Integer, Double> entry : DEFAULT_BASELINES.entrySet()) {
                motionBaselines.putIfAbsent(entry.getKey(), entry.getValue());
            }

            // 设置当前呼吸基线为运动类型0对应的基线
            currentBaseline = motionBaselines.get(0);
            // 设置当前阈值
            currentThreshold = Math.max(currentBaseline * 0.2, 2.0);
        });
    }

    // 更新运动类型
    public void updateMotionType(MotionType motionType) {
        if (motionType.getValue() == currentMotionType) return;

        currentMotionType = motionType.getValue();
        // 获取新运动类型对应的呼吸基线
        Double newBaseline = motionBaselines.get(currentMotionType);
        if (newBaseline != null) {
            currentBaseline = newBaseline;
            currentThreshold = Math.max(currentBaseline * 0.2, 2.0);
            dailyBaselineMin = currentBaseline * 0.7;
            dailyBaselineMax = currentBaseline * 1.3;
        }

        // 如果新运动类型没有对应的基线
        if (!motionBaselines.containsKey(currentMotionType)) {
            // 开始动态基线测量
            startDynamicBaselineMeasurement(currentMotionType);
        }
    }

    // 开始动态基线测量的方法，传入运动类型
    private void startDynamicBaselineMeasurement(int motionType) {
        baselineStartTime = System.currentTimeMillis();
        historyFreq.clear();

        // 在主线程更新UI提示
        mainHandler.post(() -> {
            if (listener != null) {
                String motionName = getMotionName(motionType);
                listener.onBaselineInstruction("正在测量" + motionName + "模式的呼吸基线", true);
            }
        });

        // 延迟一段时间后检查动态基线测量是否完成
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (historyFreq.size() >= 10) {
                finishBaselineSetup(motionType);
                // 在主线程更新UI提示
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onBaselineInstruction(motionType + "模式基线测量完成", false);
                    }
                });
            }
        }, 60000);
    }

    // 完成基线设置的方法，传入运动类型
    private void finishBaselineSetup(int motionType) {
        // 计算历史呼吸频率的中位数作为新的基线
        double median = calculateMedian(historyFreq);
        // 将新的基线存入运动类型对应的基线数据map中
        motionBaselines.put(motionType, median);
        currentBaseline = median;
        currentThreshold = Math.max(median * 0.2, 2.0);
        isBaselineReady = true;

        BreathBaseline entity = new BreathBaseline();
        entity.setMotionType(MotionType.fromValue(motionType));
        entity.setBaselineValue(median);
        entity.setTimestamp(new Date());
        dbExecutor.execute(() -> baselineDao.insert(entity));
    }

    // 处理音频数据的方法，传入音频数据缓冲区
    private void processAudioData(short[] data) {
        // 对音频数据进行带通滤波
        double[] filtered = bandpassFilter(data, 200, 800);
        // 计算音频数据的希尔伯特包络
        double[] envelope = computeHilbertEnvelope(filtered);
        // 查找呼吸峰值
        List<Integer> breathPeaks = findBreathPeaks(envelope);
        // 计算呼吸频率
        double breathRate = calculateBreathRate(breathPeaks);

        // 如果基线未准备好
        if (!isBaselineReady) {
            // 处理基线测量数据
            handleBaselineMeasurement(breathRate);
            return;
        }

        // 如果当前运动类型为0或者步数检测器检测到未移动
        if (currentMotionType == 0 || !stepDetector.getIsMoving().getValue()) {
            // 更新动态基线
            updateDynamicBaseline(breathRate);
        }

        // 检查呼吸异常等级
        AnomalyLevel anomalyLevel = checkAnomaly(breathRate);

        // 获取当前的呼吸数据
        HealthData currentData = breathingData.getValue();
        // 如果当前呼吸数据不为空
        if (currentData != null) {
            currentData.setBreathingRate((int) Math.round(breathRate));
            currentData.setBreathAnomaly(anomalyLevel);
            currentData.setMotionType(MotionType.fromValue(currentMotionType));
            currentData.setTimestamp(new Date());
            breathingData.postValue(currentData);

            // 如果监听器不为空
            if (listener != null) {
                // 通知监听器呼吸数据更新
                listener.onBreathingDataUpdate(currentData);
            }
        }

        // 如果检测到呼吸异常
        if (anomalyLevel.getValue() > 0) {
            // 处理呼吸异常警报
            handleAnomalyAlert(anomalyLevel.getValue(), breathRate);
        }

    }

    // 获取运动类型名称的方法，传入运动类型
    private String getMotionName(int motionType) {
        return MotionType.fromValue(motionType).getDescription();
    }

    // 处理呼吸异常警报的方法，传入异常等级和当前呼吸频率
    private void handleAnomalyAlert(int level, double currentRate) {
        String message;
        switch (level) {
            case 1:
                message = String.format("呼吸轻度异常: %.1f次/分", currentRate);
                mainHandler.post(() -> {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                });
                break;
            case 2:
                message = String.format("呼吸中度异常: %.1f次/分", currentRate);
                mainHandler.post(() -> {
                    SmsUtils.sendEmergencySms(context, message);
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                });
                break;
            case 3:
                message = String.format("呼吸严重异常: %.1f次/分！请立即休息", currentRate);
                mainHandler.post(() -> {
                    VibrationUtils.strongVibrate(context, 5000);
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    SmsUtils.sendEmergencySms(context, message);
                });
                break;
        }

        HealthData currentData = breathingData.getValue();
        if (currentData != null) {
            currentData.setBreathAnomaly(AnomalyLevel.fromValue(level));
            breathingData.postValue(currentData);
        }
    }

    // 生成扫描信号的方法，用于音频检测
    private short[] generateSweepSignal() {
        // 创建一个存储扫描信号的数组，长度为采样率
        short[] sweep = new short[SAMPLE_RATE];
        // 生成线性调频信号（Chirp信号）
        for (int i = 0; i < sweep.length; i++) {
            // 计算当前时间点的相位，频率从FREQ_MIN线性增加到FREQ_MAX
            double phase = 2 * Math.PI * i *
                    (FREQ_MIN + (FREQ_MAX - FREQ_MIN) * i / (2.0 * sweep.length)) / SAMPLE_RATE;
            // 将计算得到的正弦值转换为short类型，并设置适当的幅度
            sweep[i] = (short) (Short.MAX_VALUE * 0.7 * Math.sin(phase));
        }
        return sweep; // 返回生成的扫描信号
    }

    // 对音频数据进行带通滤波的方法，保留指定频率范围内的信号
    private double[] bandpassFilter(short[] input, double lowCut, double highCut) {
        // 创建输出数组
        double[] output = new double[input.length];
        // 计算时间步长
        double dt = 1.0 / SAMPLE_RATE;
        // 计算高通滤波器的RC时间常数
        double RC = 1.0 / (2 * Math.PI * highCut);
        // 计算高通滤波器的alpha参数
        double alpha = dt / (RC + dt);

        // 首先应用高通滤波器
        double[] highPass = new double[input.length];
        highPass[0] = input[0];
        for (int i = 1; i < input.length; i++) {
            // 实现高通滤波器的差分方程
            highPass[i] = alpha * (highPass[i-1] + input[i] - input[i-1]);
        }

        // 然后应用低通滤波器
        RC = 1.0 / (2 * Math.PI * lowCut);
        alpha = dt / (RC + dt);
        output[0] = highPass[0];
        for (int i = 1; i < highPass.length; i++) {
            // 实现低通滤波器的差分方程
            output[i] = output[i-1] + alpha * (highPass[i] - output[i-1]);
        }

        return output; // 返回滤波后的音频数据
    }

    // 计算音频信号的希尔伯特包络的方法，用于提取信号的幅度包络
    private double[] computeHilbertEnvelope(double[] signal) {
        int n = signal.length;
        double[] envelope = new double[n];
        Complex[] complexSignal = new Complex[n];

        // 将实数信号转换为复数信号（虚部为0）
        for (int i = 0; i < n; i++) {
            complexSignal[i] = new Complex(signal[i], 0);
        }

        // 进行快速傅里叶变换
        Complex[] fft = FFT.fft(complexSignal);

        // 修改频域表示，只保留正频率部分（单边谱）
        for (int i = 1; i < n/2; i++) {
            fft[n-i] = new Complex(0, 0);
        }

        // 进行逆快速傅里叶变换
        Complex[] analytic = FFT.ifft(fft);

        // 计算解析信号的幅度，得到包络
        for (int i = 0; i < n; i++) {
            envelope[i] = analytic[i].abs();
        }

        return envelope; // 返回信号的包络
    }

    // 在音频包络中查找呼吸峰值的方法
    private List<Integer> findBreathPeaks(double[] envelope) {
        List<Integer> peaks = new ArrayList<>();
        // 设置峰值检测的阈值（0.2倍的最大包络值）
        double threshold = 0.2 * findMax(envelope);

        // 遍历包络数据，查找局部最大值
        for (int i = 1; i < envelope.length - 1; i++) {
            // 如果当前点的值大于阈值，并且比前后两个点的值都大，则认为是一个峰值
            if (envelope[i] > threshold &&
                    envelope[i] > envelope[i-1] &&
                    envelope[i] > envelope[i+1]) {
                peaks.add(i); // 将峰值的索引添加到列表中
            }
        }

        return peaks; // 返回峰值索引列表
    }

    // 根据呼吸峰值计算呼吸频率的方法
    private double calculateBreathRate(List<Integer> peaks) {
        // 如果检测到的峰值数量太少，无法计算频率，则返回当前基线值
        if (peaks.size() < 2) return currentBaseline;

        double avgInterval = 0;
        // 计算相邻峰值之间的平均间隔
        for (int i = 1; i < peaks.size(); i++) {
            avgInterval += (peaks.get(i) - peaks.get(i-1));
        }
        avgInterval /= (peaks.size() - 1);

        // 将样本间隔转换为每分钟的呼吸频率
        return 60.0 * SAMPLE_RATE / avgInterval;
    }

    // 处理基线测量数据的方法
    private void handleBaselineMeasurement(double currentRate) {
        // 过滤异常的呼吸频率值
        if (currentRate < 6 || currentRate > 30) return;

        // 将有效的呼吸频率添加到历史记录中
        historyFreq.add(currentRate);
        // 计算自基线测量开始以来的时间
        long elapsed = System.currentTimeMillis() - baselineStartTime;

        // 每秒钟更新一次基线测量进度
        if (listener != null && elapsed % 1000 == 0) {
            int progress = (int) (elapsed * 100 / BASELINE_DURATION);
            mainHandler.post(() -> listener.onBaselineInstruction(
                    "测量中... " + progress + "%", true));
        }
    }

    // 更新动态基线的方法，根据实时数据调整基线值
    private void updateDynamicBaseline(double currentRate) {
        // 过滤异常的呼吸频率值
        if (currentRate < 6 || currentRate > 30) return;

        // 将当前呼吸频率添加到历史记录中
        historyFreq.add(currentRate);
        // 限制历史记录的长度，保持最新的300个数据点
        if (historyFreq.size() > 300) {
            historyFreq.removeFirst();
        }

        // 计算加权平均值，近期数据权重更高
        double sum = 0, weightSum = 0;
        int pos = 1;
        for (double rate : historyFreq) {
            double weight = pos / (double) historyFreq.size();
            sum += rate * weight;
            weightSum += weight;
            pos++;
        }
        double newBaseline = sum / weightSum;

        // 检查新的基线值是否在合理范围内，如果是则更新当前基线
        if (newBaseline >= dailyBaselineMin && newBaseline <= dailyBaselineMax) {
            currentBaseline = newBaseline;
            currentThreshold = Math.max(currentBaseline * 0.2, 2.0);
        }
    }

    // 检查呼吸频率是否异常的方法
    private AnomalyLevel checkAnomaly(double currentRate) {
        // 计算当前呼吸频率与基线的偏差
        double deviation = Math.abs(currentRate - currentBaseline);

        // 根据偏差程度返回相应的异常等级
        if (currentRate > 30 || currentRate < 6) return AnomalyLevel.SEVERE;
        else if (deviation > currentBaseline * 0.3) return AnomalyLevel.MODERATE;
        else if (deviation > currentBaseline * 0.2) return AnomalyLevel.MILD;
        return AnomalyLevel.NORMAL; // 如果没有异常，返回正常等级
    }

    // 计算列表中数值的中位数的方法
    private double calculateMedian(List<Double> values) {
        // 创建一个新列表并排序
        List<Double> sorted = new ArrayList<>(values);
        Collections.sort(sorted);

        // 根据列表长度的奇偶性计算中位数
        int middle = sorted.size() / 2;
        return sorted.size() % 2 == 1 ?
                sorted.get(middle) :
                (sorted.get(middle-1) + sorted.get(middle)) / 2.0;
    }

    // 查找数组中最大值的方法
    private double findMax(double[] array) {
        double max = Double.MIN_VALUE;
        for (double value : array) {
            if (value > max) max = value;
        }
        return max;
    }

    // 停止监测呼吸的方法
    public void stopMonitoring() {
        // 设置监测状态为停止
        isMonitoring = false;

        // 释放音频播放资源
        if (audioTrack != null) {
            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }

        // 释放音频录制资源
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }

    // 检查基线是否准备好的方法
    public boolean isBaselineReady() {
        return isBaselineReady;
    }

    // 内部类：快速傅里叶变换工具类
    private static class FFT {
        // 将实数数组转换为复数数组并进行FFT
        public static Complex[] fft(double[] input) {
            Complex[] x = new Complex[input.length];
            for (int i = 0; i < x.length; i++) {
                x[i] = new Complex(input[i], 0);
            }
            return fft(x);
        }

        // 递归实现的快速傅里叶变换算法
        public static Complex[] fft(Complex[] x) {
            int n = x.length;

            // 基本情况：如果数组长度为1，直接返回
            if (n == 1) return new Complex[]{x[0]};

            // 检查数组长度是否为2的幂
            if (n % 2 != 0) throw new IllegalArgumentException("Not power of 2");

            // 将输入数组分为偶数和奇数两部分
            Complex[] even = new Complex[n/2];
            Complex[] odd = new Complex[n/2];
            for (int k = 0; k < n/2; k++) {
                even[k] = x[2*k];
                odd[k] = x[2*k + 1];
            }

            // 递归计算偶数部分和奇数部分的FFT
            Complex[] evenFFT = fft(even);
            Complex[] oddFFT = fft(odd);

            // 合并结果
            Complex[] result = new Complex[n];
            for (int k = 0; k < n/2; k++) {
                double kth = -2 * k * Math.PI / n;
                Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
                result[k] = evenFFT[k].plus(wk.times(oddFFT[k]));
                result[k + n/2] = evenFFT[k].minus(wk.times(oddFFT[k]));
            }

            return result;
        }

        // 逆快速傅里叶变换算法
        public static Complex[] ifft(Complex[] x) {
            int n = x.length;
            Complex[] result = new Complex[n];

            // 对输入取共轭
            for (int i = 0; i < n; i++) {
                result[i] = x[i].conjugate();
            }

            // 进行FFT
            result = fft(result);

            // 再取共轭并除以n，得到逆变换结果
            for (int i = 0; i < n; i++) {
                result[i] = result[i].conjugate().times(1.0/n);
            }

            return result;
        }
    }

    // 内部类：复数类，用于FFT计算
    private static class Complex {
        private final double re;  // 实部
        private final double im;  // 虚部

        // 构造函数
        public Complex(double real, double imag) {
            this.re = real;
            this.im = imag;
        }

        // 计算复数的模（幅度）
        public double abs() {
            return Math.hypot(re, im);
        }

        // 复数加法
        public Complex plus(Complex other) {
            return new Complex(re + other.re, im + other.im);
        }

        // 复数减法
        public Complex minus(Complex other) {
            return new Complex(re - other.re, im - other.im);
        }

        // 复数乘法
        public Complex times(Complex other) {
            return new Complex(
                    re * other.re - im * other.im,
                    re * other.im + im * other.re
            );
        }

        // 复数与标量乘法
        public Complex times(double scalar) {
            return new Complex(re * scalar, im * scalar);
        }

        // 计算共轭复数
        public Complex conjugate() {
            return new Complex(re, -im);
        }
    }
}

