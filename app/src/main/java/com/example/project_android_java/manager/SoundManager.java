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
    private int sound5050, soundAudience, soundPhone, soundIntro, soundTimer30s;
    private int soundQ1, soundQ2, soundQ3, soundQ4, soundQ5, soundQ6, soundQ7, soundQ8, soundQ9, soundQ10;
    private int soundQ11, soundQ12, soundQ13, soundQ14, soundQ15;
    private int soundCorrectAnswer, soundWrongAnswer, soundWin15, soundLose15;
    private int soundSage;
    private int soundQ15Background, soundCorrect15, soundCorrect5, soundWrong15;
    private int soundCorrectA, soundCorrectB, soundCorrectD;
    private boolean isSoundEnabled = true;

    private MediaPlayer introPlayer;
    private MediaPlayer backgroundPlayer;
    private MediaPlayer readyPlayer;
    private MediaPlayer questionPlayer;

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
        soundPool = new SoundPool(10, android.media.AudioManager.STREAM_MUSIC, 0);
        
        soundCorrect = soundPool.load(context, R.raw.cau_tra_loi_dung, 1);
        soundWrong = soundPool.load(context, R.raw.cau_1_5_sai, 1);
        soundClick = soundPool.load(context, R.raw.game_start, 1);
        soundWin = soundPool.load(context, R.raw.cau_15_dung, 1);
        soundLose = soundPool.load(context, R.raw.cau_15_sai, 1);
        
        sound5050 = soundPool.load(context, R.raw.nhac_5050, 1);
        soundAudience = soundPool.load(context, R.raw.khan_gia_tro_giup, 1);
        soundPhone = soundPool.load(context, R.raw.goi_dien_thoai_cho_nguoi_than, 1);
        soundTimer30s = soundPool.load(context, R.raw.thoi_gian_30s, 1);
        soundSage = soundPool.load(context, R.raw.lua_chon_tro_giup, 1);
        
        soundQ1 = soundPool.load(context, R.raw.cau_hoi_dau_tien, 1);
        soundQ2 = soundPool.load(context, R.raw.cau_hoi_so_2, 1);
        soundQ3 = soundPool.load(context, R.raw.cau_hoi_so_3, 1);
        soundQ4 = soundPool.load(context, R.raw.cau_hoi_so_4, 1);
        soundQ5 = soundPool.load(context, R.raw.cau_hoi_so_5, 1);
        soundQ6 = soundPool.load(context, R.raw.cau_hoi_so_6, 1);
        soundQ7 = soundPool.load(context, R.raw.cau_hoi_so_7, 1);
        soundQ8 = soundPool.load(context, R.raw.cau_hoi_so_8, 1);
        soundQ9 = soundPool.load(context, R.raw.cau_hoi_so_9, 1);
        soundQ10 = soundPool.load(context, R.raw.cau_hoi_so_10, 1);
        soundQ11 = soundPool.load(context, R.raw.cau_hoi_so_11, 1);
        soundQ12 = soundPool.load(context, R.raw.cau_hoi_so_12, 1);
        soundQ13 = soundPool.load(context, R.raw.cau_hoi_so_13, 1);
        soundQ14 = soundPool.load(context, R.raw.cau_hoi_so_14, 1);
        soundQ15 = soundPool.load(context, R.raw.cau_hoi_so_15, 1);
        
        soundIntro = soundPool.load(context, R.raw.gioi_thieu_luat_choi, 1);
        
        soundQ15Background = soundPool.load(context, R.raw.cau_1_5, 1);
        soundCorrect15 = soundPool.load(context, R.raw.cau_1_4_dung, 1);
        soundWrong15 = soundPool.load(context, R.raw.cau_1_5_sai, 1);
        soundCorrect5 = soundPool.load(context, R.raw.cau_5_dung, 1);
        
        soundCorrectA = soundPool.load(context, R.raw.dap_an_a_dung, 1);
        soundCorrectB = soundPool.load(context, R.raw.dap_b_dung, 1);
        soundCorrectD = soundPool.load(context, R.raw.dap_an_d_dung, 1);
        
        introPlayer = MediaPlayer.create(context, R.raw.gioi_thieu_luat_choi);
        readyPlayer = MediaPlayer.create(context, R.raw.nguoi_choi_da_san_sang);
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

    public void play5050() {
        if (isSoundEnabled && sound5050 != 0) {
            soundPool.play(sound5050, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    public void play5050WithCallback(Runnable onComplete) {
        if (!isSoundEnabled) {
            if (onComplete != null) onComplete.run();
            return;
        }
        
        MediaPlayer player = MediaPlayer.create(context, R.raw.bo_di_2_dap_an);
        if (player != null) {
            player.setOnCompletionListener(mp -> {
                if (onComplete != null) {
                    onComplete.run();
                }
            });
            player.start();
        } else if (onComplete != null) {
            onComplete.run();
        }
    }

    public void playAudience() {
        if (isSoundEnabled && soundAudience != 0) {
            soundPool.play(soundAudience, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    public void playAudienceWithCallback(Runnable onComplete) {
        if (!isSoundEnabled) {
            if (onComplete != null) onComplete.run();
            return;
        }
        
        MediaPlayer player = MediaPlayer.create(context, R.raw.hoi_khan_gia);
        if (player != null) {
            player.setOnCompletionListener(mp -> {
                if (onComplete != null) {
                    onComplete.run();
                }
            });
            player.start();
        } else if (onComplete != null) {
            onComplete.run();
        }
    }

    public void playPhone() {
        if (isSoundEnabled && soundPhone != 0) {
            soundPool.play(soundPhone, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    public void playPhoneWithCallback(Runnable onComplete) {
        if (!isSoundEnabled) {
            if (onComplete != null) onComplete.run();
            return;
        }
        
        MediaPlayer player = MediaPlayer.create(context, R.raw.goi_cho_ai);
        if (player != null) {
            player.setOnCompletionListener(mp -> {
                if (onComplete != null) {
                    onComplete.run();
                }
            });
            player.start();
        } else if (onComplete != null) {
            onComplete.run();
        }
    }

    public void playSage() {
        if (isSoundEnabled && soundSage != 0) {
            soundPool.play(soundSage, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    public void playTimer30s() {
        if (isSoundEnabled && soundTimer30s != 0) {
            soundPool.play(soundTimer30s, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    public void playIntro() {
        stopIntro();
        if (isSoundEnabled && introPlayer != null) {
            introPlayer.start();
        }
    }

    public void stopIntro() {
        if (introPlayer != null) {
            if (introPlayer.isPlaying()) {
                introPlayer.stop();
            }
            introPlayer.release();
            introPlayer = MediaPlayer.create(context, R.raw.gioi_thieu_luat_choi);
        }
    }

    public void playReady(Runnable onComplete) {
        stopIntro();
        if (isSoundEnabled && readyPlayer != null) {
            readyPlayer.setOnCompletionListener(mp -> {
                if (onComplete != null) {
                    onComplete.run();
                }
            });
            readyPlayer.start();
        } else if (onComplete != null) {
            onComplete.run();
        }
    }

    public void playQuestion(int questionNumber) {
        playQuestion(questionNumber, null);
    }

    public void playQuestion(int questionNumber, Runnable onComplete) {
        stopQuestion();
        
        if (!isSoundEnabled) {
            if (onComplete != null) onComplete.run();
            return;
        }
        
        int resId = 0;
        switch (questionNumber) {
            case 1: resId = R.raw.cau_hoi_dau_tien; break;
            case 2: resId = R.raw.cau_hoi_so_2; break;
            case 3: resId = R.raw.cau_hoi_so_3; break;
            case 4: resId = R.raw.cau_hoi_so_4; break;
            case 5: resId = R.raw.cau_hoi_so_5; break;
            case 6: resId = R.raw.cau_hoi_so_6; break;
            case 7: resId = R.raw.cau_hoi_so_7; break;
            case 8: resId = R.raw.cau_hoi_so_8; break;
            case 9: resId = R.raw.cau_hoi_so_9; break;
            case 10: resId = R.raw.cau_hoi_so_10; break;
            case 11: resId = R.raw.cau_hoi_so_11; break;
            case 12: resId = R.raw.cau_hoi_so_12; break;
            case 13: resId = R.raw.cau_hoi_so_13; break;
            case 14: resId = R.raw.cau_hoi_so_14; break;
            case 15: resId = R.raw.cau_hoi_so_15; break;
        }
        
        if (resId != 0) {
            questionPlayer = MediaPlayer.create(context, resId);
            if (questionPlayer != null) {
                questionPlayer.setOnCompletionListener(mp -> {
                    if (onComplete != null) {
                        onComplete.run();
                    }
                });
                questionPlayer.start();
            }
        } else if (onComplete != null) {
            onComplete.run();
        }
    }

    private void stopQuestion() {
        if (questionPlayer != null) {
            if (questionPlayer.isPlaying()) {
                questionPlayer.stop();
            }
            questionPlayer.release();
            questionPlayer = null;
        }
    }

    public void playBackground15() {
        if (!isSoundEnabled) return;
        
        if (backgroundPlayer != null) {
            if (backgroundPlayer.isPlaying()) {
                backgroundPlayer.stop();
            }
            backgroundPlayer.release();
        }
        
        backgroundPlayer = MediaPlayer.create(context, R.raw.cau_1_5);
        if (backgroundPlayer != null) {
            backgroundPlayer.setLooping(true);
            backgroundPlayer.start();
        }
    }

    public void stopBackground() {
        if (backgroundPlayer != null) {
            try {
                if (backgroundPlayer.isPlaying()) {
                    backgroundPlayer.stop();
                }
            } catch (IllegalStateException e) {
                // Player already stopped
            }
            backgroundPlayer.release();
            backgroundPlayer = null;
        }
    }

    public void playCorrect15() {
        stopBackground();
        if (isSoundEnabled && soundCorrect15 != 0) {
            soundPool.play(soundCorrect15, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    public void playCorrect5() {
        stopBackground();
        if (isSoundEnabled && soundCorrect5 != 0) {
            soundPool.play(soundCorrect5, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    public void playCorrect5WithCallback(Runnable onComplete) {
        stopBackground();
        if (!isSoundEnabled) {
            if (onComplete != null) onComplete.run();
            return;
        }
        
        MediaPlayer player = MediaPlayer.create(context, R.raw.cau_5_dung);
        if (player != null) {
            player.setOnCompletionListener(mp -> {
                if (onComplete != null) {
                    onComplete.run();
                }
            });
            player.start();
        } else if (onComplete != null) {
            onComplete.run();
        }
    }

    public void playCorrectAnswerByIndex(int answerIndex) {
        stopBackground();
        if (!isSoundEnabled) return;
        
        int resId = 0;
        if (answerIndex == 0) {
            resId = R.raw.dap_an_a_dung;
        } else if (answerIndex == 1) {
            resId = R.raw.dap_b_dung;
        } else if (answerIndex == 3) {
            resId = R.raw.dap_an_d_dung;
        } else {
            resId = R.raw.cau_1_4_dung;
        }
        
        if (resId != 0) {
            MediaPlayer correctPlayer = MediaPlayer.create(context, resId);
            if (correctPlayer != null) {
                correctPlayer.start();
            }
        }
    }

    public void playCorrectAnswerByIndex(int answerIndex, Runnable onComplete) {
        stopBackground();
        if (!isSoundEnabled) {
            if (onComplete != null) onComplete.run();
            return;
        }
        
        int resId = 0;
        if (answerIndex == 0) {
            resId = R.raw.dap_an_a_dung;
        } else if (answerIndex == 1) {
            resId = R.raw.dap_b_dung;
        } else if (answerIndex == 2) {
            resId = R.raw.chac_chan_la_cau_tra_loi_dung_roi;
        } else if (answerIndex == 3) {
            resId = R.raw.dap_an_d_dung;
        } else {
            resId = R.raw.cau_1_4_dung;
        }
        
        if (resId != 0) {
            MediaPlayer correctPlayer = MediaPlayer.create(context, resId);
            if (correctPlayer != null) {
                correctPlayer.setOnCompletionListener(mp -> {
                    if (onComplete != null) {
                        onComplete.run();
                    }
                });
                correctPlayer.start();
            }
        } else if (onComplete != null) {
            onComplete.run();
        }
    }

    public void playWrong15() {
        stopBackground();
        if (isSoundEnabled && soundWrong15 != 0) {
            soundPool.play(soundWrong15, 1.0f, 1.0f, 1, 0, 1.0f);
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