package sensors.mmbuw.example.com.sensors;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class SensorDataActivity extends ActionBarActivity{

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private SeekBar mWindowSize, mSampleRate;
    double ax,ay,az;   // these are the acceleration in x,y and z axis
    private int mSampleRateInt = 200000;
    private int mWindowSizeInt = 4;
    private MagnitudeData mMagnitudeData;
    private AccelerometerView mAccelerometerView;
    private FFTTransformView mFFTTransformView;
    private int mLastActivity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data);


        mMagnitudeData = new MagnitudeData(mWindowSizeInt);

        mAccelerometerView = (AccelerometerView) findViewById(R.id.accelerometerView);
        mAccelerometerView.setMagnitudeData(mMagnitudeData);

        mFFTTransformView = (FFTTransformView) findViewById(R.id.fftTransformView);
        mFFTTransformView.setMagnitudeData(mMagnitudeData);

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
        mAccelerometerView.setXyzMaximumRange(Math.abs(mSensor.getMaximumRange()));



        /*
        * use seek bar to control sample rate
        * link -> http://javatechig.com/android/android-seekbar-example
        * */

        mSampleRate = (SeekBar) findViewById(R.id.sampleRate);

        mSampleRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                mSampleRateInt = progress * 10000;
                reRegisterSensor();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mWindowSize = (SeekBar) findViewById(R.id.fftWindowSize);
        mWindowSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                mWindowSizeInt = progress + 1;
                SetWindowSize();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        startService(new Intent(getBaseContext(), ActivityRecognitionService.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sensor_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void ShowCurrentActivity(){
        Toast.makeText(this, mMagnitudeData.getCurrentActivityText(), Toast.LENGTH_SHORT).show();
    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            //if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            {
                ax = event.values[0];
                ay = event.values[1];
                az = event.values[2];
                double omegaMagnitude = Math.sqrt(ax * ax + ay * ay + az * az);

                mMagnitudeData.AddToList(omegaMagnitude);
                mMagnitudeData.setAccelerometerData(ax, ay, az, omegaMagnitude);
                mAccelerometerView.setMagnitudeData(mMagnitudeData);
                mFFTTransformView.setMagnitudeData(mMagnitudeData);
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

    @Override
    protected void onResume() {
        super.onResume();
        registerSensor();
    }

    @Override
    protected void onPause() {
        unregisterSensor();
        super.onPause();
    }


    private void registerSensor(){
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), mSampleRateInt);
    }

    private void unregisterSensor(){
        mSensorManager.unregisterListener(mSensorListener);
    }

    private void reRegisterSensor(){
        unregisterSensor();
        registerSensor();
    }

    private void SetWindowSize(){
        mMagnitudeData.ResetMagnitudeData(mWindowSizeInt);
    }

}
