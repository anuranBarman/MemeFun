package com.anuranbarman.memefun;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Created by anuran on 5/10/16.
 */

public class MyMeme extends Activity {
    static String meme_name,fileName;
    ImageView img;
    EditText top,bottom;
    Button btn,save,share,another;
    String topText,bottomText;
    static Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.mymeme);
        ctx=getApplicationContext();
        meme_name=getIntent().getExtras().getString("meme_name");
        fileName=meme_name+new Random().nextInt(100)+".jpg";
        img=(ImageView)findViewById(R.id.imgMeme);
        top=(EditText)findViewById(R.id.etTop);
        bottom=(EditText)findViewById(R.id.etBottom);
        btn=(Button)findViewById(R.id.genButton);
        save=(Button)findViewById(R.id.btnSave);
        save.setEnabled(false);
        share=(Button)findViewById(R.id.btnShare);
        another=(Button)findViewById(R.id.btnTry);
        share.setEnabled(false);
        Typeface typeface=Typeface.createFromAsset(getAssets(),"font.ttf");
        btn.setTypeface(typeface);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyMeme.this,"To share the meme save it first.",Toast.LENGTH_LONG).show();
                topText=top.getText().toString();
                bottomText=bottom.getText().toString();
                if(topText.equals("") || bottomText.equals("")){
                    Toast.makeText(MyMeme.this,"Empty Field.Please fill up the text fields.",Toast.LENGTH_SHORT).show();
                    return;
                }
                topText=topText.replace("_","__");
                topText=topText.replace("-","--");
                topText=topText.replace(" ","-");
                topText=topText.replace("?","~q");
                topText=topText.replace("%","~p");
                topText=topText.replace("#","~h");
                topText=topText.replace("/","~s");
                topText=topText.replace("\"","''");

                bottomText=bottomText.replace("_","__");
                bottomText=bottomText.replace("-","--");
                bottomText=bottomText.replace(" ","-");
                bottomText=bottomText.replace("?","~q");
                bottomText=bottomText.replace("%","~p");
                bottomText=bottomText.replace("#","~h");
                bottomText=bottomText.replace("/","~s");
                bottomText=bottomText.replace("\"","''");

                Picasso.with(MyMeme.this).load("https://memegen.link/"+meme_name+"/"+topText+"/"+bottomText+".jpg").into(img);
                save.setEnabled(true);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picasso.with(MyMeme.this).load("https://memegen.link/"+meme_name+"/"+topText+"/"+bottomText+".jpg").into(getTarget(meme_name));
                Toast.makeText(ctx,"Successfully saved the meme in sdcard/Download/ folder.",Toast.LENGTH_SHORT).show();
                share.setEnabled(true);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+File.separator+fileName));
                share.putExtra(Intent.EXTRA_TEXT,"Created with Meme Fun.Download the app from www.anuranbarman.com");
                startActivity(Intent.createChooser(share, "Share Image"));
            }
        });

        another.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MyMeme.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                200);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 200:
                if(grantResults.length<=0 && grantResults[0] !=PackageManager.PERMISSION_GRANTED && grantResults[1] != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            200);
                }
                break;
        }

    }
    private static Target getTarget(String name){
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+"/"+fileName);
                            try {
                                FileOutputStream ostream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                                ostream.flush();
                                ostream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }

        } ;
        return target;
    }


}
