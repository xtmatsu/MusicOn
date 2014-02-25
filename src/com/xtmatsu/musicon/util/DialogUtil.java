package com.xtmatsu.musicon.util;

import android.app.Dialog;
import android.content.Context;

import com.xtmatsu.musicon.R;

public class DialogUtil extends Dialog{

	public DialogUtil(Context context) {
		super(context, R.style.Theme_CustomProgressDialog);  
		  
	    // レイアウトを決定  
	    setContentView(R.layout.custom_progress_dialog);
	}
	

}
