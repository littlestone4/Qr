package com.example.proqr;

import android.app.Activity;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class sysPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_page);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String id = bundle.getString("id");
        String name = bundle.getString("name");
        String math = bundle.getString("math");
        String english = bundle.getString("english");
        String chinese = bundle.getString("chinese");

        TextView idShow = (TextView) findViewById(R.id.textViewIDShow);
        TextView nameShow = (TextView) findViewById(R.id.textViewNameShow);
        TextView mathShow = (TextView) findViewById(R.id.textViewMathShow);
        TextView englishShow = (TextView) findViewById(R.id.textViewEnShow);
        TextView chineseShow = (TextView) findViewById(R.id.textViewChShow);
        idShow.setText(id);
        nameShow.setText(name);
        mathShow.setText(math);
        englishShow.setText(english);
        chineseShow.setText(chinese);

    }
}
