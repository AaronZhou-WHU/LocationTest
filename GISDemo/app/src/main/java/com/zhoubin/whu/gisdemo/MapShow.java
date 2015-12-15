package com.zhoubin.whu.gisdemo;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.supermap.android.maps.CoordinateReferenceSystem;
import com.supermap.android.maps.LayerView;
import com.supermap.android.maps.MapController;
import com.supermap.android.maps.MapView;
import com.supermap.android.maps.Overlay;
import com.supermap.android.maps.Point2D;

public class MapShow extends Activity {

    public static final String DEFAULT_URL = "http://support.supermap.com.cn:8090/iserver/services/map-china400/rest/maps/China";


    public MapView mapView;
    public TextView longitude;
    public TextView latitude;
    public Location location;
    public LocationManager locManager;
    public MapController mapController;
    Bitmap bitmap_mark;
    private double db_longitutu, db_latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_show);
        //初始界面显示地图
        mapView = (MapView) this.findViewById(R.id.mapview);
        LayerView layerView = new LayerView(this);
        CoordinateReferenceSystem crs = new CoordinateReferenceSystem();
        crs.wkid = 4326;
        layerView.setCRS(crs);
        layerView.setURL(DEFAULT_URL);
        mapView.setBuiltInZoomControls(true);
        mapView.setClickable(true);
        mapController = mapView.getController();
        mapController.setZoom(1);
        mapView.addLayer(layerView);
        bitmap_mark = BitmapFactory.decodeResource(getResources(), R.drawable.min_blue_pin);

        Button btn_location = (Button) this.findViewById(R.id.button_location);
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.getOverlays().add(new MyOwnOverlay());
            }
        });
        // 地图上定位并标记

        Button locationButton = (Button) this.findViewById(R.id.btn_location);
    locationButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mapView.getOverlays().add(new MyOwnOverlay());
        }
    });
        // 显示定位信息
        longitude = (TextView) findViewById(R.id.longitude);
        latitude = (TextView) findViewById(R.id.latitude);
        // 创建LocationManager对象
        locManager = (LocationManager) getSystemService
                (Context.LOCATION_SERVICE);
        // 从GPS获取最近的最近的定位信息
        location = locManager.getLastKnownLocation(
                LocationManager.NETWORK_PROVIDER);
        // 使用location根据EditText的显示
        updateView(location);
        // 设置每3秒获取一次Network的定位信息
        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER
                , 3000, 8, new LocationListener()  //①
        {
            @Override
            public void onLocationChanged(Location location)
            {
                // 当GPS定位信息发生改变时，更新位置
                updateView(location);
            }

            @Override
            public void onProviderDisabled(String provider)
            {
                updateView(null);
            }

            @Override
            public void onProviderEnabled(String provider)
            {
                // 当GPS LocationProvider可用时，更新位置
                updateView(locManager
                        .getLastKnownLocation(provider));
            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras)
            {
            }
        });

    }


    // 在定位点显示标记
    public class MyOwnOverlay extends Overlay
    {
        Point2D point2D = new Point2D(db_longitutu, db_latitude);
        Paint paint = new Paint();

        @Override
        public void draw(Canvas canvas, MapView mapView, boolean shadow)
        {
            // 在目标点的位置绘制
            Point point = mapView.getProjection().toPixels(point2D, null);
            canvas.drawBitmap(bitmap_mark, point.x - bitmap_mark.getWidth() / 2, point.y - bitmap_mark.getHeight() / 2, paint);
            //canvas.drawText("★这里是定位点", point.x, point.y, paint);
        }
    }

    // 更新EditText中显示的内容
    public void updateView(Location newLocation)
    {
        if (newLocation != null)
        {
            longitude.setText(String.valueOf(newLocation.getLongitude()));
            db_longitutu = newLocation.getLongitude();
            latitude.setText(String.valueOf(newLocation.getLatitude()));
            db_latitude = newLocation.getLatitude();

        }
        else
        {
            // 如果传入的Location对象为空则清空EditText
            longitude.setText("");
            latitude.setText("");
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}

