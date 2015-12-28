/**
 * Created by rohit on 24/12/15.
 */
package in.woobus.app.auditor;

import android.content.Context;
import android.graphics.Color;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private final JSONArray questions;
    private View.OnClickListener onButtonClickListener;
    private int TYPE_MAX_COUNT = 7;

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
    private final Answer tmp = new Answer(false, "", "");

    public CustomAdapter(Context context, String[] values, JSONArray questions) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
        this.questions = questions;
        for(int i = 0; i <= questions.length(); i++) {
            answers.add(new Answer(false, "", values[i]));
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final String s = values[position];
        final ViewHolderBool holderBool;
        final ViewHolderText holderText;
        final ViewHolderRate holderRate;
        tmp.s(true, "", s);
        switch (s) {
            case "bool" :
            case "boolinfo" :
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.bool, parent, false);
                    holderBool = new ViewHolderBool(convertView);
                    holderBool.info.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence edit, int start, int before, int count) {}

                        @Override
                        public void afterTextChanged(Editable edit) {
                            answers.set(position, tmp.s(answers.get(position).choice, holderBool.info.getText().toString(), s));
                        }
                    });
                    holderBool.rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            if (holderBool.rbY.getId() == checkedId) {
                                answers.set(position, tmp.s(true, answers.get(position).info, s));
                                if (s.equals("boolinfo")) {
                                    holderBool.info.setVisibility(View.INVISIBLE);
                                }
                            } else {
                                answers.set(position, tmp.s(false, holderBool.info.getText().toString(), s));
                                if (s.equals("boolinfo")) {
                                    holderBool.info.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
                    convertView.setTag(holderBool);
                } else {
                    holderBool = (ViewHolderBool) convertView.getTag();
                }

                holderBool.question.setText(getItem(position));
                holderBool.rbY.setChecked(answers.get(position).choice);
                holderBool.rbN.setChecked(!answers.get(position).choice);
                holderBool.info.setText(answers.get(position).info);

                if (s.equals("bool"))
                    holderBool.info.setVisibility(View.GONE);
                else if (holderBool.rbN.isChecked())
                    holderBool.info.setVisibility(View.VISIBLE);
                else
                    holderBool.info.setVisibility(View.INVISIBLE);
                break;
            case "rate" :
                answers.get(position).info = "0";
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.rate, parent, false);
                    holderRate = new ViewHolderRate(convertView);
                    holderRate.rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                            answers.set(position, tmp.s(true, String.valueOf(holderRate.rating.getRating()), s));
                        }
                    });
                    convertView.setTag(holderRate);
                } else {
                    holderRate = (ViewHolderRate) convertView.getTag();
                }
                holderRate.question.setText(getItem(position));
                holderRate.rating.setRating(Float.valueOf(answers.get(position).info));
                break;
            case "txt" :
            case "num" :
            case "longtxt"  :
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.text, parent, false);
                    holderText = new ViewHolderText(convertView);
                   /* holderText.info.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence edit, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable edit) {
                            answers.set(position, tmp.s(true, holderText.info.getText().toString(), s));
                        }
                    });
                    */
                    convertView.setTag(holderText);
                } else {
                    holderText = (ViewHolderText) convertView.getTag();
                }
                if (s.equals("longtext")) {
                    holderText.info.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                } else if (s.equals("num")) {
                    holderText.info.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                } else {
                    holderText.info.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                }

                holderText.question.setText(getItem(position));
                holderText.info.setText(answers.get(position).info);
                break;
            case "button" :
                convertView = inflater.inflate(R.layout.submit, parent, false);
                TextView txtView = (TextView) convertView.findViewById(R.id.question);
                Button submit = (Button) convertView.findViewById(R.id.button);
                if (questions.length() != 0) {
                    submit.setOnClickListener(this.onButtonClickListener);
                    submit.setBackgroundColor(Color.LTGRAY);
                } else {
                    txtView.setVisibility(TextView.VISIBLE);
                    txtView.setText(R.string.noQ);
                }
                break;
        }
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

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        int r = 3;
        switch (values[position]) {
            case "bool" : r = 0; break;
            case "boolinfo" : r =1; break;
            case "rate" : r = 2; break;
            case "txt" : r = 3; break;
            case "num" : r = 4; break;
            case "longtxt" : r = 5; break;
            case "button" : r = 6; break;
        }
        return r;
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
