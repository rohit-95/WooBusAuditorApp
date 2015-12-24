package in.woobus.app.auditor;

import android.app.ListActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class audit_submit extends ListActivity {
    String[] qTypeArray;
    TextView temp;
    Button submit;
    JSONArray questions;


    LinearLayout.LayoutParams lwc = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    LinearLayout.LayoutParams lmp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

    FetchQuestions helper = FetchQuestions.getInstance();

    final static String url = "http://dev.cachefi.com/api/v1/audits/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_submit);
        loadQuestions();
    }

    private void showQuestions() {
        int questionCount = questions.length();
        qTypeArray = new String[questionCount + 1];

        if(questionCount != 0) {
            for(int i = 0; i < questionCount; i++) {
                try {
                    qTypeArray[i] = questions.getJSONObject(i).getString("q_type");
                } catch (JSONException e) {
                    Log.e("js", "Invalid JSON string", e);
                }
            }
            CustomAdapter adapter = new CustomAdapter(getApplicationContext(), qTypeArray, questions);
            setListAdapter(adapter);
        }
        else {
            temp = new TextView(getApplicationContext());
            temp.setText("No questions found on server");
            addContentView(temp, lwc);
            Log.i("noQ", "onCreate: No questions found");
        }
        submit = new Button(this);
        submit.setBackgroundColor(Color.LTGRAY);
        submit.setText("Submit");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAudit();
            }
        });
        addContentView(submit, lwc);
    }

    private JSONObject evalQuestions() {
        JSONObject audit = new JSONObject();
        ListView list = getListView();
        int qCount = list.getChildCount();
        View rowView;
        String tag;
        JSONObject tmp = new JSONObject();

        for (int i = 0; i < qCount; i++) {
            rowView = list.getChildAt(i);
            tag = rowView.getTag().toString();

            switch (tag) {
                case "bool" :
                    if ( ((RadioButton) rowView.findViewById(R.id.rbYes)).isChecked() ) {
                        try {
                            audit.put(questions.getJSONObject(i).getString("name"), true);
                        } catch (JSONException e) {
                            Log.e(tag + " yes " + i, "Invalid JSON string", e);
                        }
                    } else {
                        try {
                            audit.put(questions.getJSONObject(i).getString("name"), false);
                        } catch (JSONException e) {
                            Log.e(tag + " no " + i, "Invalid JSON string", e);
                        }
                    }
                    break;
                case "boolinfo" :
                    if ( ((RadioButton) rowView.findViewById(R.id.rbYes)).isChecked() ) {
                        try {
                            tmp.put("all", true);
                            tmp.put("info", "");
                            audit.put(questions.getJSONObject(i).getString("name"), tmp);
                        } catch (JSONException e) {
                            Log.e(tag + " yes " + i, "Invalid JSON string", e);
                        }
                    } else {
                        try {
                            tmp.put("all", true);
                            tmp.put("info", ((EditText) rowView.findViewById(R.id.info)).getText());
                            audit.put(questions.getJSONObject(i).getString("name"), tmp);
                        } catch (JSONException e) {
                            Log.e(tag + " no " + i, "Invalid JSON string", e);
                        }
                    }
                    break;
                case "rate" :
                    try {
                        audit.put(questions.getJSONObject(i).getString("name"),
                                ((RatingBar) rowView.findViewById(R.id.ratingBar)).getRating());
                    } catch (JSONException e) {
                        Log.e(tag + " rating " + i, "Invalid JSON string", e);
                    }
                    break;
                case "txt" :
                    try {
                        audit.put(questions.getJSONObject(i).getString("name"),
                                ((EditText) rowView.findViewById(R.id.info)).getText());
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
        Log.i("jsondata for submit",auditData.toString());
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