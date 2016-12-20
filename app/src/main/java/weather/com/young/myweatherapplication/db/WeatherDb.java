package weather.com.young.myweatherapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import weather.com.young.myweatherapplication.model.City;
import weather.com.young.myweatherapplication.model.Country;
import weather.com.young.myweatherapplication.model.Province;

/**
 * Created by young on 15/12/30.
 */
public class WeatherDb {

    /**
     * 数据库名 */
    public static final String DB_NAME = "cool_weather";
    /**
     * 数据库版本 */
    public static final int VERSION = 1;
    private static WeatherDb weatherDB;
    private SQLiteDatabase db;

    public WeatherDb(Context context){

        WeatherSqlHelper ws = new WeatherSqlHelper(context,DB_NAME,null,VERSION);
        db = ws.getWritableDatabase();

    }

    public synchronized static WeatherDb getInstance(Context context){

        if(weatherDB==null){
            weatherDB = new WeatherDb(context);

        }

        return weatherDB;

    }


    public void saveProvince(Province province) {

        if (province != null) {
            ContentValues cv = new ContentValues();
            cv.put("province_name", province.getProvinceName());
            cv.put("province_code", province.getProvinceCode());
            db.insert("Province", null, cv);

        }

    }
        /**
         * 将City实例存储到数据库。 */
        public void saveCity(City city) {
            if (city != null) {
                ContentValues values = new ContentValues();
                values.put("city_name", city.getCityName());
                values.put("city_code", city.getCityCode());
                values.put("province_id", city.getProvinceId());
                db.insert("City", null, values);
            }

    }


    public List<Province> findAllProvince(){

        List<Province> list = new ArrayList<Province>();

        Cursor cursor = db.query("Province", null, null, null, null, null, null);

        if(cursor.moveToFirst()) {
            do {
                Province po = new Province();
                po.setId(cursor.getInt(cursor.getColumnIndex("id")));
                po.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                po.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));

                list.add(po);
            } while (cursor.moveToNext());

        }
        return list;


    }



    /**
     * 从数据库读取某省下所有的城市信息。 */
    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City", null, "province_id = ?",
                new String[] { String.valueOf(provinceId) }, null, null, null); if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor
                        .getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor
                        .getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                list.add(city);
            } while (cursor.moveToNext());
        }
        return list;
    }
    /**
     * 将County实例存储到数据库。 */
    public void saveCounty(Country county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyName());
            values.put("county_code", county.getCountyCode());
            values.put("city_id", county.getCityId());
            db.insert("County", null, values);
        }
    }
    /**
     * 从数据库读取某城市下所有的县信息。 */
    public List<Country> loadCounties(int cityId) {
        List<Country> list = new ArrayList<Country>();
        Cursor cursor = db.query("County", null, "city_id = ?",
                new String[] { String.valueOf(cityId) }, null, null, null); if (cursor.moveToFirst()) {
            do {
                Country county = new Country();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor
                        .getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor
                        .getColumnIndex("county_code")));
                county.setCityId(cityId);
                list.add(county);
            } while (cursor.moveToNext());
        }
        return list;
    }

}
