package com.example.mvvmforjava.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BaseBindingAdapter<T> extends RecyclerView.Adapter<BaseBindingAdapter<T>.MyViewHolder> {
    private int layoutId;
    private int BRId;
    private List mList = new ArrayList();

    public BaseBindingAdapter(int layoutId, int BRId) {
        this.layoutId = layoutId;
        this.BRId = BRId;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), layoutId, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(binding.getRoot());
        viewHolder.setBinding(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.getBinding().setVariable(BRId, mList.get(position));
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    //fun
    public void refresh(List list) {
        mList.clear();
        this.mList.addAll(list);
        notifyDataSetChanged();
    }



    //view holder
    class MyViewHolder extends RecyclerView.ViewHolder{
        private ViewDataBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void setBinding(ViewDataBinding binding){
            this.binding = binding;
        }
        ViewDataBinding getBinding() {
            return this.binding;
        }
    }
}
