package sensors.mmbuw.example.com.sensors;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;

/**
 * Created by payam on 5/25/15.
 */
public class AccelerometerView extends View {
    private Paint paint = new Paint();
    private MagnitudeData mMagnitudeData;
    private float xyzMaximumRange;
    public AccelerometerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        if(mMagnitudeData == null)
            return;
        double[] linewith = Arrays.copyOf(mMagnitudeData.GetAccelerometerData(), 4);

        paint.setStrokeWidth(5);

        paint.setColor(Color.RED);
        canvas.drawText(Double.toString(linewith[0]), getMeasuredWidth()/2, 10, paint);
        canvas.drawLine(getMeasuredWidth()/2, 15,getMeasuredWidth()/2 + (float)linewith[0]/ xyzMaximumRange / 2 * getMeasuredWidth(), 15, paint);

        paint.setColor(Color.GREEN);
        canvas.drawText(Double.toString(linewith[1]), getMeasuredWidth()/2, 60, paint);
        canvas.drawLine(getMeasuredWidth()/2, 65,getMeasuredWidth()/2 + (float)linewith[1]/ xyzMaximumRange / 2 * getMeasuredWidth(), 65, paint);

        paint.setColor(Color.BLUE);
        canvas.drawText(Double.toString(linewith[2]), getMeasuredWidth()/2, 110, paint);
        canvas.drawLine(getMeasuredWidth()/2, 115,getMeasuredWidth()/2 + (float)linewith[2]/ xyzMaximumRange / 2 * getMeasuredWidth(), 115, paint);

        paint.setColor(Color.WHITE);
        canvas.drawText(Double.toString(linewith[3]), getMeasuredWidth()/2, 150, paint);
        canvas.drawLine(10, 155, (float)linewith[3] * 20, 155, paint);
    }
    public void setMagnitudeData(MagnitudeData magnitudeData){
        this.mMagnitudeData = magnitudeData;
        invalidate();
    }
    public void setXyzMaximumRange(float value){
        xyzMaximumRange = value;
    }
}
