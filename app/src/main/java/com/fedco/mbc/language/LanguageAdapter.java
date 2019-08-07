package com.fedco.mbc.language;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fedco.mbc.R;

import java.util.ArrayList;

/**
 * Created by Hasnain on 09-Jan-18.
 */

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ItemHolder> {
    private ArrayList<LanguageDTO> arrayList;
    private LanguageSelect listener;
    private Context context;

    public interface LanguageSelect {
        void onLanguageSelected(LanguageDTO language);
    }

    public LanguageAdapter(ArrayList<LanguageDTO> arrayList, LanguageSelect listener) {
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ItemHolder(LayoutInflater.from(context).inflate(R.layout.row_language, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.bind(arrayList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        private TextView tv_language_name;

        public ItemHolder(View itemView) {
            super(itemView);
            tv_language_name = (TextView) itemView.findViewById(R.id.tv_language_name);
        }

        public void bind(final LanguageDTO language, final LanguageSelect listener) {
            tv_language_name.setText(language.getLanguageName());

            tv_language_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onLanguageSelected(language);
                }
            });

        }

    }
}
