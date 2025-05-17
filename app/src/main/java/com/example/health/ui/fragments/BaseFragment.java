// BaseFragment.java
package com.example.health.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public abstract class BaseFragment<B extends ViewDataBinding, VM extends androidx.lifecycle.ViewModel> extends Fragment {
    protected B binding;
    protected VM viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this, getViewModelFactory()).get(getViewModelClass());
        initViews();
        observeData();
    }

    protected abstract int getLayoutId();
    protected abstract Class<VM> getViewModelClass();
    protected abstract ViewModelProvider.Factory getViewModelFactory();
    protected abstract void initViews();
    protected abstract void observeData();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.unbind();
    }
}