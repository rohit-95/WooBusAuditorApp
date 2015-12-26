/**
 * Created by rohit on 24/12/15.
 */
package in.woobus.app.auditor;

import android.content.Context;
import android.content.DialogInterface;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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

import com.android.volley.ParseError;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private final JSONArray questions;
    private View.OnClickListener onButtonClickListener;

    public class Answer {
        public Boolean choice = null;
        public String info = null;
        public String type = null;

        public Answer (Boolean b, String i, String t) {
            this.choice = b;
            this.info = i;
            this.type = t;
        }

        public Answer s(Boolean b, String i, String t) {
            this.choice = b;
            this.info = i;
            this.type = t;
            return this;
        }

        @Override
        public String toString() {
            return choice + " " + info + " " + type;
        }
    }

    private final List<Answer> answers = new ArrayList<>();

    public CustomAdapter(Context context, String[] values, JSONArray questions) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
        this.questions = questions;
        for(int i = 0; i < questions.length(); i++) {
            answers.add(new Answer(false, "0", values[i]));
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final Answer tmp = new Answer(true, "", "");
        final String s = values[position];
        final ViewHolderBool holderBool;
        final ViewHolderText holderText;
        final ViewHolderRate holderRate;
        Log.d("arr before", answers.get(position).toString() + " " + position);
        //Log.d("array", answers.get(position).toString() + " " + s + " " + position);
        switch (s) {
            case "bool" :
                if (convertView == null || ((ViewHolder)convertView.getTag()).type != "bool") {
                    convertView = inflater.inflate(R.layout.bool, parent, false);
                    holderBool = new ViewHolderBool(convertView);
                    holderBool.question.setText(getItem(position));
                    holderBool.rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            if (holderBool.rbN.isChecked()) {
                                answers.set(position, tmp.s(true, "", s));
                            } else {
                                answers.set(position, tmp.s(false, "", s));
                            }
                        }
                    });
                    convertView.setTag(holderBool);
                } else {
                    holderBool = (ViewHolderBool) convertView.getTag();
                }
                if (answers.get(position) != null) {
                    holderBool.rbY.setChecked(answers.get(position).choice);
                    holderBool.rbN.setChecked(!answers.get(position).choice);
                }
                break;
            case "boolinfo" :
                if (convertView == null || ((ViewHolder)convertView.getTag()).type != "bool") {
                    convertView = inflater.inflate(R.layout.bool, parent, false);
                    holderBool = new ViewHolderBool(convertView);
                    holderBool.rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            if (holderBool.rbY.isChecked()) {
                                answers.set(position, tmp.s(true, "", s));
                                Log.d("changed", answers.get(position).toString() + " " + position);
                                holderBool.info.setVisibility(View.INVISIBLE);
                            } else {
                                holderBool.info.setVisibility(View.VISIBLE);
                                answers.set(position, tmp.s(false, holderBool.info.getText().toString(), s));
                                Log.d("changed", answers.get(position).toString() + " " + position);
                            }
                        }
                    });
                    holderBool.info.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable edit) {
                            answers.set(position, tmp.s(false, holderBool.info.getText().toString(), s));
                        }
                    });
                    convertView.setTag(holderBool);
                } else {
                    holderBool = (ViewHolderBool) convertView.getTag();
                }
                holderBool.question.setText(getItem(position));
                if (answers.get(position) != null) {
                    Log.d("changed", answers.get(position).toString() + " " + position);
                    holderBool.rbY.setChecked(answers.get(position).choice);
                    holderBool.rbN.setChecked(!answers.get(position).choice);
                    holderBool.info.setText(answers.get(position).info);
                }
                break;
            case "rate" :
                if (convertView == null || ((ViewHolder)convertView.getTag()).type != "rate") {
                    convertView = inflater.inflate(R.layout.rate, parent, false);
                    holderRate = new ViewHolderRate(convertView);
                    holderRate.rating.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            answers.set(position, tmp.s(true, String.valueOf(holderRate.rating.getRating()), s));
                        }
                    });
                    convertView.setTag(holderRate);
                } else {
                    holderRate = (ViewHolderRate) convertView.getTag();
                }
                holderRate.question.setText(getItem(position));
                if (answers.get(position) != null) {
                    holderRate.rating.setRating(Float.valueOf(answers.get(position).info));
                }
                break;
            case "txt"  :
                if (convertView == null || ((ViewHolder)convertView.getTag()).type != "txt") {
                    convertView = inflater.inflate(R.layout.text, parent, false);
                    holderText = new ViewHolderText(convertView);
                    holderText.info.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}

                        @Override
                        public void afterTextChanged(Editable edit) {
                            answers.set(position, tmp.s(true, holderText.info.getText().toString(), s));
                        }
                    });
                    convertView.setTag(holderText);
                } else {
                    holderText = (ViewHolderText) convertView.getTag();
                }
                holderText.question.setText(getItem(position));
                if (answers.get(position) != null) {
                    holderText.info.setText(answers.get(position).info);
                }
                break;
            case "num" :
                if (convertView == null  || ((ViewHolder)convertView.getTag()).type != "txt") {
                    convertView = inflater.inflate(R.layout.text, parent, false);
                    holderText = new ViewHolderText(convertView);
                    holderText.info.setInputType(InputType.TYPE_CLASS_NUMBER);
                    holderText.info.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}

                        @Override
                        public void afterTextChanged(Editable edit) {
                            answers.set(position, tmp.s(true, holderText.info.getText().toString(), s));
                        }
                    });
                    convertView.setTag(holderText);
                } else {
                    holderText = (ViewHolderText) convertView.getTag();
                }
                holderText.question.setText(getItem(position));
                if (answers.get(position) != null) {
                    holderText.info.setText(answers.get(position).info);
                }
                break;
            case "button" :
                convertView = inflater.inflate(R.layout.submit, parent, false);
                TextView txtView = (TextView) convertView.findViewById(R.id.question);
                if (questions.length() != 0) {
                    Button submit = (Button) convertView.findViewById(R.id.button);
                    submit.setOnClickListener(this.onButtonClickListener);
                }
                else {
                    txtView.setVisibility(TextView.VISIBLE);
                    txtView.setText(R.string.noQ);
                }
                break;
        }
        Log.d("arr after", answers.get(position).toString() + " " + position);
        return convertView;
    }

    @Override
    public String getItem(int position) {
        try {
            return questions.getJSONObject(position).getString("question");
        } catch (JSONException e) {
            Log.e("js", "Invalid JSON string", e);
        }
        return "No questions found";
    }

    public void setOnButtonClickListener (final View.OnClickListener listener) {
        this.onButtonClickListener = listener;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    static class ViewHolder {
        public TextView question;
        public String type;
        public int position;
    }

    static class ViewHolderBool extends ViewHolder {
        public RadioGroup rg;
        public RadioButton rbN, rbY;
        public EditText info;

        public ViewHolderBool(View view) {
            question = (TextView) view.findViewById(R.id.question);
            rg = (RadioGroup) view.findViewById(R.id.rgChoices);
            rbN = (RadioButton) view.findViewById(R.id.rbNo);
            rbY = (RadioButton) view.findViewById(R.id.rbYes);
            info = (EditText) view.findViewById(R.id.info);
            type = "bool";
        }
    }

    static class ViewHolderRate extends ViewHolder{
        public RatingBar rating;

        public ViewHolderRate(View view) {
            question = (TextView) view.findViewById(R.id.question);
            rating = (RatingBar) view.findViewById(R.id.ratingBar);
            type = "rate";
        }
    }
    static class ViewHolderText extends ViewHolder{
        public EditText info;

        public ViewHolderText(View view) {
            question = (TextView) view.findViewById(R.id.question);
            info = (EditText) view.findViewById(R.id.info);
            type = "txt";
        }
    }

    /*static class ViewHolderButton {
        public TextView txt;
        public Button submit;

        public ViewHolderButton(View view) {
            txt = (TextView) view.findViewById(R.id.question);
            submit = (Button) view.findViewById(R.id.button);
        }
    }*/
}
