package com.head.music_ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    public class SeekbarChangrOnListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                player.seekTo(progress);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            player.pause();
            animator.pause();

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            player.start();
            if(seekBar.getProgress()<10){
                animator.start();
            }else {
                animator.resume();
            }

        }
    }

    private TextView tv_sing_name,singer,preview_lyrics,center_lyrics,next_lyrics,total,current;
    private ImageView disk,more,comment,download;
    private SeekBar progress;
    private ImageButton play,review,next,pause;
    private  ObjectAnimator animator;
    private MediaPlayer player;
    private  int currentPlaying=0;
    private ArrayList<Integer> resourceId=new ArrayList<>();
    private  boolean isPausing=false,isPlaying=false;//不在暂停，第一次播放后


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        preparePlaylist();
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                if(isPlaying){
                    upTimer();
                }
            }
        };
        new Timer().scheduleAtFixedRate(timerTask,0,500);

    }
    /**
     * 初始化变量；
     */
    void init(){
        tv_sing_name=findViewById(R.id.tv_sing_name);
        singer=findViewById(R.id.singer);
        preview_lyrics=findViewById(R.id.preview_lyrics);
        center_lyrics=findViewById(R.id.center_lyrics);
        next_lyrics=findViewById(R.id.next_lyrics);
        total=findViewById(R.id.total);
        current=findViewById(R.id.current);
        disk=findViewById(R.id.disk);
        more=findViewById(R.id.more);
        comment=findViewById(R.id.comment);
        download=findViewById(R.id.download);
        progress=findViewById(R.id.progress);
        play=findViewById(R.id.play);
        review=findViewById(R.id.review);
        next=findViewById(R.id.next);

        OnClick onClick=new OnClick();
        review.setOnClickListener(onClick);
        play.setOnClickListener(onClick);
        next.setOnClickListener(onClick);
        SeekbarChangrOnListener seekbarChangrOnListener=new SeekbarChangrOnListener();
        progress.setOnSeekBarChangeListener(seekbarChangrOnListener);

        animator= ObjectAnimator.ofFloat(disk,"rotation",0,360.0F);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);






    }
    private  void   preparePlaylist(){
        Field[] fields=R.raw.class.getFields();
        for(int count=0;count<fields.length;count++){
            Log.i( "Raw aAsset",fields[count].getName());
            try {
                int resId=fields[count].getInt(fields[count]);
                resourceId.add(resId);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private  void  prepareMedia(){
        if(isPlaying){
            player.stop();
            player.reset();
        }
        player=MediaPlayer.create(getApplicationContext(),resourceId.get(currentPlaying));
        int musicDuration=player.getDuration();
        progress.setMax(musicDuration);
        int sec=musicDuration/1000;
        int min=sec/60;
        sec-=min*60;
        String musicTime=String.format("%02d:%02d",min,sec);
        total.setText(musicTime);
        player.start();


    }
    private  void  upTimer(){
        runOnUiThread(()->{
            int currentMs=player.getCurrentPosition();
            int sec=currentMs/1000;
            int min=sec/60;
            sec-=min*60;
            String time=String.format("%02d:%02d",min,sec);
            progress.setProgress(currentMs);
            current.setText(time);

        });
    }

    private  class  OnClick implements View.OnClickListener{

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.review:
                    //上一首
                    play.setImageResource(R.drawable.pause);
                    currentPlaying= --currentPlaying % resourceId.size();
                    animator.start();
                    prepareMedia();
                    isPausing=false;
                    isPlaying=true;

                break;
                case R.id.play:
                    //播放
                    //开始播放
                    if(!isPausing&&!isPlaying){
                        //开始播放
                        play.setImageResource(R.drawable.pause);
                        animator.start();
                        prepareMedia();
                        isPlaying=true;

                    }
                    //继续播放
                    else if(isPausing&&isPlaying) {
                        //继续播放
                        play.setImageResource(R.drawable.pause);
                        animator.resume();
                        player.start();
                    }
                    //暂停播放
                    else
                    {
                        animator.pause();
                        play.setImageResource(R.drawable.play);
                        player.pause();
                    }
                    isPausing=!isPausing;
                    break;
                case R.id.next:
                    //下一首
                    Log.i("INFO", "onClick:下一首被点击 ");
                    play.setImageResource(R.drawable.pause);
                    currentPlaying= ++currentPlaying % resourceId.size();
                    animator.start();
                    prepareMedia();
                    isPausing=false;
                    isPlaying=true;

                    break;

                default:





            }
        }
    }
}