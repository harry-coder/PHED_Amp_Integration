package com.fedco.mbc.felhr.droidterm;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fedco.mbc.R;
import com.fedco.mbc.felhr.profile.Profile;
import java.util.List;

public class ProfilesDialogFragment extends DialogFragment implements OnClickListener, OnItemClickListener {
    public static final String CLASS_ID = ProfilesDialogFragment.class.getSimpleName();
    private ProfileAdapter adapter;
    private Button backButton;
    private IProfilesDialogCommunicator comm;
    private Context context;
    private ListView listView;
    private Button newConfigButton;

    public interface IProfilesDialogCommunicator {
        void backToMainMenu();

        void loadProfile(Profile profile);

        void newConfig();
    }

    private class ProfileAdapter extends ArrayAdapter<Profile> {
        public ProfileAdapter(Context context, List<Profile> profiles) {
            super(context, R.layout.profile_list_item, profiles);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.profile_list_item, parent, false);
            TextView profileName = (TextView) rowView.findViewById(R.id.textViewProfileName);
            TextView profileData = (TextView) rowView.findViewById(R.id.textViewProfileData);
            Profile profile = (Profile) getItem(position);
            String parity = null;
            if (profile.getParity() == 0) {
                parity = "None";
            }
            if (profile.getParity() == 1) {
                parity = "Odd";
            }
            if (profile.getParity() == 2) {
                parity = "Even";
            }
            if (profile.getParity() == 3) {
                parity = "Mark";
            }
            if (profile.getParity() == 4) {
                parity = "Space";
            }
            profileName.setText("Profile Name: " + profile.getProfileName());
            profileData.setText("BaudRate: " + profile.getBaudRate() + "\nData bits: " + profile.getDataBits() + "\nStop bits: " + profile.getStopBits() + "\nparity: " + parity + "\nFlow control: " + "None");
            return rowView;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profiles_list_dialog, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.context = getActivity();
        this.comm = (IProfilesDialogCommunicator) getActivity();
        this.listView = (ListView) getView().findViewById(R.id.listViewprofiles);
        this.backButton = (Button) getView().findViewById(R.id.button_backMenu);
        this.newConfigButton = (Button) getView().findViewById(R.id.button_newconfig);
        this.backButton.setOnClickListener(this);
        this.newConfigButton.setOnClickListener(this);
        this.listView.setOnItemClickListener(this);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(1);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        return dialog;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_backMenu:
                this.comm.backToMainMenu();
                return;
            case R.id.button_newconfig:
                this.comm.newConfig();
                return;
            default:
                return;
        }
    }

    public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
        this.comm.loadProfile((Profile) this.adapter.getItem(arg2));
    }

    public void populateList(List<Profile> profiles) {
        this.adapter = new ProfileAdapter(this.context, profiles);
        this.listView.setAdapter(this.adapter);
    }
}
