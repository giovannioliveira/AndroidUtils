
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.TypedValue;
import android.widget.ImageView;

import com.dezinove.fnfusuario.R;
public class ImageUtils {

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int color, int cornerDips, int borderDips, Context context) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int borderSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, (float) borderDips,
                context.getResources().getDisplayMetrics());
        final int cornerSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, (float) cornerDips,
                context.getResources().getDisplayMetrics());
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        // prepare canvas for transfer
        paint.setAntiAlias(true);
        paint.setColor(0xFFFFFFFF);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

        // draw bitmap
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        // draw border
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) borderSizePx);
        canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

        return output;
    }
    
    public static Bitmap squareBitmap(Bitmap srcBmp){
    	Bitmap dstBmp;
    	if (srcBmp.getWidth() >= srcBmp.getHeight()){

    		  dstBmp = Bitmap.createBitmap(
    		     srcBmp, 
    		     srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
    		     0,
    		     srcBmp.getHeight(), 
    		     srcBmp.getHeight()
    		     );

    		}else{

    		  dstBmp = Bitmap.createBitmap(
    		     srcBmp,
    		     0, 
    		     srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
    		     srcBmp.getWidth(),
    		     srcBmp.getWidth() 
    		     );
    		}
    	
    	return dstBmp;
    	
    }

}
