package com.anuranbarman.memefun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by anuran on 5/10/16.
 */

public class Memes extends Activity {
    String[] memeArray;
    GridView gridView;
    TextView memeHeader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.memes);
        memeArray=getIntent().getExtras().getStringArray("meme_array");
        memeHeader=(TextView)findViewById(R.id.memeHeader);
        Typeface type=Typeface.createFromAsset(getAssets(),"font.ttf");
        memeHeader.setTypeface(type);
        gridView=(GridView)findViewById(R.id.gridview);
        gridView.setAdapter(new ImageAdapter(this));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(Memes.this,MyMeme.class);
                Bundle b = new Bundle();
                b.putString("meme_name",memeArray[position]);
                i.putExtras(b);
                startActivity(i);
                finish();
            }
        });
    }

    class ImageAdapter extends BaseAdapter{
        Context mContext;
        private ImageAdapter(Context c){
            mContext=c;
        }

        @Override
        public int getCount() {
            return memeArray.length;
        }

        @Override
        public Object getItem(int position) {
            return memeArray[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null){
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(180, 180));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setBackgroundResource(R.drawable.meme_grid_bg);
                imageView.setPadding(3, 3, 3, 3);
            }
            else{
                imageView = (ImageView) convertView;
            }

            Picasso.with(Memes.this).load("https://memegen.link/"+memeArray[position]+"/your-text/goes-here.jpg").into(imageView);
            return imageView;
        }
    }
}
