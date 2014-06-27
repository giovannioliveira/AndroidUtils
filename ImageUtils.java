package com.dezinove.fnfusuario.util;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.dezinove.fnfusuario.Contents;
import com.dezinove.fnfusuario.QRCodeEncoder;
import com.dezinove.fnfusuario.R;
import com.dezinove.fnfusuario.model.User;
import com.google.zxing.BarcodeFormat;
import com.squareup.picasso.Picasso;

public class ImageUtils {

    public static void loadImage(String imageURL, ImageView imageView, Context context) {

    	String url = imageURL;
    	Picasso.with(context).load(url)
    	.placeholder(R.drawable.event_banner_default)
    	.into(imageView);
    }
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
    
	public static Bitmap generateFnfCard(Context context, String data){
 		try {
 		
 			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
 			Display display = wm.getDefaultDisplay();
 		
 			@SuppressWarnings("deprecation")
 			int width = display.getWidth();
 			int qrCodeDimention = width;

 			QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(data, null,
 					Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), qrCodeDimention);

 			return qrCodeEncoder.encodeAsBitmap();
 			
 		} catch (Exception e) {
 		    return null;
 		}
	}

}
