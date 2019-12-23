package com.app.timemanager;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.view.ViewGroup;
import androidx.viewpager.widget.PagerAdapter;

public class Guide extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private ViewPager vp;
    private List<View> views;
    private  Handler handler;
    private Button start_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);
        initViews();
    }

    private void initViews() {
        vp = (ViewPager) findViewById(R.id.viewpager);
        LayoutInflater inflater = LayoutInflater.from(this);

        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.one, null));
        PagerAdapter vpAdapter = new PagerAdapter(){
            public int getCount(){
                return views.size();
            }
            public boolean isViewFromObject(View view,  Object o){
                return view==o;
            }
            public void destroyItem(ViewGroup container,int position,Object object){
                container.removeView(views.get(position));
            }
            public Object instantiateItem(ViewGroup container, int position){
                container.addView(views.get(position));
                return  views.get(position);
            }
        };
        vp.setAdapter(vpAdapter);
        start_btn = (Button) views.get(0).findViewById(R.id.start_btn);
        start_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(Guide.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        vp.setOnPageChangeListener(this);
    }


    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub
    }

}