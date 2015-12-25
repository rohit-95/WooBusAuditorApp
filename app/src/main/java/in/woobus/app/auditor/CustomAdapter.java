/**
 * Created by rohit on 24/12/15.
 */
package in.woobus.app.auditor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private final JSONArray questions;
    private View.OnClickListener onButtonClickListener;

    private class Answer {
        public Boolean choice;
        public String info;

        public Answer(Boolean c, String i) {
            this.choice = c;
            this.info = i;
        }
    }

    private ArrayList<Answer> answers;

    public CustomAdapter(Context context, String[] values, JSONArray questions) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
        this.questions = questions;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder = null;
        TextView txtView;
        RadioGroup rg;
        RadioButton rb;

        Boolean found = true;

        String s = values[position];

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row, parent, false);
            switch (s) {
                case "bool":
                    rb = (RadioButton) convertView.findViewById(R.id.rbYes);
                    rb.setVisibility(View.VISIBLE);
                    rb = (RadioButton) convertView.findViewById(R.id.rbNo);
                    rb.setVisibility(View.VISIBLE);
                    rg = (RadioGroup) convertView.findViewById(R.id.rgChoices);
                    rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            if (rb.isChecked()) {
                                answers[position].choice = false;
                            } else {
                                answers[position].choice = true;
                            }
                        }
                    });
                    break;
                case "boolinfo":
                    rb = (RadioButton) convertView.findViewById(R.id.rbYes);
                    rb.setVisibility(View.VISIBLE);
                    rb = (RadioButton) convertView.findViewById(R.id.rbNo);
                    rb.setVisibility(View.VISIBLE);
                    rg = (RadioGroup) convertView.findViewById(R.id.rgChoices);
                    final EditText inp = (EditText) rg.findViewById(R.id.info);
                    inp.setVisibility(View.INVISIBLE);
                    rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            if (rb.isChecked()) {
                                inp.setVisibility(EditText.VISIBLE);
                            } else {
                                inp.setVisibility(EditText.INVISIBLE);
                            }
                        }
                    });
                    break;
                case "rate":
                    break;
                case "txt":
                    break;
                case "num":
                    break;
                default:
                    convertView = inflater.inflate(R.layout.submit, parent, false);
                    found = false;
                    txtView = (TextView) convertView.findViewById(R.id.question);
                    if (s == "found") {
                        txtView.setVisibility(TextView.GONE);
                        Button submit = (Button) convertView.findViewById(R.id.button);
                        submit.setOnClickListener(this.onButtonClickListener);
                    } else {
                        txtView.setText(R.string.noQ);
                    }
            }
            if (found) {
                txtView = (TextView) convertView.findViewById(R.id.question);
                txtView.setText(getItem(position));
            }
        } else {

        }
        return convertView
    }

    public void setOnButtonClickListener (final View.OnClickListener listener) {
        this.onButtonClickListener = listener;
    }

    @Override
    public int getViewTypeCount() {

    }

    @Override
    public String getItem(int position) {
        try {
            return questions.getJSONObject(position).getString("question"));
        } catch (JSONException e) {
            Log.e("js", "Invalid JSON string", e);
        }
        return "No Question Found";
    }

    public static class ViewHolder {
        public TextView question;
        public RatingBar rating;
        public RadioGroup rg;
    }
}
