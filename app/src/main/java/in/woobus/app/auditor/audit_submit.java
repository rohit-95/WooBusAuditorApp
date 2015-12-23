package in.woobus.app.auditor;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class audit_submit extends AppCompatActivity {
    LinearLayout checklist;
    LinearLayout row, col;
    int questionCount = 0;
    JSONArray questions;
    JSONObject audit;
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

    final static String urlG = "http://192.168.56.1:1337/api/v1/audits/questions";
    final static String urlP = "http://192.168.56.1:1337/api/v1/audits/add";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_submit);

        checklist = (LinearLayout)findViewById(R.id.cl1);
        checklist.setOrientation(LinearLayout.VERTICAL);

        loadQuestions();

        submit = new Button(this);
        submit.setLayoutParams(lwc);
        submit.setBackgroundColor(Color.LTGRAY);
        submit.setText("Submit");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evalQuestions();
            }
        });
    }

    private void showQuestions() {
        if(questionCount != 0) {
            qTypeArray = new String[questionCount];

            for(int i = 0; i < questionCount; i++) {
                final int j = i;
                col = new LinearLayout(getApplicationContext());
                col.setOrientation(LinearLayout.VERTICAL);

                temp = new TextView(getApplicationContext());
                try {
                    temp.setText(questions.getJSONObject(i).getString("question"));
                } catch (JSONException e) {
                    Log.e("js", "Invalid JSON string", e);
                }

                col.addView(temp);

                try {
                    qTypeArray[i] = questions.getJSONObject(i).getString("q_type");
                } catch (JSONException e) {
                    Log.e("js", "Invalid JSON string", e);
                }
                switch (qTypeArray[i]) {
                    case "bool" :
                        rg = new RadioGroup(getApplicationContext());
                        rg.setOrientation(RadioGroup.HORIZONTAL);
                        yes = new RadioButton(getApplicationContext()); no = new RadioButton(getApplicationContext());
                        yes.setText("Yes"); no.setText("No");
                        //yes.setTextColor(Color.BLACK);no.setTextColor(Color.BLACK);
                        yes.setId(i + 400);no.setId(i + 500);
                        rg.addView(yes); rg.addView(no);
                        rg.setId(i + 100);
                        col.addView(rg);
                        checklist.addView(col);
                        break;
                    case "boolinfo" :
                        rg = new RadioGroup(getApplicationContext());
                        rg.setOrientation(RadioGroup.HORIZONTAL);

                        yes = new RadioButton(getApplicationContext()); no = new RadioButton(getApplicationContext());
                        yes.setText("Yes"); no.setText("No");
                        //yes.setTextColor(Color.BLACK);no.setTextColor(Color.BLACK);
                        yes.setId(i + 400);no.setId(i + 500);
                        rg.addView(yes); rg.addView(no);
                        rg.setId(i + 100);

                        row = new LinearLayout(getApplicationContext());
                        row.setOrientation(LinearLayout.HORIZONTAL);
                        row.setLayoutParams(lmp);
                        row.addView(rg);

                        inp = new EditText(getApplicationContext());
                        inp.setLayoutParams(lmp);
                        inp.setId(i + 300);
                        inp.setVisibility(View.GONE);
                        row.addView(inp);

                        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                if (checkedId / 100 == 5) {
                                    inp = (EditText) checklist.findViewById(group.getId() % 10 + 300);
                                    inp.setVisibility(View.VISIBLE);
                                } else {
                                    inp = (EditText) checklist.findViewById(group.getId() % 10 + 300);
                                    inp.setVisibility(View.GONE);
                                }
                            }
                        });

                        col.addView(row);
                        checklist.addView(col);
                        break;
                    case "num":
                        rb = new RatingBar(getApplicationContext());
                        rb.setNumStars(5);
                        rb.setLayoutParams(lwc);
                        rb.setId(i + 300);
                        col.addView(rb);
                        checklist.addView(col);
                        break;
                    case "txt"  :
                        inp = new EditText(getApplicationContext());
                        inp.setLayoutParams(lmp);
                        inp.setId(i + 300);
                        col.addView(inp);
                        checklist.addView(col);
                        break;
                }
                LinearLayout space = new LinearLayout(getApplicationContext());
                space.setLayoutParams(lwc);
                space.setMinimumHeight(20);
                checklist.addView(space);
            }
        }
        else {
            temp = new TextView(getApplicationContext());
            temp.setText("No Questions on server");
            checklist.addView(temp);
            Log.i("noQ", "onCreate: No questions found");
        }
        checklist.addView(submit);
    }

    private void evalQuestions() {
        if (questionCount != 0) {
            audit = new JSONObject();
            int selectedId;
            String val;
            Boolean val3;
            JSONObject val2;
            for (int i = 0; i < questionCount; i++) {
                val = new String();
                val2 = new JSONObject();
                switch (qTypeArray[i]) {
                    case "bool":
                        rg = (RadioGroup) checklist.findViewById(i + 100);
                        selectedId = rg.getCheckedRadioButtonId();
                        if ((int) (selectedId / 100) == 4)
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
                        if ((int) (selectedId / 100) == 4) {
                            try {
                                val2.put("all", true);
                                val2.put("all", "");
                            } catch (JSONException e) {
                                Log.e("js", "Invalid JSON string: ", e);
                            }
                        } else {
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
            temp = new TextView(getApplicationContext());
            temp.setId(10000);
            checklist.addView(temp);
            submitAudit(audit);
        }
    }

    private void loadQuestions() {
        JsonArrayRequest jsArrRequest = new JsonArrayRequest
                (Request.Method.GET, urlG, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        questionCount = response.length();
                        questions = response;
                        Log.e("Response " + questionCount, questions.toString());
                        showQuestions();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());
//                      error.printStackTrace();
                    }
                });
        helper.add(jsArrRequest);
    }

    private void submitAudit(final JSONObject auditData) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, urlP, auditData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        temp = (TextView)checklist.findViewById(10000);
                        temp.setText("Submitted Successfully");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: " + auditData.toString(), error.getMessage());
                        temp = (TextView)checklist.findViewById(10000);
                        temp.setText("Submission Unsuccessful. Please try again.");
                    }
                });
        helper.add(jsObjRequest);
    }
}