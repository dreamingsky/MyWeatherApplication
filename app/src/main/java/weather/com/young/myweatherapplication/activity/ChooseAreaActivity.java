package weather.com.young.myweatherapplication.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import weather.com.young.myweatherapplication.R;
import weather.com.young.myweatherapplication.db.WeatherDb;
import weather.com.young.myweatherapplication.model.City;
import weather.com.young.myweatherapplication.model.Country;
import weather.com.young.myweatherapplication.model.Province;
import weather.com.young.myweatherapplication.util.HttpCallbackListener;
import weather.com.young.myweatherapplication.util.HttpUtil;
import weather.com.young.myweatherapplication.util.Ultity;

/**
 * Created by young on 16/1/1.
 */
public class ChooseAreaActivity extends Activity {


    public final static int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog pd;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private WeatherDb db;
    private List<String> dataList = new ArrayList<String>();
    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<Country> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;

    private boolean isFromWeatherActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity",false);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if(sp.getBoolean("city_selected",false) && !isFromWeatherActivity){
            Intent it = new Intent(this,WeatherActivity.class);
            startActivity(it);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);

        listView =(ListView) findViewById(R.id.list_view);
        titleText =(TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        db = WeatherDb.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }else if(currentLevel ==LEVEL_COUNTY){
                    String countryCode = countyList.get(position).getCountyCode();
                    Intent itent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    itent.putExtra("country_code",countryCode);
                    startActivity(itent);
                    finish();
                }
            }
        });

        queryProvince();
    }


    public void showProgressDialog(){
        if(pd == null){
            pd = new ProgressDialog(this);
            pd.setMessage("正在加载....");
            pd.setCanceledOnTouchOutside(false);
        }
        pd.show();
    }

    public void closeProgressDialog(){
        if(pd!=null){
            pd.dismiss();
        }
    }


    public void queryFromServer(final String code,final String type){

        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code +
                    ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();

        HttpUtil.setHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if("province".equals(type)){
                    result = Ultity.handleProvince(db,response);
                }else if("city".equals(type)){
                    result = Ultity.handleCity(db,response,selectedProvince.getId());
                }else if("country".equals(type)){
                    result = Ultity.handleCountry(db,response,selectedCity.getId());
                }

                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvince();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("country".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }


    public void queryProvince(){
        provinceList = db.findAllProvince();
        if(provinceList.size()>0){
            dataList.clear();
            for(Province p : provinceList){
                dataList.add(p.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }else{
            queryFromServer(null,"province");
        }
    }


    private void queryCities() {
        cityList = db.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    private void queryCounties() {
        countyList = db.loadCounties(selectedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (Country county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), "country");
        } }

    @Override
    public void onBackPressed(){
        if(currentLevel==LEVEL_COUNTY){
            queryCities();
        }else if(currentLevel == LEVEL_CITY){
            queryProvince();
        }else{
            if(isFromWeatherActivity){
                Intent it = new Intent(this,WeatherActivity.class);
                startActivity(it);
            }
            finish();
        }
    }


}
