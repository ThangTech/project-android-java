package com.example.project_android_java.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.example.project_android_java.R;

public class SoundManager {

    private static SoundManager instance;
    private Context context;
    private SharedPreferences prefs;

    private SoundPool soundPool;
    private int soundCorrect, soundWrong, soundClick, soundWin, soundLose;
    private boolean isSoundEnabled = true;

    private MediaPlayer introPlayer;

    private SoundManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences("GameSettings", Context.MODE_PRIVATE);
    }

    public static SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context);
        }
        return instance;
    }

    public void initSoundPool() {
        soundPool = new SoundPool(5, android.media.AudioManager.STREAM_MUSIC, 0);
        soundCorrect = soundPool.load(context, R.raw.correct, 1);
        soundWrong = soundPool.load(context, R.raw.wrong, 1);
        soundClick = soundPool.load(context, R.raw.click, 1);
        soundWin = soundPool.load(context, R.raw.win, 1);
        soundLose = soundPool.load(context, R.raw.lose, 1);
    }

    public void playCorrect() {
        if (isSoundEnabled && soundCorrect != 0) {
            soundPool.play(soundCorrect, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    public void playWrong() {
        if (isSoundEnabled && soundWrong != 0) {
            soundPool.play(soundWrong, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    public void playClick() {
        if (isSoundEnabled && soundClick != 0) {
            soundPool.play(soundClick, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    public void playWin() {
        if (isSoundEnabled && soundWin != 0) {
            soundPool.play(soundWin, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    public void playLose() {
        if (isSoundEnabled && soundLose != 0) {
            soundPool.play(soundLose, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    public void releaseSoundPool() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    public void setSoundEnabled(boolean enabled) {
        this.isSoundEnabled = enabled;
    }

    public void playIntroMusic(boolean loop) {
        isSoundEnabled = prefs.getBoolean("music_enabled", true);
        if (isSoundEnabled) {
            stopIntroMusic();
            introPlayer = MediaPlayer.create(context, R.raw.intro_music);
            if (introPlayer != null) {
                introPlayer.setLooping(loop);
                introPlayer.start();
            }
        }
    }

    public void stopIntroMusic() {
        if (introPlayer != null) {
            if (introPlayer.isPlaying()) {
                introPlayer.stop();
            }
            introPlayer.release();
            introPlayer = null;
        }
    }

    public void release() {
        releaseSoundPool();
        stopIntroMusic();
    }
}