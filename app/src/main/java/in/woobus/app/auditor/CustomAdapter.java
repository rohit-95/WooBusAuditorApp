/**
 * Created by rohit on 24/12/15.
 */
package in.woobus.app.auditor;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

public class CustomAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private final JSONArray questions;
    private View.OnClickListener onButtonClickListener;

    public CustomAdapter(Context context, String[] values, JSONArray questions) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
        this.questions = questions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = new View(context);
        TextView txtView;
        RadioGroup rg;
        Boolean found = true;

        String s = values[position];
        rowView.setTag(s);

        switch (s) {
            case "bool" :
                rowView = inflater.inflate(R.layout.bool, parent, false);
                rowView.setTag(s);
                break;
            case "boolinfo" :
                rowView = inflater.inflate(R.layout.bool, parent, false);
                rg = (RadioGroup) rowView.findViewById(R.id.rgChoices);
                final EditText inp = (EditText) rg.findViewById(R.id.info);
                inp.setVisibility(View.INVISIBLE);
                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if ( ((RadioButton) group.findViewById(R.id.rbNo)).isChecked() ) {
                            inp.setVisibility(EditText.VISIBLE);
                        } else {
                            inp.setVisibility(EditText.INVISIBLE);
                        }
                    }
                });
                break;
            case "rate" :
                rowView = inflater.inflate(R.layout.rate, parent, false);
                break;
            case "txt"  :
                rowView = inflater.inflate(R.layout.text, parent, false);
                break;
            case "num" :
                rowView = inflater.inflate(R.layout.rate, parent, false);
                break;
            default :
                rowView = inflater.inflate(R.layout.submit, parent, false);
                found = false;
                txtView = (TextView) rowView.findViewById(R.id.question);
                if (s == "found") {
                    txtView.setVisibility(TextView.GONE);
                    Button submit = (Button) rowView.findViewById(R.id.button);
                    submit.setOnClickListener(this.onButtonClickListener);
                }
                else {
                    txtView.setText(R.string.noQ);
                }
        }
        if(found) {
            txtView = (TextView) rowView.findViewById(R.id.question);
            try {
                txtView.setText(questions.getJSONObject(position).getString("question"));
            } catch (JSONException e) {
                Log.e("js", "Invalid JSON string", e);
            }
        }
        if (convertView == null) {
            return rowView;
        }
        else {

        }
    }

    public void setOnButtonClickListener (final View.OnClickListener listener) {
        this.onButtonClickListener = listener;
    }
}
