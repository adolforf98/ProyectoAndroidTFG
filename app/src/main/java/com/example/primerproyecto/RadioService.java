package com.example.primerproyecto;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

public class RadioService extends Service {

    private static final String TAG = "RadioService";
    private static final String CHANNEL_ID = "RadioServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    private ExoPlayer player;
    private PlayerNotificationManager playerNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Creating notification channel");
        createNotificationChannel();

        try {
            Log.d(TAG, "Building ExoPlayer");
            player = new ExoPlayer.Builder(getApplicationContext()).build();
            Log.d(TAG, "ExoPlayer initialized");

            playerNotificationManager = new PlayerNotificationManager.Builder(this, NOTIFICATION_ID, CHANNEL_ID)
                    .setMediaDescriptionAdapter(new PlayerNotificationManager.MediaDescriptionAdapter() {
                        @Override
                        public CharSequence getCurrentContentTitle(Player player) {
                            return "Radio en vivo";
                        }

                        @Nullable
                        @Override
                        public PendingIntent createCurrentContentIntent(Player player) {
                            Intent intent = new Intent(RadioService.this, MainActivity.class);
                            int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_IMMUTABLE : 0;
                            return PendingIntent.getActivity(RadioService.this, 0, intent, flags);
                        }

                        @Nullable
                        @Override
                        public CharSequence getCurrentContentText(Player player) {
                            return "Escuchando la radio";
                        }

                        @Nullable
                        @Override
                        public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                            return BitmapFactory.decodeResource(getResources(), R.drawable.ic_radio);
                        }
                    })
                    .setNotificationListener(new PlayerNotificationManager.NotificationListener() {
                        @Override
                        public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                            stopSelf();
                        }

                        @Override
                        public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
                            startForeground(notificationId, notification);
                        }
                    })
                    .build();
            playerNotificationManager.setPlayer(player);

            Log.d(TAG, "PlayerNotificationManager configured");

        } catch (Exception e) {
            Log.e(TAG, "Error initializing ExoPlayer", e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if ("PLAY".equals(action)) {
            String url = intent.getStringExtra("URL");
            Log.d(TAG, "Received PLAY action with URL: " + url);
            playRadio(url);
        } else if ("STOP".equals(action)) {
            Log.d(TAG, "Received STOP action");
            stopRadio();
        }
        return START_NOT_STICKY;
    }

    private void playRadio(String url) {
        if (player == null) {
            Log.e(TAG, "Player is not initialized");
            return;
        }
        try {
            Log.d(TAG, "Preparing media item from URL: " + url);
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
            Log.d(TAG, "Playback started");
        } catch (Exception e) {
            Log.e(TAG, "Error playing media", e);
        }
    }

    private void stopRadio() {
        if (player != null) {
            Log.d(TAG, "Stopping playback");
            player.stop();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            Log.d(TAG, "Releasing player");
            player.release();
            player = null;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Radio Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}
