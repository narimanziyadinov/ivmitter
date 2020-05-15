package narimanz.dev.ivmitter;

import android.app.Application;

import com.twitter.sdk.android.core.Twitter;

public class IvmitterApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Twitter.initialize(this);
    }
}
