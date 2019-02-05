package com.fedco.mbc.meter_selection.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fedco.mbc.R;
import com.fedco.mbc.meter_selection.model.ModelPojo;

import java.util.ArrayList;

/**
 * Created by Hasnain on 01-Sep-17.
 */

public class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.ItemHolder>{
    private ArrayList<ModelPojo> arrayList;
    private ModelClick modelClick;
    private Context context;

    public interface ModelClick{
        void onModelClick(ModelPojo modelPojo);
    }

    public ModelAdapter(ArrayList<ModelPojo> arrayList, ModelClick modelClick) {
        this.arrayList = arrayList;
        this.modelClick = modelClick;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ItemHolder(LayoutInflater.from(context).inflate(R.layout.row_model, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.bind(arrayList.get(position),modelClick);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        private TextView tv_model_name;
        private LinearLayout ll_root;

        public ItemHolder(View itemView) {
            super(itemView);
            tv_model_name = (TextView) itemView.findViewById(R.id.tv_model_name);
            ll_root = (LinearLayout) itemView.findViewById(R.id.ll_root);

        }

        public void bind(final ModelPojo rowItem,final ModelClick modelClick) {
            tv_model_name.setText(rowItem.getModelName());
            ll_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    modelClick.onModelClick(rowItem);
                }
            });



        }

    }
}
