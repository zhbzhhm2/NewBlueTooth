package code.source.es.newbluetooth.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

import code.source.es.newbluetooth.Service.ScanService;

public class LineTestActivity extends Activity {

    DisplayMetrics dm = new DisplayMetrics();
    float height,width;
    float []distance=new float[3];
    float []RSSIArray=new float[3];
    Set RSSIFlag =new HashSet();
    View view;
    int distanceOfABC=4;
    Messenger messenger;

    ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger=new Messenger(service);
            Message msg=new Message();
            msg.what=ScanService.START_BLUETOOTH;
            try {
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(ScanService.GET_BLUETOOTH_RSSI.equals(intent.getAction())){
                float []d= LineTestActivity.this.distance;
                String name=intent.getExtras().getString("name");
                String MAC=intent.getExtras().getString("MAC");
                int RSSI=intent.getExtras().getInt("RSSI");
                double distance=intent.getExtras().getDouble("distance");
                for(int i=0;i<d.length;i++){
                    String s="BINGO"+(i+1);
                    if(s.equals(name)) {
                        RSSIArray[i]=RSSI;
                        d[i] = (float) distance;
                        RSSIFlag.add(s);
                    }
                }
                if(RSSIFlag.size()>=2){
                    reView();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_line_test);
        initValues();
        initDate();
        view =new MyView(this);
        setContentView(view);

        IntentFilter filter=new IntentFilter(ScanService.GET_BLUETOOTH_RSSI);
        this.registerReceiver(mReceiver,filter);
        bindService(new Intent(this,ScanService.class),conn,BIND_AUTO_CREATE);

    }
    private void initValues(){
        getWindowManager().getDefaultDisplay().getMetrics(dm);
         height = dm.heightPixels;
         width = dm.widthPixels;
    }
    private void initDate(){

        distance[0]=100f;
        distance[1]=1.7f;
        distance[2]=2.7f;

    }

    void reView(){

        Message msg=new Message();
        msg.what=ScanService.START_BLUETOOTH;
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        RSSIFlag.clear();
        view.postInvalidate();
    }

    private float getHeightOfScreen(float[] f){
        float []d=new float[f.length];
        for(int i=0;i<f.length;i++)
            d[i]=f[i];
        for(int i=0;i<d.length;i++)
            d[i]*=0.25/distanceOfABC*height;
        int fir=0,sec=1;
        if(d[fir]>d[sec]){
            sec=0;
            fir=1;
        }
        for(int i=2;i<d.length;i++){
            if(d[i]<d[fir]){
                sec=fir;
                fir=i;
            }else if(d[i]<d[sec]){
                sec=i;
            }
        }
        float dis=height*(1-(fir+1)*0.25f);
        float sub= fir>sec? d[fir]:d[fir];
        return f[sec]<distanceOfABC? dis+sub:dis-sub;
    }

    private class MyView extends View{
        public MyView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(Color.WHITE);
            Paint paint=new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);
            paint.setTextSize(25);

            canvas.drawLine(width*0.25f,0,width*0.25f,height,paint);
            canvas.drawLine(width*0.65f,0,width*0.65f,height,paint);

            canvas.drawCircle(width*0.45f,height*0.25f,4,paint);
            canvas.drawText("C RSSI="+RSSIArray[2],width*0.47f,height*0.25f,paint);

            canvas.drawCircle(width*0.45f,height*0.5f,4,paint);
            canvas.drawText("B RSSI="+RSSIArray[1],width*0.47f,height*0.5f,paint);

            canvas.drawCircle(width*0.45f,height*0.75f,4,paint);
            canvas.drawText("A RSSI="+RSSIArray[0],width*0.47f,height*0.75f,paint);

            paint.setColor(Color.RED);
            canvas.drawCircle(width*0.45f,getHeightOfScreen(distance),4,paint);
            canvas.drawText("Man",width*0.47f,getHeightOfScreen(distance),paint);
        }
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return  true;
        }
    }
    class TestThread extends Thread{
        @Override
        public void run() {
            while (true){
                distance[0]+=0.1f;
                view.postInvalidate();
                try {
                    this.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
