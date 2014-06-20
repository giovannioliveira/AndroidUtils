import java.io.File;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;

public class FileUtils {
    public static void delete(File folder) {
        if (folder != null && folder.listFiles() != null) {
            for (File f : folder.listFiles()) {
                if (f.isDirectory()) {
                    if (f.list() == null) {
                        System.out.println("Removing file "+f.getAbsolutePath());
                        f.delete();
                    } else {
                        delete(f);
                    }
                } else {
                    System.out.println("Removing file "+f.getAbsolutePath());
                    f.delete();
                }
            }
            System.out.println("Removing file "+folder.getAbsolutePath());
            folder.delete();
        }
    }
    
	public static Bitmap decodeSampledBitmapFromFile(File file,int reqWidth, int reqHeight) {

	    boolean mustRotate = false;
	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(file.getAbsolutePath(), options);

	    if (options.outWidth > options.outHeight) {
	    	mustRotate = true;
	    }
	    
	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath(), options); 
	    
	    int rotate = 0;
        try {
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	    
	    
	    //Picture taken in portrait must rotate
	    if(mustRotate){
	    	bm = rotateBitmap(bm, rotate);
	    }
	    
	    return ThumbnailUtils.extractThumbnail(bm, reqWidth, reqHeight);
	}
	
	public static Bitmap rotateBitmap(Bitmap bitmap, int degree) {
	    Matrix matrix = new Matrix();
	    matrix.postRotate(degree);
	    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}
		
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    
	    if (height > reqHeight || width > reqWidth) {
	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }
	
	    return inSampleSize;
	}
	
    public static Bitmap getRoundedBitmap(Bitmap bmp, int radius) {
		Bitmap sbmp;
		
		if (bmp.getWidth() != radius || bmp.getHeight() != radius) {
			float smallest = Math.min(bmp.getWidth(), bmp.getHeight());
			float factor = smallest / radius;
			sbmp = Bitmap.createScaledBitmap(bmp, (int)(bmp.getWidth() / factor), (int)(bmp.getHeight() / factor), false);
		} else {
			sbmp = bmp;
		}
		
		Bitmap output = Bitmap.createBitmap(radius, radius,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
 
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, radius, radius);
 
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.parseColor("#BAB399"));
		canvas.drawCircle(radius / 2 + 0.7f,
				radius / 2 + 0.7f, radius / 2 + 0.1f, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);
 
		return output;
	}

}
