package com.fedco.mbc.meter_selection.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.TypedArray;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.Readinginput;
import com.fedco.mbc.meter_selection.StartMeterReading;
import com.fedco.mbc.meter_selection.adapter.MakeGridAdapter;
import com.fedco.mbc.meter_selection.model.MakePojo;
import com.fedco.mbc.meter_selection.model.ModelPojo;

import java.util.ArrayList;

/**
 * Created by ABHI on 28-Nov-17.
 */

public class BottomSheetFragment extends BottomSheetDialogFragment {
    private static final String TAG = BottomSheetFragment.class.getSimpleName();
    private RecyclerView makeRecyclerView;
    private StartMeterReading startMeterReading;


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.bottom_meter_make, null);
        startMeterReading = (StartMeterReading) getActivity();
        dialog.setContentView(contentView);

        makeRecyclerView = (RecyclerView) contentView.findViewById(R.id.rv_make_grid);
        makeRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        makeRecyclerView.setAdapter(new MakeGridAdapter(getMakeAndModel(), new MakeGridAdapter.MakeClick() {
            @Override
            public void onMakeClick(MakePojo makePojo, String model_number) {
                startMeterReading.onStartReading(makePojo.getMakeName(),model_number);
                dismiss();
            }
        }));
    }

    private ArrayList<MakePojo> getMakeAndModel() {
        ArrayList<MakePojo> arrayList = new ArrayList<>();
        try {
            String make[] = getResources().getStringArray(R.array.make_name);
            TypedArray model = getResources().obtainTypedArray(R.array.make_images);
            for (int i = 0; i < make.length; i++) {
                arrayList.add(new MakePojo(i + "", make[i], model.getDrawable(i), getModel(make[i])));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    private ArrayList<ModelPojo> getModel(String make) {
        ArrayList<ModelPojo> arrayList = new ArrayList<>();

        try {
            String model[];
            switch (make) {

                case "ALLIED":
                    model = getResources().getStringArray(R.array.model_allied);
                    for (int i = 0; i < model.length; i++) {
                        arrayList.add(new ModelPojo(i + "", model[i]));
                    }
                    break;

                case "AVON":
                    model = getResources().getStringArray(R.array.model_avon);
                    for (int i = 0; i < model.length; i++) {
                        arrayList.add(new ModelPojo(i + "", model[i]));
                    }
                    break;

                case "BENTEK":
                    model = getResources().getStringArray(R.array.model_bentek);
                    for (int i = 0; i < model.length; i++) {
                        arrayList.add(new ModelPojo(i + "", model[i]));
                    }
                    break;

                case "EMCO":
                    model = getResources().getStringArray(R.array.model_emco);
                    for (int i = 0; i < model.length; i++) {
                        arrayList.add(new ModelPojo(i + "", model[i]));
                    }
                    break;

                case "GENUS":
                    model = getResources().getStringArray(R.array.model_genus);
                    for (int i = 0; i < model.length; i++) {
                        arrayList.add(new ModelPojo(i + "", model[i]));
                    }
                    break;

                case "HPL":
                    model = getResources().getStringArray(R.array.model_hpl);
                    for (int i = 0; i < model.length; i++) {
                        arrayList.add(new ModelPojo(i + "", model[i]));
                    }
                    break;

                case "LNT":
                    model = getResources().getStringArray(R.array.model_lnt);
                    for (int i = 0; i < model.length; i++) {
                        arrayList.add(new ModelPojo(i + "", model[i]));
                    }
                    break;

                case "MONTEL":
                    model = getResources().getStringArray(R.array.model_montel);
                    for (int i = 0; i < model.length; i++) {
                        arrayList.add(new ModelPojo(i + "", model[i]));
                    }
                    break;

                case "LNG":
                    model = getResources().getStringArray(R.array.model_lng);
                    for (int i = 0; i < model.length; i++) {
                        arrayList.add(new ModelPojo(i + "", model[i]));
                    }
                    break;

                case "SECURE":
                    model = getResources().getStringArray(R.array.model_secure);
                    for (int i = 0; i < model.length; i++) {
                        arrayList.add(new ModelPojo(i + "", model[i]));
                    }
                    break;

                case "VISIONTEK":
                    model = getResources().getStringArray(R.array.model_vision_tek);
                    for (int i = 0; i < model.length; i++) {
                        arrayList.add(new ModelPojo(i + "", model[i]));
                    }
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

}
