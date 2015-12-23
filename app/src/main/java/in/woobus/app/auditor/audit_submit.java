package in.woobus.app.auditor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

    LinearLayout checklist = new LinearLayout(this);
    LinearLayout row;
    int questionCount = 0;
    JSONArray questions;
    JSONObject audit;
    TextView[] questionArray;
    String[] qTypeArray;
    TextView temp;
    RadioButton yes, no;
    RadioGroup rg;
    EditText inp;
    Button submit;


    LinearLayout.LayoutParams lwc = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    LinearLayout.LayoutParams lmp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

    FetchQuestions helper = FetchQuestions.getInstance();

    final static String url = "http://localhost:1337/api/v1/audits/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_submit);

        loadQuestions();

        if(questionCount != 0) {
            questionArray = new TextView[questionCount];
            qTypeArray = new String[questionCount];

            for(int i = 0; i < questionCount; i++) {
                final int j = i;
                row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);

                temp = new TextView(this);
                try {
                    temp.setText(questions.getJSONObject(i).getString("question"));
                } catch (JSONException e) {
                    Log.e("js", "Invalid JSON string", e);
                }
                row.addView(temp);
                questionArray[i] = temp;

                try {
                    qTypeArray[i] = questions.getJSONObject(i).getString("q_type");
                } catch (JSONException e) {
                    Log.e("js", "Invalid JSON string", e);
                }
                switch (qTypeArray[i]) {
                    case "bool" :
                        rg = new RadioGroup(this);
                        rg.setOrientation(RadioGroup.HORIZONTAL);
                        yes = new RadioButton(this); no = new RadioButton(this);
                        yes.setText("Yes"); no.setText("No");
                        yes.setId(i + 400);no.setId(i + 500);
                        rg.addView(yes); rg.addView(no);
                        rg.setId(i + 100);
                        row.addView(rg);
                        checklist.addView(row);
                        break;
                    case "binfo" :
                        rg = new RadioGroup(this);
                        rg.setOrientation(RadioGroup.HORIZONTAL);
                        yes = new RadioButton(this); no = new RadioButton(this);
                        yes.setText("Yes"); no.setText("No");
                        yes.setId(i + 400);no.setId(i + 500);
                        no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TextView tmp1 = new TextView(getApplicationContext());
                                tmp1.setText("Seat Numbers: ");
                                row.addView(tmp1);
                                inp = new EditText(getApplicationContext());
                                inp.setLayoutParams(lmp);
                                inp.setId(j + 300);
                                row.addView(inp);
                            }
                        });
                        rg.addView(yes); rg.addView(no);
                        rg.setId(i + 100);
                        row.addView(rg);
                        checklist.addView(row);
                        break;
                    case "2bool":
                        TextView tmp1 = new TextView(this);
                        try {
                            tmp1.setText(questions.getJSONObject(i).getJSONObject("info").getString("1"));
                        } catch (JSONException e) {
                            Log.e("js", "Invalid JSON string", e);
                        }
                        row.addView(tmp1);

                        rg = new RadioGroup(this);
                        rg.setOrientation(RadioGroup.HORIZONTAL);
                        yes = new RadioButton(this); no = new RadioButton(this);
                        yes.setText("Yes"); no.setText("No");
                        yes.setId(i + 400);no.setId(i + 500);
                        rg.addView(yes); rg.addView(no);
                        rg.setId(i + 100);
                        row.addView(rg);
                        checklist.addView(row);

                        TextView tmp2 = new TextView(this);
                        try {
                            tmp2.setText(questions.getJSONObject(i).getJSONObject("info").getString("1"));
                        } catch (JSONException e) {
                            Log.e("js", "Invalid JSON string", e);
                        }
                        row.addView(tmp1);

                        rg = new RadioGroup(this);
                        rg.setOrientation(RadioGroup.HORIZONTAL);
                        yes = new RadioButton(this); no = new RadioButton(this);
                        yes.setText("Yes"); no.setText("No");
                        yes.setId(i + 400);no.setId(i + 500);
                        rg.addView(yes); rg.addView(no);
                        rg.setId(i + 200);
                        row.addView(rg);
                        checklist.addView(row);
                        break;
                    case "num"  :
                        inp = new EditText(this);
                        inp.setLayoutParams(lmp);
                        inp.setId(i + 300);
                        row.addView(inp);
                        checklist.addView(row);
                        break;
                    case "txt"  :
                        inp = new EditText(this);
                        inp.setLayoutParams(lmp);
                        inp.setId(i + 300);
                        row.addView(inp);
                        checklist.addView(row);
                        break;
                }
            }
            submit = new Button(this);
            submit.setLayoutParams(lwc);
            checklist.addView(submit);
        }
        else {
            temp = new TextView(this);
            temp.setText("No Questions on server");
            checklist.addView(temp);
            Log.i("noQ", "onCreate: No questions found");
        }
        checklist.setId(R.id.cl1);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audit = new JSONObject();
                int selectedId;
                String val;
                Boolean val3;
                JSONObject val2;
                for(int i = 0; i < questionCount; i++) {
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
                        case "binfo":
                            rg = (RadioGroup) checklist.findViewById(i + 100);
                            selectedId = rg.getCheckedRadioButtonId();
                            if ((int) (selectedId / 100) == 4) {
                                try {
                                    val2.put("all", true);
                                    val2.put("all", "");
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
                        case "2bool":
                            rg = (RadioGroup) checklist.findViewById(i + 100);
                            selectedId = rg.getCheckedRadioButtonId();
                            if ((int) (selectedId / 100) == 4)
                                val3 = true;
                            else
                                val3 = false;
                            try {
                                val2.put(questions.getJSONObject(i).getJSONObject("info").getString("1"), val3);
                            } catch (JSONException e) {
                                Log.e("js", "Invalid JSON string: ", e);
                            }

                            rg = (RadioGroup) checklist.findViewById(i + 200);
                            selectedId = rg.getCheckedRadioButtonId();
                            if ((int) (selectedId / 100) == 4)
                                val3 = true;
                            else
                                val3 = false;
                            try {
                                val2.put(questions.getJSONObject(i).getJSONObject("info").getString("2"), val3);
                            } catch (JSONException e) {
                                Log.e("js", "Invalid JSON string: ", e);
                            }
                            try {
                                audit.put(questions.getJSONObject(i).getString("name"), val2);
                            } catch (JSONException e) {
                                Log.e("js", "Invalid JSON string: ", e);
                            }
                            break;
                        case "txt" :
                        case "num" :
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
                checklist = new LinearLayout(getApplicationContext());
                temp = new TextView(getApplicationContext());
                temp.setText("Submitted Successfully");
                checklist.addView(temp);
                checklist.setId(R.id.cl1);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void loadQuestions() {
        JsonArrayRequest jsArrRequest = new JsonArrayRequest
                (Request.Method.GET, url + "questions", null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        questionCount = response.length();
                        questions = response;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());
                    }
                });
        helper.add(jsArrRequest);
    }

    private void submitAudit(JSONObject auditData) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url + "add", auditData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());
                    }
                });
        helper.add(jsObjRequest);
    }
}