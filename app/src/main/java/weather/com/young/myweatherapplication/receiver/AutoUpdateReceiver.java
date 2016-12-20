package weather.com.young.myweatherapplication.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import weather.com.young.myweatherapplication.service.AutoUpdateService;

/**
 * Created by young on 16/1/3.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent it = new Intent(context, AutoUpdateService.class);
        context.startService(it);
    }
}
