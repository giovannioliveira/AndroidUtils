    public void buildDateDialog(final EditText editText, final String items[]){
		
		OnWheelChangedListener listener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {

				selectionNumber = newValue;

			}
		};
		
		final Dialog dialog = new Dialog(activity);
		dialog.setContentView(R.layout.dialog_date);
		
		WheelView wheelView = (WheelView) dialog.findViewById(R.id.wvDialog);
		
		String currentText = editText.getText().toString();
		int currentItem = 0;
		
		for(int i=0; i<items.length; i++){
			if(items[i].equals(currentText)){
				currentItem = i;
			}
		}
		
		wheelView.setViewAdapter(new DateArrayAdapter(activity, items, currentItem));
		wheelView.setCurrentItem(currentItem);
		wheelView.addChangingListener(listener);
		
		Button btPositive = (Button) dialog.findViewById(R.id.btDialogRight);
		Button btNegative = (Button) dialog.findViewById(R.id.btDialogLeft);
		FontUtils.setGothamFont(activity, btPositive, FontUtils.FontFamily.GOTHAM_BOLD);
		FontUtils.setGothamFont(activity, btNegative, FontUtils.FontFamily.GOTHAM_BOOK);
		
		btPositive.setText("Confirmar");
		btNegative.setText("Cancelar");
		
		btPositive.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				editText.setText(items[selectionNumber]);
				dialog.dismiss();
				
			}
		});
		
		btNegative.setOnClickListener(new View.OnClickListener() {
			
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
		
		dialog.show();
		
		
	}
	
