package sensors.mmbuw.example.com.sensors;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.List;

/**
 * Created by payam on 5/24/15.
 */
public class ActivityRecognitionService extends Service {
    private MagnitudeData mMagnitudeData;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private int mLastActivity = 0;

    @Override
    public void onCreate() {
        mMagnitudeData = new MagnitudeData(6);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null){
            List<Sensor> gravSensors = mSensorManager.getSensorList(Sensor.TYPE_GRAVITY);
            for(int i=0; i<gravSensors.size(); i++) {
                if ((gravSensors.get(i).getVendor().contains("Google Inc.")) &&
                        (gravSensors.get(i).getVersion() == 3)){
                    // Use the version 3 gravity sensor.
                    mSensor = gravSensors.get(i);
                }
            }
        }
        else{
            // Use the accelerometer.
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            }
            else{
                // Sorry, there are no accelerometers on your device.
                // You can't play this game.
            }
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 200000);
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(mSensorListener);
    }

    private void ShowCurrentActivity(){
        /*
        notify current activity
        link -> http://stackoverflow.com/questions/17746403/background-service-to-control-proximity-sensor
        * */
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent bIntent = new Intent(ActivityRecognitionService.this, SensorDataActivity.class);
        PendingIntent pbIntent = PendingIntent.getActivity(ActivityRecognitionService.this, 0, bIntent, 0);
        NotificationCompat.Builder bBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Current Activity")
                        .setContentText(mMagnitudeData.getCurrentActivityText())
                        .setAutoCancel(true)
                        .setOngoing(true)
                        .setContentIntent(pbIntent);
        Notification barNotif = bBuilder.build();
        this.startForeground(1, barNotif);
    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        double ax,ay,az;   // these are the acceleration in x,y and z axis
        @Override
        public void onSensorChanged(SensorEvent event) {

            //if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            {
                ax = event.values[0];
                ay = event.values[1];
                az = event.values[2];
                double omegaMagnitude = Math.sqrt(ax * ax + ay * ay + az * az);
                //String str = ax + " " + ay + " " + az + " " + omegaMagnitude;
                //Toast.makeText(this, str, Toast.LENGTH_LONG);
                mMagnitudeData.AddToList(omegaMagnitude);
                if(mLastActivity != mMagnitudeData.ActivityRecognition()) {
                    mLastActivity = mMagnitudeData.ActivityRecognition();
                    ShowCurrentActivity();
                }

            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do nothing here.
        }

    };

}
