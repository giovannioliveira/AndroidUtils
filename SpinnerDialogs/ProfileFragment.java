		userBirth.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				if(event.getAction() == MotionEvent.ACTION_UP){
					
					DatePickerDailog dp = new DatePickerDailog(activity,
		    				calendar, new DatePickerDailog.DatePickerListner() {

		    					@Override
		    					public void OnDoneButton(Dialog datedialog, Calendar c) {
		    						datedialog.dismiss();
		    						c.set(Calendar.YEAR, c.get(Calendar.YEAR));
		    						c.set(Calendar.MONTH,c.get(Calendar.MONTH));
		    						c.set(Calendar.DAY_OF_MONTH,c.get(Calendar.DAY_OF_MONTH));
		    						((EditText)userBirth).setText(new SimpleDateFormat("dd/MM/yyyy").format(c.getTime()));
		    					}

		    					@Override
		    					public void OnCancelButton(Dialog datedialog) {
		    						// TODO Auto-generated method stub
		    						datedialog.dismiss();
		    					}
		    				});
					
					dp.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
					
					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
					Window window = dp.getWindow();
					lp.copyFrom(window.getAttributes());

					lp.width = WindowManager.LayoutParams.MATCH_PARENT;
					lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
					window.setAttributes(lp);
					
					dp.show();
				}
				return false;	
			}
		});

