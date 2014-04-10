package com.vuseniordesign.safekey;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class CustomizerFragment extends Fragment {

    public static final String ARG_OBJECT = "object";
    
    RadioGroup rGroup;
    static CharSequence radioText;
    
    static CustomizerFragment init(){
    	CustomizerFragment newFrag = new CustomizerFragment();
     	return newFrag;        	
     }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_data_display, container, false);
         
        rGroup = (RadioGroup) rootView.findViewById(R.id.radiogrp);
        
        TextView start = (TextView)rootView.findViewById(R.id.startDate);
        TextView end = (TextView)rootView.findViewById(R.id.endDate);
        
        start.setOnClickListener(datepicker);
        end.setOnClickListener(datepicker);
        
    rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
    {
        public void onCheckedChanged(RadioGroup rGroup, int checkedId)
        {
            // This will get the radiobutton that has changed in its check state
            RadioButton checkedRadioButton = (RadioButton)rGroup.findViewById(checkedId);
            // This puts the value (true/false) into the variable
            boolean isChecked = checkedRadioButton.isChecked();
            // If the radiobutton that has changed in check state is now checked...
            if (isChecked)
            {
                // Changes the textview's text to "Checked: example radiobutton text"
                radioText =  checkedRadioButton.getText();
                if (radioText.equals("Custom")){
                	
                }
            }
        }
    });
    
    return rootView;
}
    
    OnClickListener datepicker = new OnClickListener(){
		@Override
		public void onClick(View v) {
			showDatePickerDialog(v);
		}
    };
    
    public void showDatePickerDialog(View v) {
        new DatePickerFragment((TextView) v).show(getActivity().getSupportFragmentManager(), "datePicker");
    }
    
    @SuppressLint("ValidFragment")
	public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    	public TextView activity_text;

    	public DatePickerFragment(TextView tv) {
    	    activity_text = tv;
    	}

    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    	    // Use the current date as the default date in the picker
    	    final Calendar c = Calendar.getInstance();
    	    int year = c.get(Calendar.YEAR);
    	    int month = c.get(Calendar.MONTH);
    	    int day = c.get(Calendar.DAY_OF_MONTH);

    	    // Create a new instance of DatePickerDialog and return it
    	    return new DatePickerDialog(getActivity(), this, year, month, day);
    	}

    	    @Override
    	    public void onDateSet(DatePicker view, int year, int month, int day) {
    	        activity_text.setText(String.valueOf(month + 1 ) + "/" + String.valueOf(day) + "/" + String.valueOf(year));
    	    }
    	}

}
