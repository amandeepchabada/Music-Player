package miniproject.com.musicproject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private ImageButton play,pause,next,previous;
    private MediaPlayer mediaPlayer;
    private double startTime = 0;
    private double endTime = 0;
    private ImageView nowPlayingImage;
    private Handler myHandler = new Handler();
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private SeekBar seekBar;
    private TextView timeStart, timeEnd, songName;
    public static int oneTimeOnly = 0;
    private ListView  playlistView;
    int count = 0;
    private static final Integer[] Songs ={R.raw.always,R.raw.bothofus,R.raw.madworld,R.raw.shotgun,R.raw.sofaraway,R.raw.strangerthings};
    private static final Integer[] Thumbnails = {R.drawable.always,R.drawable.bothofus,R.drawable.madworld,R.drawable.shotgun,R.drawable.sofaraway,R.drawable.strangerthings};
    private static final String[] songNames = {"Always","Both Of Us","Mad World","Shotgun","So Far away","Stranger Things"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get view data
        playlistView =(ListView) findViewById(R.id.playlistView);
        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        timeStart = findViewById(R.id.timeStart);
        timeEnd = findViewById(R.id.timeEnd);
        songName = findViewById(R.id.songName);
        nowPlayingImage = findViewById(R.id.nowPlayingImage);
        mediaPlayer = MediaPlayer.create(this, Songs[0]);
        setSongData(0);
        seekBar = findViewById(R.id.songProgressBar);
        seekBar.setClickable(false);
        pause.setEnabled(false);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10, 0);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.list,songNames);
        playlistView.setAdapter(adapter);
        playlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playMusic(position);
                count=position;
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                endTime = mediaPlayer.getDuration();
                startTime = mediaPlayer.getCurrentPosition();

                if (oneTimeOnly == 0) {
                    seekBar.setMax((int)endTime);
                    oneTimeOnly = 1;
                }
                timeEnd.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes((long) endTime), TimeUnit.MILLISECONDS.toSeconds((long) endTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) endTime)))
                );

                timeStart.setText(String.format("%d:%d",TimeUnit.MILLISECONDS.toMinutes((long) startTime),TimeUnit.MILLISECONDS.toSeconds((long) startTime) -TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))));
                seekBar.setProgress((int) startTime);
                myHandler.postDelayed(UpdateSongTime, 100);
                play.setEnabled(false);
                pause.setEnabled(true);
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
                play.setEnabled(true);
                pause.setEnabled(false);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count<5)
                {
                    count++;
                }
                else
                    count=0;
                playMusic(count);
            }
        });
       next.setOnLongClickListener(new View.OnLongClickListener(){
           @Override
           public boolean onLongClick(View v) {
               int temp = (int)startTime;
               if((temp+forwardTime)<=endTime){
                   startTime = startTime + forwardTime;
                   mediaPlayer.seekTo((int) startTime);
               }
               return true;
           }
       });

       previous.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(count>0)
               {
                   count--;
               }
               else
                   count=0;
               playMusic(count);
           }
       });

        previous.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int temp = (int)startTime;
                if((temp-backwardTime)>0){
                    startTime = startTime - backwardTime;
                    mediaPlayer.seekTo((int) startTime);
                }
                return true;
            }
        });

    }
    public void playMusic(int songIndex){
        mediaPlayer.reset();
        mediaPlayer = MediaPlayer.create(getApplicationContext(),Songs[songIndex]);
        setSongData(songIndex);
        mediaPlayer.start();
        endTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();
        if (oneTimeOnly == 0) {
            seekBar.setMax((int)endTime);
            oneTimeOnly = 1;
        }
        timeEnd.setText(String.format("%d:%d ",
                TimeUnit.MILLISECONDS.toMinutes((long) endTime), TimeUnit.MILLISECONDS.toSeconds((long) endTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) endTime)))
        );

        timeStart.setText(String.format("%d:%d ",TimeUnit.MILLISECONDS.toMinutes((long) startTime),TimeUnit.MILLISECONDS.toSeconds((long) startTime) -TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))));
        seekBar.setProgress((int) startTime);
        myHandler.postDelayed(UpdateSongTime, 100);
        play.setEnabled(false);
        pause.setEnabled(true);
    }
    public void setSongData(int count)
    {
        Drawable d = getResources().getDrawable(Thumbnails[count]);
        nowPlayingImage.setImageDrawable(d);
        songName.setText(songNames[count]);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mediaPlayer.release();
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            timeStart.setText(String.format("%d:%d ",TimeUnit.MILLISECONDS.toMinutes((long) startTime),TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))));
            seekBar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };
}
