package com.fedco.mbc.meter_selection.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fedco.mbc.R;
import com.fedco.mbc.meter_selection.model.MakePojo;
import com.fedco.mbc.meter_selection.model.ModelPojo;

import java.util.ArrayList;

/**
 * Created by ABHI on 26-Oct-17.
 */

public class MakeGridAdapter extends RecyclerView.Adapter<MakeGridAdapter.MakeViewHolder> {
    private ArrayList<MakePojo> arrayList;
    private MakeClick makeClick;
    private Context mContext;

    public interface MakeClick {
        void onMakeClick(MakePojo makePojo, String model_number);
    }

    public MakeGridAdapter(ArrayList<MakePojo> arrayList, MakeClick makeClick) {
        this.arrayList = arrayList;
        this.makeClick = makeClick;
    }

    @Override
    public MakeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.row_make_grid, parent, false);
        return new MakeViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MakeViewHolder holder, int position) {
        holder.bind(arrayList.get(position), position,makeClick);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MakeViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_make;
        private TextView tv_make_name;
        private LinearLayout ll_root;

        public MakeViewHolder(View itemView) {
            super(itemView);

            iv_make = (ImageView) itemView.findViewById(R.id.iv_make);
            tv_make_name = (TextView) itemView.findViewById(R.id.tv_make_name);
            ll_root = (LinearLayout) itemView.findViewById(R.id.ll_root);
        }

        public void bind(final MakePojo makePojo, final int position, final MakeClick makeClick) {
            tv_make_name.setText(makePojo.getMakeName());
            iv_make.setImageDrawable(makePojo.getDrawableImage());

            tv_make_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<ModelPojo> modelPojoArrayList = makePojo.getModelPojoArrayList();
                    String selected_model = "";
                    makeClick.onMakeClick(makePojo, selected_model);
                }
            });

            ll_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<ModelPojo> modelPojoArrayList = makePojo.getModelPojoArrayList();
                    if(modelPojoArrayList.size() > 0){
                        showCountryPopup(makePojo);
                    }else{
                        makeClick.onMakeClick(makePojo, "na");
                    }
                }
            });
        }
    }

    private void showCountryPopup(final MakePojo makePojo) {
        final Dialog dialog = new Dialog(mContext);
        try {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.dialog_model);
            dialog.getWindow().setLayout(-1, -2);
            dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);

            ((TextView) dialog.findViewById(R.id.tv_title)).setText("Select "+makePojo.getMakeName()+" model");

            RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.rv_model);
            recyclerView.setLayoutManager(new LinearLayoutManager(dialog.getContext()));
            recyclerView.setAdapter(new ModelAdapter(makePojo.getModelPojoArrayList(), new ModelAdapter.ModelClick() {
                @Override
                public void onModelClick(ModelPojo modelPojo) {
                    makeClick.onMakeClick(makePojo, modelPojo.getModelName());
                    dialog.dismiss();
                }
            }));

            /**
             * Close button
             */
            dialog.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
