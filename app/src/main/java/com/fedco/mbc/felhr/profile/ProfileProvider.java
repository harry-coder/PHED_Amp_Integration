package com.fedco.mbc.felhr.profile;

import android.content.Context;
import java.util.List;

public class ProfileProvider {
    private Context context;
    private ProfileDbAdapter dbAdapter;
    private HeavyTasksThread thread = new HeavyTasksThread();

    private class HeavyTasksThread extends Thread {
        private HeavyTasksThread() {
        }

        public void run() {
            ProfileProvider.this.dbAdapter = new ProfileDbAdapter(ProfileProvider.this.context);
            ProfileProvider.this.dbAdapter.open();
        }
    }

    public ProfileProvider(Context context) {
        this.context = context;
        this.thread.start();
    }

    public void close() {
        if (this.dbAdapter != null) {
            this.dbAdapter.close();
        }
    }

    public List<Profile> lookup(String vid, String pid) {
        if (this.dbAdapter != null) {
            return this.dbAdapter.query(vid, pid);
        }
        return null;
    }

    public boolean insertProfile(Profile profile) {
        if (this.dbAdapter.insertProfile(profile) != -1) {
            return true;
        }
        return false;
    }
}
