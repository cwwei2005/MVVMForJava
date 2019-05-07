package com.example.mvvmforjava.viewmodel;

import androidx.lifecycle.LiveData;

import com.example.mvvmforjava.model.net.NETSTATE;

import java.util.List;

public interface IViewModel {
    /**
     * 获取实体
     * 数据无论在网络或数据库都将以实体形式操作
     * @param o ： 泛型
     * @return
     */
    LiveData getEntity(Object o);

    /**
     * 分页获取
     * @param o
     * @param args
     * @return
     */
    LiveData getEntityInPage(Object o, String...args);

//    LiveData<List<Object>> getEntitys();
//    LiveData<List<Object>> getEntitysInPage();

    /**
     * 网络请求结果的状态
     * @return
     */
    LiveData<NETSTATE> getNetState();

    /**
     * 重新请求网络刷新数据
     * @param o
     */
    void refresh(Object o);
}
