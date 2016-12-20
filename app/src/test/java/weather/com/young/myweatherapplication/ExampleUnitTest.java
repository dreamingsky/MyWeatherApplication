package weather.com.young.myweatherapplication;

import org.junit.Test;

import weather.com.young.myweatherapplication.util.HttpCallbackListener;
import weather.com.young.myweatherapplication.util.HttpUtil;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void testHttpUtil(){
        //String url = "http://www.weather.com.cn/data/cityinfo/101010200.html";
        String url = "http://www.weather.com.cn/data/list3/city010102.xml";

        HttpUtil.setHttpRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                System.out.print(response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}