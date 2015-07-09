package com.example.katerina.mapsex;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.*;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MapFragment
        extends
        com.google.android.gms.maps.MapFragment
        implements

        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapLongClickListener,
        OnClickListener {


    private LatLng temp;
    //интервал обновления положения всплывающего окна.
    //для плавности необходимо 60 fps, то есть 1000 ms / 60 = 16 ms между обновлениями.
    private static final int POPUP_POSITION_REFRESH_INTERVAL = 16;
    //длительность анимации перемещения карты
    private static final int ANIMATION_DURATION = 500;

    private Map<Marker, CheckIn> spots, spotsPrevious;
    private   GoogleMap map;
    //точка на карте, соответственно перемещению которой перемещается всплывающее окно
    private LatLng trackedPosition;

    //Handler, запускающий обновление окна с заданным интервалом
    private Handler handler;

    //Runnable, который обновляет положение окна
    private Runnable positionUpdaterRunnable;

    //смещения всплывающего окна, позволяющее
    //скорректировать его положение относительно маркера
    private int popupXOffset;
    private int popupYOffset;
    //высота маркера
    private int markerHeight;
    private AbsoluteLayout.LayoutParams overlayLayoutParams;

    //слушатель, который будет обновлять смещения
    private ViewTreeObserver.OnGlobalLayoutListener infoWindowLayoutListener;

    //контейнер всплывающего окна
    private View infoWindowContainer;
    private TextView textView;
    private TextView button;

    public MapFragment(){
        this.setRetainInstance(true);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spots = new HashMap<>();
        markerHeight = getResources().getDrawable(R.drawable.pin).getIntrinsicHeight();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_map_fragment, null);
        Button button1 = (Button) rootView.findViewById(R.id.button7);
        button1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(new Intent(getActivity(), GamesActivity.class));
                startActivity(intent);
            }
        });
        FrameLayout containerMap = (FrameLayout) rootView.findViewById(R.id.container_map);
        View mapView = super.onCreateView(inflater, container, savedInstanceState);
        containerMap.addView(mapView, new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        MapFragment mapFragment =
                (MapFragment) getFragmentManager().findFragmentById(R.id.container_map_fragment);

        if (savedInstanceState == null) {
            map = getMap();
        } else {
            map = mapFragment.getMap();
        }


        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(48.35, 31.16), 5.5f));
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
        map.setOnMapLongClickListener(this);



        //map.clear();
        //spots.clear();
        /*BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.pin);
        for (CheckIn spot : SPOTS_ARRAY) {
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(spot.getPosition())
                    .title("Title")
                    .snippet("Subtitle")
                    .icon(icon));
            spots.put(marker, spot);
        }*/

        infoWindowContainer = rootView.findViewById(R.id.container_popup);
        //подписываемся на изменения размеров всплывающего окна
        infoWindowLayoutListener = new InfoWindowLayoutListener();
        infoWindowContainer.getViewTreeObserver().addOnGlobalLayoutListener(infoWindowLayoutListener);
        overlayLayoutParams = (AbsoluteLayout.LayoutParams) infoWindowContainer.getLayoutParams();

        textView = (TextView) infoWindowContainer.findViewById(R.id.textview_title);
      //  button = (TextView) infoWindowContainer.findViewById(R.id.button_view_article);
       // button.setOnClickListener(this);


        return rootView;
    }
    public Object onRetainNonConfigurationInstance(){
        return spots;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //очистка
        handler = new Handler(Looper.getMainLooper());
        positionUpdaterRunnable = new PositionUpdaterRunnable();
        handler.post(positionUpdaterRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        infoWindowContainer.getViewTreeObserver().removeGlobalOnLayoutListener(infoWindowLayoutListener);
        handler.removeCallbacks(positionUpdaterRunnable);
        handler = null;
    }

    @Override
    public void onClick(View v) {
      //  String name = (String) v.getTag();
       // startActivity( new Intent(getActivity(),CheckInInfoActivity.class));
       // startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.google.com/search?q=" + name)));
    }

    @Override
    public void onMapClick(LatLng latLng) {
        infoWindowContainer.setVisibility(INVISIBLE);




    }




    @Override
    public boolean onMarkerClick(Marker marker) {
        GoogleMap map = getMap();
        Projection projection = map.getProjection();
        trackedPosition = marker.getPosition();
        Point trackedPoint = projection.toScreenLocation(trackedPosition);
        trackedPoint.y -= popupYOffset / 2;
        LatLng newCameraLocation = projection.fromScreenLocation(trackedPoint);
        map.animateCamera(CameraUpdateFactory.newLatLng(newCameraLocation), ANIMATION_DURATION, null);

        CheckIn spot = spots.get(marker);
        textView.setText(spot.getName());
      //  button.setTag(spot.getName());

        infoWindowContainer.setVisibility(VISIBLE);

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == -1) {
                String result = data.getStringExtra("comment")+"\n"+data.getStringExtra("garbage");
                final MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(temp);
                markerOptions.title(temp.latitude + " : " + temp.longitude);
                map.animateCamera(CameraUpdateFactory.newLatLng(temp));
                Projection projection = map.getProjection();
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.pin);
                Marker m =   map.addMarker(markerOptions.icon(icon));
                Date cDate = new Date();

                String fDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(cDate);
                spots.put(m,new CheckIn(result,temp));
            }
            if (resultCode == 1) {
                //Write your code if there's no result
            }

        }

    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        temp=latLng;
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(latLng.latitude + " : " + latLng.longitude);
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        Projection projection = map.getProjection();
        new AlertDialog.Builder(this.getActivity()) .setTitle("Check-in?")
                .setMessage("Do you want to check-in your location?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle a positive answer
                        startActivityForResult(new Intent(getActivity(), CheckInInfoActivity.class), 1);

/*
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.pin);
                        Marker m =   map.addMarker(markerOptions.icon(icon));
                        Date cDate = new Date();

                        String fDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(cDate);
                        spots.put(m, new CheckIn("check-in", latLng));*/
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle a negative answer
                    }
                })
                .setIcon(R.drawable.check_in)
                .show();
    }



    private class InfoWindowLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            //размеры окна изменились, обновляем смещения
            popupXOffset = infoWindowContainer.getWidth() / 2;
            popupYOffset = infoWindowContainer.getHeight();
        }
    }

    private class PositionUpdaterRunnable implements Runnable {
        private int lastXPosition = Integer.MIN_VALUE;
        private int lastYPosition = Integer.MIN_VALUE;

        @Override
        public void run() {
            //помещаем в очередь следующий цикл обновления
            handler.postDelayed(this, POPUP_POSITION_REFRESH_INTERVAL);

            //если всплывающее окно скрыто, ничего не делаем
            if (trackedPosition != null && infoWindowContainer.getVisibility() == VISIBLE) {
                Point targetPosition = getMap().getProjection().toScreenLocation(trackedPosition);

                //если положение окна не изменилось, ничего не делаем
                if (lastXPosition != targetPosition.x || lastYPosition != targetPosition.y) {
                    //обновляем положение
                    overlayLayoutParams.x = targetPosition.x - popupXOffset;
                    overlayLayoutParams.y = targetPosition.y - popupYOffset - markerHeight -30;
                    infoWindowContainer.setLayoutParams(overlayLayoutParams);

                    //запоминаем текущие координаты
                    lastXPosition = targetPosition.x;
                    lastYPosition = targetPosition.y;
                }
            }
        }
    }
}