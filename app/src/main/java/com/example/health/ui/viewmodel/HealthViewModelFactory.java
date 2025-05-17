package com.example.health.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.health.model.repository.HealthRepository;
import com.example.health.sensor.BluetoothManager;
import com.example.health.ui.viewmodel.HealthViewModel;

// 在 HealthViewModelFactory.java
public class HealthViewModelFactory implements ViewModelProvider.Factory {
    private final HealthRepository repository;
    private final BluetoothManager bluetoothManager;
    private final Application application;

    public HealthViewModelFactory(HealthRepository repository,
                                  BluetoothManager bluetoothManager,
                                  Application application) {
        this.repository = repository;
        this.bluetoothManager = bluetoothManager;
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new HealthViewModel(repository, bluetoothManager, application);
    }
}