package sensors.mmbuw.example.com.sensors;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;

/**
 * Created by payam on 5/25/15.
 */
public class FFTTransformView extends View {
    private Paint paint = new Paint();
    private MagnitudeData mMagnitudeData;
    public FFTTransformView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        if(mMagnitudeData == null)
            return;
        paint.setStrokeWidth(5);

        double[] ttf = mMagnitudeData.GetTransformedResult();
        float xInc = getMeasuredWidth()/ttf.length;
        paint.setColor(Color.BLUE);
        for(int i = 0; i < ttf.length ; i++){
            canvas.drawPoint(i * xInc, (float)ttf[i]*10, paint);
        }



    }
    public void setMagnitudeData(MagnitudeData magnitudeData){
        this.mMagnitudeData = magnitudeData;
        invalidate();
    }
}
