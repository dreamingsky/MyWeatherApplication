package weather.com.young.myweatherapplication.util;

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
