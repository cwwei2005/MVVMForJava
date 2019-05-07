package com.example.mvvmforjava.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mvvmforjava.MyIntentService;
import com.example.mvvmforjava.R;
import com.example.mvvmforjava.databinding.ActivityMainBinding;
import com.example.mvvmforjava.model.db.MyDatabase;
import com.example.mvvmforjava.model.db.Theater;
import com.example.mvvmforjava.model.net.NETSTATE;
import com.example.mvvmforjava.view.adapter.BaseBindingAdapter;
import com.example.mvvmforjava.viewmodel.IViewModel;
import com.example.mvvmforjava.viewmodel.ViewModelImpl;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private IViewModel vm;
    private Theater theater;
    private boolean setTheater = false;
    private BaseBindingAdapter adapter;
    private int refreshMode  = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        startService(new Intent(this, MyIntentService.class));
        initBinding();
        initView();
        initData();
    }

    private void initBinding() {
        vm = ViewModelProviders.of(this).get(ViewModelImpl.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
//        binding.setVm1(vm);
//        binding.setTheater(theater);
        binding.setIsLoading(true);
    }

    private void initView(){
        //todo
//        findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                vm.refresh(new Theater());
//            }
//        });
        initRecyclerView();
    }

    private void initRecyclerView() {
        adapter = new BaseBindingAdapter<>(R.layout.recyclerview_movie_item, BR.theaterBean);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                refreshMode = 1;
                vm.refresh(new Theater());
            }

            @Override
            public void onLoadMore() {
                refreshMode = 2;
                vm.refresh(new Theater());
            }
        });
    }

    private void initData(){
        initTheater();
    }

    private void initTheater(){
        Observer entityObserver = new Observer() {
            @Override
            public void onChanged(Object o) {
                updateUI(o);
            }
        };

        Observer netObserver = new Observer() {
            @Override
            public void onChanged(Object o) {
//                if (o.toString().equals("SUCCESS")){
//                    Toast.makeText(MainActivity.this, "SUCCESS",Toast.LENGTH_SHORT).show();
//                }
                SystemClock.sleep(300);
                if (refreshMode == 1) binding.recyclerView.refreshComplete();
                if (refreshMode == 2) binding.recyclerView.loadMoreComplete();
                refreshMode = 0;
            }
        };

        vm.getEntity(new Theater()).observe(this, entityObserver);
        vm.getNetState().observe(this, netObserver);
    }

    private void updateUI(Object o){
        if (o instanceof Theater){
            Theater theater = (Theater) o;
//            if (!setTheater){
//                setTheater = true;
//                binding.setTheater(theater);  //第一次需要设置非空对象，以后可以单独修改某个属性
//            }
            adapter.refresh(theater.getSubjects());
            Log.e("tag",""+theater.getTitle());
        }
        Log.e("tag","onChanged....");
        binding.setIsLoading(false);
    }
}
