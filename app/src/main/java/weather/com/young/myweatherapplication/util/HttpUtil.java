package weather.com.young.myweatherapplication.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by young on 16/1/1.
 */
public class HttpUtil {

    public static  void setHttpRequest(final String address,final HttpCallbackListener listener){

        new Thread(new Runnable() {
            @Override
            public void run() {

                HttpURLConnection conn = null;

                try {
                    URL url = new URL(address);

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(8000);
                    conn.setConnectTimeout(10000);
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line;
                    char[] buffer = new char[1024];
                    int len = br.read(buffer);
                    sb.append(String.valueOf(buffer,0,len));

                    if(listener!=null){
                        System.out.print(sb.toString());
                        listener.onFinish(sb.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(listener!=null){
                        listener.onError(e);
                    }
                }finally {
                    if(conn!=null){
                        conn.disconnect();
                    }
                }

            }
        }).start();

    }

}
