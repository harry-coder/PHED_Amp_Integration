package com.fedco.mbc.utils;

/*
 * Import Application Package
 */
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.fedco.mbc.R;
/*
 * Import Android Packages
 */

/**
 * Called to create a dialog
 */
public class CustomDialog extends DialogFragment implements OnClickListener{
	public static final String TAG = CustomDialog.class.getSimpleName();
	private ProgressBar mProgressBar ,mProgressBarfile;
	private OnCustomDialogClick mClickListener = null;
	private TextView mProgressTextView ,mProgressTextViewFile;
	public void setmClickListener(OnCustomDialogClick mClickListener) {
		this.mClickListener = mClickListener;
	}

	/*
         * Called on fragment creation
         * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
         */
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = (View) inflater.inflate(R.layout.customprogress, null);
//		Button buttonPositive = (Button) view.findViewById(R.id.dialog_button_positive);
//		Button buttonNegative = (Button) view.findViewById(R.id.dialog_button_negative);
		mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
		mProgressTextView = (TextView) view.findViewById(R.id.progress_txt);
		mProgressBarfile = (ProgressBar) view.findViewById(R.id.progress_bar2);
		mProgressTextViewFile = (TextView) view.findViewById(R.id.progress_txt2);

//		buttonNegative.setOnClickListener(this);
//		buttonPositive.setEnabled(false);
//		buttonPositive.setOnClickListener(this);
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return view;
	}


	/*
	 * Create dialog to non cancelable on touch outside
	 * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog =  super.onCreateDialog(savedInstanceState);
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}

	/*
	 * Dismiss the dialog on button click
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View iView) {
		if(mClickListener!= null){
			switch (iView.getId()) {
//			case R.id.dialog_button_positive:
//				mClickListener.onCustomDialogClickView(true);
//				break;
//			case R.id.dialog_button_negative:
//				mClickListener.onCustomDialogClickView(false);
//				break;

			default:
				break;
			}
		}
		dismiss();
	}

	public void setProgress(Integer... progress){
//		mProgressBar.setProgress(progress[2]);

		mProgressBarfile.setProgress(progress[0]);
		mProgressBarfile.setMax(progress[1]);
		mProgressTextViewFile.setText(String.valueOf(progress[0]) + " line of " + String.valueOf(progress[1])+" lines");

		mProgressBar.setProgress(progress[3]);
		mProgressBar.setMax(progress[2]);
		mProgressTextView.setText(String.valueOf(progress[3]) + " file of " + progress[2]+" Files");

//		diaText1.setText(" From ");
//            diaText2.setText("Loading - line no." + values[0] + " of "+values[1] + " From " +values[3]+" of "+values[2]+ " FILES");
//            diaText1.setText("Loading");
	}


	@Override
	public void onCancel(DialogInterface dialog) {
//		super.onCancel(dialog);
	}
}