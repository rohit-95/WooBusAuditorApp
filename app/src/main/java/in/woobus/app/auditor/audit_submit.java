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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class audit_submit extends ListActivity {
    String[] qTypeArray;
    TextView temp;
    RadioButton yes, no;
    RadioGroup rg;
    RatingBar rb;
    EditText inp;
    Button submit;


    LinearLayout.LayoutParams lwc = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    LinearLayout.LayoutParams lmp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

    FetchQuestions helper = FetchQuestions.getInstance();

    final static String url = "http://192.168.0.107:1337/api/v1/audits/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_submit);

        JSONObject audit;

        loadQuestions();
    }

    private void showQuestions(JSONArray questions) {
        int questionCount = questions.length();
        if(questionCount != 0) {
            qTypeArray = new String[questionCount];

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
            addContentView(temp, lmp);
            Log.i("noQ", "onCreate: No questions found");
        }
        submit = new Button(this);
        submit.setBackgroundColor(Color.LTGRAY);
        submit.setText("Submit");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //evalQuestions();
            }
        });
        addContentView(submit, lmp);
    }
/*
    private void evalQuestions() {
        audit = new JSONObject();
        int selectedId;
        String val;
        Boolean val3;
        JSONObject val2;

        if (questionCount != 0) {
            for (int i = 0; i < questionCount; i++) {
                val = new String();
                val2 = new JSONObject();
                switch (qTypeArray[i]) {
                    case "bool":
                        rg = (RadioGroup) checklist.findViewById(i + 100);
                        selectedId = rg.getCheckedRadioButtonId();
                        Log.d("id", selectedId + " Id " + i);
                        if (selectedId / 100 == 4)
                            val3 = true;
                        else
                            val3 = false;
                        try {
                            audit.put(questions.getJSONObject(i).getString("name"), val3);
                        } catch (JSONException e) {
                            Log.e("js", "Invalid JSON string", e);
                        }
                        break;
                    case "boolinfo":
                        rg = (RadioGroup) checklist.findViewById(i + 100);
                        selectedId = rg.getCheckedRadioButtonId();
                        if (selectedId / 100 == 4) {
                            try {
                                val2.put("all", true);
                                val2.put("info", "");
                            } catch (JSONException e) {
                                Log.e("js", "Invalid JSON string: ", e);
                            }
                        }
                        else {
                            try {
                                val2.put("all", false);
                                inp = (EditText) checklist.findViewById(i + 300);
                                val2.put("info", inp.getText());
                            } catch (JSONException e) {
                                Log.e("js", "Invalid JSON string: ", e);
                            }
                        }
                        try {
                            audit.put(questions.getJSONObject(i).getString("name"), val2);
                        } catch (JSONException e) {
                            Log.e("js", "Invalid JSON string: ", e);
                        }
                        break;
                    case "num":
                        float rate;
                        rb = (RatingBar) checklist.findViewById(i + 300);
                        rate = rb.getRating();
                        try {
                            audit.put(questions.getJSONObject(i).getString("name"), rate);
                        } catch (JSONException e) {
                            Log.e("js", "Invalid JSON string: ", e);
                        }
                        break;
                    case "txt":
                        inp = (EditText) checklist.findViewById(i + 300);
                        val = inp.getText().toString();
                        try {
                            audit.put(questions.getJSONObject(i).getString("name"), val);
                        } catch (JSONException e) {
                            Log.e("js", "Invalid JSON string: ", e);
                        }
                        break;
                }
            }
            submitAudit(audit);
        }
    }
*/
    private void loadQuestions() {
        JsonArrayRequest jsArrRequest = new JsonArrayRequest
                (Request.Method.GET, url + "questions", null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Response " + response.length(), response.toString());
                        showQuestions(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());
//                      error.printStackTrace();
                        showQuestions(new JSONArray());
                        Toast.makeText(getApplicationContext(), "Network Error",
                                Toast.LENGTH_LONG).show();
                    }
                });
        helper.add(jsArrRequest);
    }

    private void submitAudit(final JSONObject auditData) {
        Log.d("jsondata",auditData.toString());
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