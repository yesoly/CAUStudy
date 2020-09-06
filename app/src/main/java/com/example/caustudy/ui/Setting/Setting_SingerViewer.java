package com.example.caustudy.ui.Setting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.caustudy.R;
import com.example.caustudy.ui.MyStudy.MyStudy_SingerItem;

public class Setting_SingerViewer extends LinearLayout {
    TextView title;

    ImageView imageView;

    public Setting_SingerViewer(Context context) {
        super(context);
        init(context);
    }

    public Setting_SingerViewer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_mystudy,this,true);
        title = (TextView)findViewById(R.id.title);

        imageView = (ImageView)findViewById(R.id.imageView);
    }

    public void setItem(MyStudy_SingerItem singerItem){
        title.setText(singerItem.getTitle());
        imageView.setImageResource(singerItem.getImage());
    }
}