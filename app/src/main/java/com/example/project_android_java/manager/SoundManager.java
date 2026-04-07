package com.example.project_android_java.manager;

import android.content.Context;
import android.media.MediaPlayer;
import com.example.project_android_java.R;

public class SoundManager {
    private static SoundManager instance;
    private MediaPlayer mediaPlayer;
    private Context context;
    private boolean isSoundEnabled = true;

    private SoundManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static SoundManager getInstance(Context context) {
        if (instance == null) instance = new SoundManager(context);
        return instance;
    }

    public void playSound(int resId, boolean loop) {
        if (!isSoundEnabled) return;
        stopSound();
        mediaPlayer = MediaPlayer.create(context, resId);
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(loop);
            mediaPlayer.start();
        }
    }

    public void stopSound() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void setSoundEnabled(boolean enabled) {
        this.isSoundEnabled = enabled;
        if (!enabled) stopSound();
    }
}