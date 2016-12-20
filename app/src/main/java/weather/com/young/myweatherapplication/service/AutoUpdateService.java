package weather.com.young.myweatherapplication.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import weather.com.young.myweatherapplication.receiver.AutoUpdateReceiver;
import weather.com.young.myweatherapplication.util.HttpCallbackListener;
import weather.com.young.myweatherapplication.util.HttpUtil;
import weather.com.young.myweatherapplication.util.Ultity;

/**
 * Created by young on 16/1/3.
 */
public class AutoUpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE); int anHour = 8 * 60 * 60 * 1000; // 这是8小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour; Intent i = new Intent(this, AutoUpdateReceiver.class); PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0); manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi); return super.onStartCommand(intent, flags, startId);
    }
    /**
     * 更新天气信息。 */
    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(this);
        String weatherCode = prefs.getString("weather_code", "");
        String address = "http://www.weather.com.cn/data/cityinfo/" +
                weatherCode + ".html";
        HttpUtil.setHttpRequest(address, new HttpCallbackListener() {

            @Override
            public void onFinish(String response) {
                Ultity.saveWeatherInfo(AutoUpdateService.this,response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }


}
