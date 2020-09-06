package com.example.caustudy.ui.MyStudy;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.caustudy.R;

public class MyStudy_SingerViewer  extends LinearLayout {
    TextView title;
    TextView period;
    TextView time;
    TextView organization;
    ImageView imageView;

    public MyStudy_SingerViewer(Context context) {
        super(context);
        init(context);
    }

    public MyStudy_SingerViewer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_mystudy,this,true);
        title = (TextView)findViewById(R.id.title);
        period = (TextView)findViewById(R.id.period);
        time = (TextView)findViewById(R.id.time);
        organization = (TextView)findViewById(R.id.organization);

        imageView = (ImageView)findViewById(R.id.imageView);
    }

    public void setItem(MyStudy_SingerItem singerItem){
        title.setText(singerItem.getTitle());
        period.setText(singerItem.getPeriod());
        time.setText(singerItem.getTime());
        organization.setText(singerItem.getOrganization());
        imageView.setImageResource(singerItem.getImage());
    }
}
