package com.example.mvvmforjava.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mvvmforjava.model.MyRepository;
import com.example.mvvmforjava.model.net.NETSTATE;

public class ViewModelImpl extends ViewModel implements IViewModel {
    @Override
    public LiveData getEntity(Object o) {
        return MyRepository.getInstance().getEntity(o);
    }

    @Override
    public LiveData getEntityInPage(Object o, String... args) {
        return null;
    }

    @Override
    public LiveData<NETSTATE> getNetState() {
        return MyRepository.getInstance().getNetState();
    }

    @Override
    public void refresh(Object o) {
        MyRepository.getInstance().refresh(o);
    }
}
