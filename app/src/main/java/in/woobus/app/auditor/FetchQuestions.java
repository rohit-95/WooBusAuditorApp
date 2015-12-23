package in.woobus.app.auditor;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by rohit on 22/12/15.
 */
public class FetchQuestions extends Application {

    private RequestQueue mRequestQueue;
    private static FetchQuestions mInstance;
    public static final String TAG = FetchQuestions.class.getName();
    private  static String url = "localhost:1337/api/v1/audits/questions";

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    public static synchronized FetchQuestions getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public <T> void add(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancel() {
        mRequestQueue.cancelAll(TAG);
    }
}
