package com.example.projekt_lk_00;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.projekt_lk_00.pojo.Answer;

public class MojAdapter extends ArrayAdapter<Answer> {
    private final Context context;
    private final Answer[] answers;
    public MojAdapter(Context context, Answer[] answers){
        super(context, android.R.layout.simple_list_item_2,answers);
        this.context = context;
        this.answers = answers;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
        TextView username = (TextView) rowView.findViewById(android.R.id.text1);
        TextView answer_text  = (TextView) rowView.findViewById(android.R.id.text2);
        username.setText(answers[position].getUsername());
        answer_text.setText(answers[position].getAnswer_text());
        Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
        username.setTypeface(boldTypeface);
        return rowView;
    }

}
