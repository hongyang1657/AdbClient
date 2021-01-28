package com.hongy.adbclient.ui.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.hongy.adbclient.R;


public class FunctionButtonAdapter extends RecyclerView.Adapter<FunctionButtonAdapter.MyViewHolder> {

    private Context mContext;
    private FloorDataChangeListener listener;
    private String[] functionList;
    private GridLayoutManager gridLayoutManager;


    public FunctionButtonAdapter(Context context, String[] functionList, FloorDataChangeListener listener, GridLayoutManager gridLayoutManager) {
        this.functionList = functionList;
        this.listener = listener;
        this.gridLayoutManager = gridLayoutManager;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_function_button, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        //设置item长宽一致
        ViewGroup.LayoutParams params = myViewHolder.itemView.getLayoutParams();
        params.height = gridLayoutManager.getWidth() / gridLayoutManager.getSpanCount()
                - 2 * myViewHolder.itemView.getPaddingLeft() - 2 * ((ViewGroup.MarginLayoutParams) params).leftMargin;

        String floorVal = functionList[i];
        myViewHolder.tvItem.setText(floorVal);
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return functionList.length;
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItem;
        private ImageView ivIcon;
        private LinearLayout llItemBg;

        MyViewHolder(View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.tv_item_name);
            ivIcon = itemView.findViewById(R.id.iv_function_icon);
            llItemBg = itemView.findViewById(R.id.ll_item_bg);
        }
    }

    public void notifyData(String[] functionList) {
        this.functionList = functionList;
        notifyDataSetChanged();
    }


    public interface FloorDataChangeListener {
        void onItemClick(int position);
    }
}
