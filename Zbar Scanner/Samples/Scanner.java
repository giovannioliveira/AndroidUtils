import java.util.ArrayList;
import java.util.List;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
/* Import ZBar Class files */
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.keymax.aplicacaopontosvirtuais.bens.Cliente;
import com.keymax.aplicacaopontosvirtuais.utilitarios.InternalStorage;

public class Scanner extends Activity {

	Button busca, cartoes, scanButton, scan, entrar,opc,codigo,volta;
	private static Camera mCamera;
	private CameraPreview mPreview;
	private Handler autoFocusHandler;

	ImageScanner scanner;
	EditText scanText;
	

	private boolean barcodeScanned = false;
	private boolean previewing = true;

	static {
		System.loadLibrary("iconv");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scanner);
		busca = (Button) findViewById(R.id.btBuscascanner);
		cartoes = (Button) findViewById(R.id.btCartoesscanner);
		scanButton = (Button) findViewById(R.id.btScannerscanner);
		codigo = (Button)findViewById(R.id.btCodigoscanner);
		opc = (Button) findViewById(R.id.btOpcscanner);
		scan = (Button) findViewById(R.id.btScanscanner);
		entrar = (Button) findViewById(R.id.btEntrarscanner);
        scanText = (EditText)findViewById(R.id.etCodigoscanner);
        volta = (Button)findViewById(R.id.btVoltascanner);

		volta.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) { 
	        	
            onBackPressed();
	        	
	        }});

		busca.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent myIntent = new Intent(v.getContext(), Busca.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(myIntent);
				releaseCamera();
				finish();

			}
		});

		cartoes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent myIntent = new Intent(v.getContext(), Cartoes.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(myIntent);
				releaseCamera();
				finish();

			}
		});

		scanButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		
		codigo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent myIntent = new Intent(v.getContext(), Codigo.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(myIntent);
				releaseCamera();
				finish();
				
			}
		});
		
		opc.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) { 
	        	
            openOptionsMenu();
            
	        }});
		
		 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

	        autoFocusHandler = new Handler();
	        mCamera = getCameraInstance();

	        /* Instance barcode scanner */
	        scanner = new ImageScanner();
	        scanner.setConfig(0, Config.X_DENSITY, 3);
	        scanner.setConfig(0, Config.Y_DENSITY, 3);

	        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
	        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
	        preview.addView(mPreview);

	        scan.setOnClickListener(new OnClickListener() {
	                public void onClick(View v) {
	                    if (barcodeScanned) {
	                        barcodeScanned = false;
	                        scanText.setText("Escaneando...");
	                        mCamera.setPreviewCallback(previewCb);
	                        mCamera.startPreview();
	                        previewing = true;
	                        mCamera.autoFocus(autoFocusCB);
	                    }
	                }
	            });
	        entrar.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					String dados;
					dados = scanText.getText().toString();

					HttpClient httpclient = new DefaultHttpClient();
			           
		        	HttpPost httppost = new HttpPost("http://keymax.com.br/pontos/validaPontos.php");

		        	    try {
		        	        // Add your data
		        	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        	        Cliente cl = (Cliente) InternalStorage.readObject(getBaseContext(), "cliente");   	        
		        	        nameValuePairs.add(new BasicNameValuePair("idcl", cl.getIdcl()));
		        	        nameValuePairs.add(new BasicNameValuePair("codigo", scanText.getText().toString()));
		        	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        	        // Execute HTTP Post Request
		        	        HttpResponse response = httpclient.execute(httppost);
		        	        
		        	        String responseBody = EntityUtils.toString(response.getEntity()); 
		        	 	    scanText.setText("");
		        	 	    
		        	 	    if(responseBody.equals("ok")){
		        	 	    	Toast.makeText(Scanner.this, "Ponto computado! Atualize suas listas de Busca e Cartões.", Toast.LENGTH_LONG).show();	
		        	 	    }else if(responseBody.equals("utilizado")){
		        	 	    	Toast.makeText(Scanner.this, "Esse cartão já foi utilizado!", Toast.LENGTH_LONG).show();
		        	 	    }else if(responseBody.equals("bloqueado")){
		        	 	    	Toast.makeText(Scanner.this, "Esse cartão está bloqueado no momento!", Toast.LENGTH_LONG).show();
		        	 	    }else {
		        	 	    	Toast.makeText(Scanner.this, "Numero de cartão inválido!", Toast.LENGTH_LONG).show();
		        	 	    }
		        	        
		        	        
		        	    } catch (Exception e) {
		        	    	Toast.makeText(Scanner.this, "Erro de conexão! (Erro:"+e+")", Toast.LENGTH_LONG).show();
		        	    } 

				}
			});

	}
	
	@Override
    public void onBackPressed() {
     super.onBackPressed();
     
     Intent myIntent = new Intent(getBaseContext(), Desbloqueio.class);
 		myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
 		myIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
 		startActivity(myIntent);
 		
 		releaseCamera();
     
     	finish();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.configmenu, menu);
        return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
         
        switch (item.getItemId())
        {
        case R.id.menu_comousar:
        	Intent myIntent = new Intent(this, ComoUsar.class);
            startActivityForResult(myIntent, 0);	
            return true;
 
        case R.id.menu_minhaconta:
        	Intent myIntent2 = new Intent(this, MinhaConta.class);
            startActivityForResult(myIntent2, 0);	
            return true;
 
        case R.id.menu_mudarsenha:
        	Intent myIntent3 = new Intent(this, MudarSenha.class);
            startActivityForResult(myIntent3, 0);	
            return true;
 
        case R.id.menu_termosdeuso:
        	Intent myIntent4 = new Intent(this, TermosdeUso.class);
            startActivityForResult(myIntent4, 0);	
            return true;
 
        case R.id.menu_contato:
        	Intent myIntent5 = new Intent(this, EntreemContato.class);
            startActivityForResult(myIntent5, 0);	
            return true;
 
        case R.id.menu_sobre:
        	Intent myIntent6 = new Intent(this, QuemSomos.class);
            startActivityForResult(myIntent6, 0);	
            return true;
 
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
    	Log.v("instance", "start");
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
        	Log.v("release", "posif");
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
                        scanText.setText(sym.getData());
                        
                        barcodeScanned = true;
                        
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

}
