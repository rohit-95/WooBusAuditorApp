package in.woobus.app.auditor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class audit_submit extends AppCompatActivity{
    String[] qTypeArray;
    JSONArray questions;
    int qCount;
    ListView list;

    FetchQuestions helper = FetchQuestions.getInstance();

    final static String url = "http://dev.cachefi.com/api/v1/audits/";
    final static String url2 = "http://192.168.0.107:1337/api/v1/audits/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_submit);

        loadQuestions();
    }

    private void showQuestions() {
        qCount = questions.length();
        qTypeArray = new String[qCount + 1];

        if(qCount != 0) {
            for(int i = 0; i < qCount; i++) {
                try {
                    qTypeArray[i] = questions.getJSONObject(i).getString("q_type");
                } catch (JSONException e) {
                    Log.e("js", "Invalid JSON string", e);
                }
            }
        }
        else {
            Log.i("noQ", "onCreate: No questions found");
        }
        qTypeArray[qCount] = "button";
        CustomAdapter adapter = new CustomAdapter(getApplicationContext(), qTypeArray, questions);
        adapter.setOnButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAudit();
            }
        });
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
    }

    public JSONObject evalQuestions() {
        JSONObject audit = new JSONObject();
        String tag;
        JSONObject tmp = new JSONObject();
        List<CustomAdapter.Answer> answers = ((CustomAdapter) list.getAdapter()).getAnswers();
        CustomAdapter.Answer ans;
        for (int i = 0; i < qCount; i++) {
            ans = answers.get(i);
            tag = ans.type;
            switch (tag) {
                case "bool" :
                    try {
                        audit.put(questions.getJSONObject(i).getString("name"), ans.choice);
                    } catch (JSONException e) {
                        Log.e(tag + " yes " + i, "Invalid JSON string", e);
                    }
                    break;
                case "boolinfo" :
                    try {
                        tmp.put("all", ans.choice);
                        tmp.put("info", ans.info);
                        audit.put(questions.getJSONObject(i).getString("name"), tmp);
                    } catch (JSONException e) {
                        Log.e(tag + " yes " + i, "Invalid JSON string", e);
                    }
                    break;
                case "rate" :
                    try {
                        audit.put(questions.getJSONObject(i).getString("name"), Float.valueOf(ans.info));
                    } catch (JSONException e) {
                        Log.e(tag + " rating " + i, "Invalid JSON string", e);
                    }
                    break;
                case "txt" :
                    try {
                        audit.put(questions.getJSONObject(i).getString("name"), ans.info);
                    } catch (JSONException e) {
                        Log.e(tag + " " + i, "Invalid JSON string", e);
                    }
                    break;
                case "num" :
                    try {
                        audit.put(questions.getJSONObject(i).getString("name"), Integer.valueOf(ans.info));
                    } catch (JSONException e) {
                        Log.e(tag + " " + i, "Invalid JSON string", e);
                    }
            }
        }
        return audit;
    }

    private void loadQuestions() {
        JsonArrayRequest jsArrRequest = new JsonArrayRequest
                (Request.Method.GET, url + "questions", null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("Response " + response.length(), response.toString());
                        questions = response;
                        showQuestions();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());
                        Toast.makeText(getApplicationContext(), "Network Error",
                                Toast.LENGTH_LONG).show();
                    }
                });
        helper.add(jsArrRequest);
    }

    private void submitAudit() {
        JSONObject auditData = evalQuestions();
        Log.i("jsondata for submit", auditData.toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url + "add", auditData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(), "Submitted Successfully",
                                Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());
                        if (error != null && error.networkResponse.statusCode == 400) {
                            Toast.makeText(getApplicationContext(), "Submission Unsuccessful. Invalid Bus ID",
                                    Toast.LENGTH_LONG).show();
                        } else if (error != null && error.networkResponse.statusCode == 500) {
                            Toast.makeText(getApplicationContext(), "Submission Unsuccessful. Database Server Error",
                                    Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Submission Unsuccessful. Network Error",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        helper.add(jsObjRequest);
    }
}