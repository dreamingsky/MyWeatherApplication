package weather.com.young.myweatherapplication.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import weather.com.young.myweatherapplication.db.WeatherDb;
import weather.com.young.myweatherapplication.model.City;
import weather.com.young.myweatherapplication.model.Country;
import weather.com.young.myweatherapplication.model.Province;

/**
 * Created by young on 16/1/1.
 */
public class Ultity {


    public synchronized static boolean handleProvince(WeatherDb db,String response){

        if(!TextUtils.isEmpty(response)){
            String[] s = response.split(",");
            if(s!=null&&s.length>0){
                for(int i=0;i<s.length;i++){

                    String[] p = s[i].split("\\|");
                    Province pro = new Province();
                    pro.setProvinceCode(p[0]);
                    pro.setProvinceName(p[1]);
                    db.saveProvince(pro);

                }

                return true;
            }
        }

        return false;

    }


    public synchronized static boolean handleCity(WeatherDb db,String response,int provinceId){

        if(!TextUtils.isEmpty(response)){
            String[] s = response.split(",");
            if(s!=null&&s.length>0){
                for(int i=0;i<s.length;i++){

                    String[] p = s[i].split("\\|");
                    City pro = new City();
                    pro.setCityCode(p[0]);
                    pro.setCityName(p[1]);
                    pro.setProvinceId(provinceId);
                    db.saveCity(pro);

                }

                return true;
            }
        }

        return false;

    }


    public synchronized static boolean handleCountry(WeatherDb db,String response,int cityId){

        if(!TextUtils.isEmpty(response)){
            String[] s = response.split(",");
            if(s!=null&&s.length>0){
                for(int i=0;i<s.length;i++){

                    String[] p = s[i].split("\\|");
                    Country pro = new Country();
                    pro.setCountyCode(p[0]);
                    pro.setCountyName(p[1]);
                    pro.setCityId(cityId);
                    db.saveCounty(pro);

                }

                return true;
            }
        }

        return false;

    }


    public static void saveWeatherInfo(Context context,String response){

        try {
            JSONObject jo = new JSONObject(response);
            JSONObject weather = jo.getJSONObject("weatherinfo");
            SimpleDateFormat sd = new SimpleDateFormat("yyyy年MM月dd日");
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putBoolean("city_selected",true);
            editor.putString("city_name", weather.getString("city"));
            editor.putString("weather_code", weather.getString("cityid"));
            editor.putString("temp1", weather.getString("temp1"));
            editor.putString("temp2", weather.getString("temp2"));
            editor.putString("weather_desp", weather.getString("weather"));
            editor.putString("publish_time", weather.getString("ptime"));
            editor.putString("current_date", sd.format(new Date()));
            editor.commit();

        }catch (Exception e){

        }


    }
}
