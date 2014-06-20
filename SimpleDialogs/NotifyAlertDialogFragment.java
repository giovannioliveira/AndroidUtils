import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.dezinove.fnfusuario.R;
import com.dezinove.fnfusuario.activity.DrawerActivity;
import com.dezinove.fnfusuario.activity.NewAccountActivity;
import com.dezinove.fnfusuario.fragment.ProfileFragment;
import com.dezinove.fnfusuario.util.Constantes;
import com.dezinove.fnfusuario.util.FontUtils;


public class NotifyAlertDialogFragment extends DialogFragment {
	
	public static NotifyAlertDialogFragment newInstance(String title, String message, int messageType) {
		NotifyAlertDialogFragment frag = new NotifyAlertDialogFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putString("message", message);
		args.putInt("messageType", messageType);
		frag.setArguments(args);
		return frag;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final String title = getArguments().getString("title");
		final String message = getArguments().getString("message");
		final int messageType = getArguments().getInt("messageType");

		Activity activity = getActivity();
		
		final Dialog dialog = new Dialog(activity);
		dialog.setContentView(R.layout.dialog_layout);
		
		TextView tvTitle = (TextView) dialog.findViewById(R.id.tvDialogTitle);
		TextView tvMessage = (TextView) dialog.findViewById(R.id.tvDialogMessage);
		Button btClose = (Button) dialog.findViewById(R.id.btDialogClose);
		
		if(title==null || title.equals("")){
			tvTitle.setVisibility(View.GONE);
		}else{
			tvTitle.setText(title);
		}

		if(message==null || message.equals("")){
			tvMessage.setVisibility(View.GONE);
		}else{
			tvMessage.setText(message);
		}
			
		FontUtils.setGothamFont(activity, tvTitle, FontUtils.FontFamily.GOTHAM_BOLD);
		FontUtils.setGothamFont(activity, tvMessage, FontUtils.FontFamily.GOTHAM_BOOK);
		FontUtils.setGothamFont(activity, btClose, FontUtils.FontFamily.GOTHAM_BOLD);
		
		btClose.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				dialog.dismiss();	
				
			}
		});
		
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		Window window = dialog.getWindow();
		lp.copyFrom(window.getAttributes());

		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);

		return dialog;

	}
}