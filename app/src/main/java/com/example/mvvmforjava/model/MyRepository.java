package com.example.mvvmforjava.model;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.mvvmforjava.App;
import com.example.mvvmforjava.model.db.MyDatabase;
import com.example.mvvmforjava.model.db.Theater;
import com.example.mvvmforjava.model.db.Top250;
import com.example.mvvmforjava.model.net.MyRetrofit;
import com.example.mvvmforjava.model.net.NETSTATE;
import com.example.mvvmforjava.model.net.NetworkUtil;
import com.example.mvvmforjava.viewmodel.IViewModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MyRepository {
    private static MyRepository instance;

    public static MyRepository getInstance(){
        if (instance == null){
            synchronized (MyRepository.class){
                if (instance == null) instance = new MyRepository();
            }
        }
        return instance;
    }



    /**
     * 获取一个实体
     */
    public LiveData getEntity(Object o){
        return getNetEntity(o);
    }

    /**
     * 获取网络请求状态
     */
    private MutableLiveData<NETSTATE> netState = new MutableLiveData<>();
    public LiveData<NETSTATE> getNetState(){
        return netState;
    }

    /**
     * 刷新请求
     * 如果请求成功，更新数据库即可，flowable能感知数据库变化
     * 失败返回状态
     * @param o
     */
    public void refresh(final Object o){
        Flowable response = getHttpRequest(o);
        if (response == null) return;

        response.subscribeOn(Schedulers.io())
                .doOnNext(new Consumer<Object>() {  //请求成功，1.写入数据库
                    @Override
                    public void accept(Object obj) throws Exception {
//                        Log.e("tag","http success");
                        dbInsert(o, obj);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object obj) throws Exception {  //2.切换到主线程通知更新
                        netState.postValue(NETSTATE.SUCCESS);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {  //请求异常，同样读数据库并通知更新
                        netState.postValue(NETSTATE.FAILED);
                    }
                });
    }

    /**
     * 1、从网络获取实体对象
     * 2、获取成功则写入数据库
     * 3、无论网络获取是否成功都读取数据库并通知activity
     */
    private LiveData getNetEntity(final Object o){
        final MediatorLiveData data = new MediatorLiveData();  //LiveData为抽象类，不能实例化
        final LiveData[] dbData = new LiveData[1];

        Flowable response = getHttpRequest(o);
        if (response == null) return data;

        response.subscribeOn(Schedulers.io())
                .doOnNext(new Consumer<Object>() {  //请求成功，1.写入数据库
                    @Override
                    public void accept(Object obj) throws Exception {
//                        Log.e("tag","http success");
                        dbInsert(o, obj);
                        dbData[0] = getDBData(o);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object obj) throws Exception {  //2.切换到主线程通知更新
                        netState.postValue(NETSTATE.SUCCESS);
                        notifyUIThread(dbData[0], data);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {  //请求异常，同样读数据库并通知更新
                        netState.postValue(NETSTATE.FAILED);
                        Toast.makeText(App.instance, "NET FAILED", Toast.LENGTH_SHORT).show();
                        Observable.just(1)
                                .subscribeOn(Schedulers.io())
                                .doOnNext(new Consumer<Integer>() {
                                    @Override
                                    public void accept(Integer integer) throws Exception {
                                        dbData[0] = getDBData(o);
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Integer>() {
                                    @Override
                                    public void accept(Integer integer) throws Exception {
                                        notifyUIThread(dbData[0], data);
                                    }
                                });
                    }
                });

        return data;
    }

    /**
     * 在主线程通知数据更新
     * 虽然room支持rxjava和livedata，但需要调用一次查询操作，后面的数据库操作才能被感知。
     * 查询得到的dbData是livedata类型，通过data.addSource使得data能被观察。
     */
    private void notifyUIThread(LiveData dbData, final MediatorLiveData data){
        if (dbData != null){
            data.addSource(dbData, new Observer() {
                @Override
                public void onChanged(Object obj) {
                    data.postValue(obj);
                }
            });
        }
    }

    //不同类型不同请求，Flowable不要指定泛型类型
    private Flowable getHttpRequest(Object o){
        if (!NetworkUtil.isNetworkConnected(App.instance) || NetworkUtil.process == 2) {
            if (NetworkUtil.process == 2){
                netState.postValue(NETSTATE.NETINVALID);
                Toast.makeText(App.instance, "NET INVALID", Toast.LENGTH_SHORT).show();
            } else{
                netState.postValue(NETSTATE.NONET);
                Toast.makeText(App.instance, "NO NET", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
        if(o instanceof Theater) return MyRetrofit.getInstance().getApi().getTheater();
        //todo...
        return null;
    }

    //不同类型不同插入
    private void dbInsert(Object o, Object obj){
        if(o instanceof Theater) MyDatabase.getInstance().theaterDao().replaceInsert((Theater) obj);
        //todo...
    }

    //不同类型不同获取
    private LiveData getDBData(Object o){
        if(o instanceof Theater) return MyDatabase.getInstance().theaterDao().getSingle2();
        //todo...
        return null;
    }
}
