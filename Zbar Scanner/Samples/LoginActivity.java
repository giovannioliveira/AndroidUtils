import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.dezinove.fnfusuario.CameraPreview;
import com.dezinove.fnfusuario.R;
import com.dezinove.fnfusuario.dialog.NotifyAlertDialogFragment;
import com.dezinove.fnfusuario.model.StatusResponse;
import com.dezinove.fnfusuario.model.User;
import com.dezinove.fnfusuario.util.Constantes;
import com.dezinove.fnfusuario.util.FontUtils;
import com.dezinove.fnfusuario.util.FontUtils.FontFamily;
import com.dezinove.fnfusuario.util.InternalStorage;
import com.dezinove.fnfusuario.util.Util;
import com.dezinove.fnfusuario.webservice.Api;
import com.dezinove.fnfusuario.webservice.ApiService;
import com.facebook.Session;
import com.facebook.SessionState;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class LoginActivity extends FragmentActivity{

    private Api api;
    private AutoCompleteTextView userMail;
    private EditText userKey;
    private List<String> emailList;
    private String accessToken;

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    
    String SENDER_ID = "528846769405";
    String TAG = "Fnf";

    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;
    LinearLayout loginProgress;
    String regid;
    
    boolean customTitleSupported;
    Window window;
    
    //for qrcode scan
	private static Camera mCamera;
	private CameraPreview mPreview;
	private Handler autoFocusHandler;

	ImageScanner scanner;

	private boolean barcodeScanned = false;
	private boolean previewing = true;
	
	private Dialog qrScannerDialog;
	
	static {
		System.loadLibrary("iconv");
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        customTitleSupported = false;
        window = getWindow();
        // If the window has a container, then we are not free to request window features.
        if (window.getContainer() == null) {
        	customTitleSupported = window.requestFeature(Window.FEATURE_CUSTOM_TITLE);
        }
        
		api = ApiService.getInstance(this).provideApiService();

        setContentView(R.layout.login_activity);
         
        context = getApplicationContext();
        
        userMail = (AutoCompleteTextView) findViewById(R.id.emailLogin);
        userKey = (EditText) findViewById(R.id.passwordLogin);
        
        loginProgress = (LinearLayout) findViewById(R.id.llLoginProgress);
        
        findViewById(R.id.flLoginActivity).requestFocus();

        setFonts();
        
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	// google analytics
    	EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
    	super.onStop();
    	// google analytics
    	EasyTracker.getInstance(this).activityStop(this);
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    	if(qrScannerDialog!=null){
    	qrScannerDialog.cancel();
    	}
    }
    
	@SuppressWarnings("unchecked")
	@Override
    public void onResume(){
    	super.onResume();
    	
        if (customTitleSupported) {
            window.setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title_simple);
            // Set up the custom title
        }
    	
    	checkPlayServices();
    	
 		try {
			emailList = (List<String>) InternalStorage.readObject(this, "emailList");
		} catch (Exception e) {
			// TODO Auto-generated catch block
	 		emailList = new ArrayList<String>();
	 		try {
				InternalStorage.writeObject(this, "emailList", emailList);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} 
 		
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.dropdown_item, emailList);

        userMail.setAdapter(adapter);
    	
    	return;
    }

    public void login(View v) {
    	
    	String email = userMail.getText().toString();
    	String password = userKey.getText().toString();
    	
    	if (password.length() == 0 || email.length() == 0){
    		showNotifyDialog(getResources().getString(R.string.title_login_error),
    						 getResources().getString(R.string.invalid_email_password), 
    						 Constantes.DIALOG_ID_ERRO);

    	}else{
    		
        	loginProgress.setVisibility(View.VISIBLE);
    		
    		api.userLogin(email, password, 0, Util.getOsVersion(), Util.getAppVersion(this),Util.getTimeStamp(), loginCallback);
    	}
    }
    
    public void facebookLogin(View v){
    	
    	Session.StatusCallback statusCallback = new Session.StatusCallback() {
		    // callback when session changes state
		    @Override
		    public void call(Session session, SessionState state, Exception exception) {
		    	if (session.isOpened()) {
		    		
		    		accessToken = session.getAccessToken();
		    		
		    		loginProgress.setVisibility(View.VISIBLE);
		    		
		    		api.facebookLogin(session.getAccessToken(), 
		    						  Util.getOsVersion(), 
		    						  Util.getAppVersion(LoginActivity.this), 
		    						  Util.getTimeStamp(),facebookLoginCallback);
		    	}
		    }
		  };

    	 Session.OpenRequest request = new Session.OpenRequest(this);
    	 request.setCallback(statusCallback);
    	 Session.openActiveSession(this, true, Arrays.asList("email","user_birthday"), statusCallback);
    }
    
    public void scanGiftCard(View v){
    	
        autoFocusHandler = new Handler();

        releaseCamera(); 
        mCamera = getCameraInstance();
        
        if(mCamera==null){
        	showNotifyDialog("", getString(R.string.error_opening_camera), Constantes.DIALOG_ID_ERRO);
        	return;
        }

        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        
        View previewLayout = getLayoutInflater().inflate(R.layout.preview_camera_layout, null);
        FrameLayout preview = (FrameLayout) previewLayout.findViewById(R.id.flScanDialog);
        preview.addView(mPreview);
        
        if (barcodeScanned) {
            barcodeScanned = false;
            mCamera.setPreviewCallback(previewCb);
            mCamera.startPreview();
            previewing = true;
            mCamera.autoFocus(autoFocusCB);
        }
        
        callQrScannerDialog(previewLayout);
       
    }
    
    public void callQrScannerDialog(View v){
    	
        qrScannerDialog = new Dialog(this);
        
        qrScannerDialog.setContentView(v);
		qrScannerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		Window window = qrScannerDialog.getWindow();
		lp.copyFrom(window.getAttributes());

		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);
        
		qrScannerDialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				releaseCamera();
			}
		});
		
		qrScannerDialog.show();
		
    }
    
    public void resultFromQrRead(String result){
    	if(result!=null){
    	loginProgress.setVisibility(View.VISIBLE);
    	api.giftCardBalance(result,
    						Util.getTimeStamp(), 
    						giftCardCallback);
    	}
    }
    
    protected final Callback<StatusResponse> giftCardCallback = new Callback<StatusResponse>() {
    	
		@Override
		public void success(StatusResponse arg0, Response arg1) {
			// TODO Auto-generated method stub
			loginProgress.setVisibility(View.GONE);
			if(arg0.getStatus().equals("OK")){
				try{
				String end = "";
				if(arg0.getBalance()!=0){
					end = getString(R.string.sucess_gift_card2);
				}
	    		showNotifyDialog(getString(R.string.gift_card_title),
						 		getString(R.string.sucess_gift_card) + " R$ " +
						 		String.format("%.2f", arg0.getBalance()) + 
						 		end, 
						 		Constantes.DIALOG_ID_ERRO);
				}catch(Exception e){
					e.printStackTrace();
				}
			}else{
				try{
					showNotifyDialog(getString(R.string.gift_card_title), 
									 getString(R.string.invalid_gift_card_message), 
									 Constantes.DIALOG_ID_ERRO);
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
		}

		@Override
		public void failure(RetrofitError arg0) {
			// TODO Auto-generated method stub
			arg0.printStackTrace();
			loginProgress.setVisibility(View.GONE);
    		showNotifyDialog(getResources().getString(R.string.title_connection_error),
					 getResources().getString(R.string.connection_error), 
					 Constantes.DIALOG_ID_ERRO);
		}

	};

    protected final Callback<User> facebookLoginCallback = new Callback<User>(){
    	
		@Override
		public void success(User user, Response response) {
			// TODO Auto-generated method stub
			
	    	loginProgress.setVisibility(View.GONE);
	    	
			if(user.getStatus().equals("OK")){
				
        		User.setInstancia(user);   		
    			startActivity(new Intent(getBaseContext(), DrawerActivity.class));
    			
    	        GCM();
    	 		
                finish();	
				
			}else if(user.getStatus().equals("USER NOT ASSOCIATED")){
				
				Intent associateIntent = new Intent(LoginActivity.this, AssociateFacebookActivity.class);   
				associateIntent.putExtra("ACCESS_TOKEN", accessToken);
				startActivity(associateIntent);
				finish();
				
			}else{
				
	    		showNotifyDialog(getResources().getString(R.string.title_login_error),
		  		 		 getResources().getString(R.string.login_error), 
		  		 		 Constantes.DIALOG_ID_ERRO);
	    		
			}
			
		}

		@Override
		public void failure(RetrofitError arg0) {
			// TODO Auto-generated method stub
			
	    	loginProgress.setVisibility(View.GONE);
	    	
    		showNotifyDialog(getResources().getString(R.string.title_login_error),
	  		 		 getResources().getString(R.string.login_error), 
	  		 		 Constantes.DIALOG_ID_ERRO);
		}
    	
    };
     
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
    	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

    }

    public void newAccount(View v) {
    	if (isNetworkAvailable()){
    		startActivity(new Intent(getBaseContext(), NewAccountActivity.class));

    	}else{
    		showNotifyDialog(getResources().getString(R.string.title_connection_error),
    						 getResources().getString(R.string.connection_error), 
    						 Constantes.DIALOG_ID_ERRO);
    	}
    }

    public void forgotPassword(View v) {
    	startActivity(new Intent(getBaseContext(), ForgotPasswordActivity.class));
    }
    
    protected final Callback<User> loginCallback = new Callback<User>() {
        public void success(User user, Response response) {
        	
        	loginProgress.setVisibility(View.GONE);
        	
        	if(!user.getStatus().equalsIgnoreCase("OK")){
        		showNotifyDialog(getResources().getString(R.string.title_login_error),
						  		 getResources().getString(R.string.login_error), 
						  		 Constantes.DIALOG_ID_ERRO);

        	}else{
        		
        		User.setInstancia(user);   		
    			startActivity(new Intent(getBaseContext(), DrawerActivity.class));
    			
    	        GCM();
    	 		
    	 		if(!emailList.contains(userMail.getText().toString())){
    	 			emailList.add(userMail.getText().toString());
    	 			try {
						InternalStorage.writeObject(context, "emailList", emailList);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    	 		}
    	 		
                finish();
        	}
        	
        }

        public void failure(RetrofitError retrofitError) {
        	
        	loginProgress.setVisibility(View.GONE);
        	
    		showNotifyDialog(getResources().getString(R.string.title_login_error),
			  		 		 getResources().getString(R.string.login_error), 
			  		 		 Constantes.DIALOG_ID_ERRO);
        }
    };
    
    protected final Callback<StatusResponse> gcmregisterCallback = new Callback<StatusResponse>() {

		@Override
		public void failure(RetrofitError arg0) {
			// TODO Auto-generated method stub
			arg0.printStackTrace();
		}

		@Override
		public void success(StatusResponse arg0, Response arg1) {
			// TODO Auto-generated method stub
			Log.v("BACKENDGCM", arg0.getStatus());
			
		}
    };

	private void showNotifyDialog(String title, String message, int messageType) {
		try{
		NotifyAlertDialogFragment newFragment = NotifyAlertDialogFragment.newInstance(title, message, messageType);
        newFragment.show(getSupportFragmentManager(), "dialog");
		}catch(Exception e){
			e.printStackTrace();
		}
    }
	
	
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
    
    private boolean checkPlayServices() {
    	
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    
    public void GCM(){
    	
    	if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(context);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {          	
               registerInBackground();
            }else{
            	api.sendGCMId(User.getInstancia().getToken(), regid,Util.getTimeStamp(), gcmregisterCallback);
            	Log.v("BACKENDGCM", User.getInstancia().getToken());
            }
        }else{
            Log.i("tag", "No valid Google Play Services APK found.");
        }
    }
    
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        Log.v("reg",registrationId);
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }

        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
    
    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences(LoginActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
    
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
   
    
    private void sendRegistrationIdToBackend() {
        api.sendGCMId(User.getInstancia().getToken(), regid,Util.getTimeStamp(), gcmregisterCallback);
        Log.v("BACKENDGCM", User.getInstancia().getToken());
    }
    
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
    
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>()  {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    sendRegistrationIdToBackend();
                    storeRegistrationId(context, regid);
                    
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.v("mensagem", msg);

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.v("postresponse", msg);
            }
        }.execute(null, null, null);
    }
    
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
//            Parameters p = c.getParameters();
//            p.setFlashMode(Parameters.FLASH_MODE_TORCH);
//            c.setParameters(p);
        } catch (Exception e){
        	e.printStackTrace();
        }
        return c;
    }
    
    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }
    
    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    PreviewCallback previewCb = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);
            
            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                
                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    resultFromQrRead(sym.getData());
                    barcodeScanned = true;
                    releaseCamera();
                    qrScannerDialog.dismiss();
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(500);
                }
            }
        }
    };

    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };
    
    private void setFonts() {
    	
    	FontUtils.setGothamFont(this, findViewById(R.id.label_email), FontUtils.FontFamily.GOTHAM_BOOK);
    	FontUtils.setGothamFont(this, findViewById(R.id.emailLogin), FontUtils.FontFamily.GOTHAM_BOOK);
    	FontUtils.setGothamFont(this, findViewById(R.id.label_password), FontUtils.FontFamily.GOTHAM_BOOK);
    	FontUtils.setGothamFont(this, findViewById(R.id.passwordLogin), FontUtils.FontFamily.GOTHAM_BOOK);
    	FontUtils.setGothamFont(this, findViewById(R.id.forgot_password_bt), FontUtils.FontFamily.GOTHAM_BOLD);
    	FontUtils.setGothamFont(this, findViewById(R.id.login_bt), FontUtils.FontFamily.GOTHAM_BOLD);
    	FontUtils.setGothamFont(this, findViewById(R.id.btGiftCardLogin), FontUtils.FontFamily.GOTHAM_BOLD);
    	FontUtils.setGothamFont(this, findViewById(R.id.withoutAccount), FontUtils.FontFamily.GOTHAM_BOLD);
    	FontUtils.setGothamFont(this, findViewById(R.id.new_account_bt), FontUtils.FontFamily.GOTHAM_BOLD);
    	FontUtils.setGothamFont(this, findViewById(R.id.btFacebookLogin), FontUtils.FontFamily.GOTHAM_BOLD);
    }
    
}



