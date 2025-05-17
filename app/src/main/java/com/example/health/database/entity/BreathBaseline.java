// BreathBaseline.java
package com.example.health.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.health.database.converter.Converters;
import com.example.health.database.converter.MotionTypeConverter;
import com.example.health.model.enums.MotionType;

import java.util.Date;

// 定义一个表示呼吸基线数据的实体类，映射到名为"breath_baseline"的数据库表
@Entity(tableName = "breath_baseline")
// 声明使用的类型转换器类
@TypeConverters({Converters.class, MotionTypeConverter.class})
public class BreathBaseline {
    // 定义主键
    @PrimaryKey(autoGenerate = true)
    private long id;

    private MotionType motionType;    // 运动类型

    private double baselineValue;    // 基线值

    private Date timestamp;    // 时间戳

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MotionType getMotionType() {
        return motionType;
    }

    public void setMotionType(MotionType motionType) {
        this.motionType = motionType;
    }

    public double getBaselineValue() {
        return baselineValue;
    }

    public void setBaselineValue(double baselineValue) {
        this.baselineValue = baselineValue;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}