package com.vuseniordesign.safekey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class CustomizerFragment extends Fragment {

    public static final String ARG_OBJECT = "object";
    static Calendar calendar = Calendar.getInstance();
    String strdate;
    final SimpleDateFormat curdate = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.ENGLISH);
    
    EditText startDate;
    EditText endDate;
    
    RadioGroup rGroup;
    static CharSequence radioText;
    
    public static Date start;
	public static Date end;
    
    static CustomizerFragment init(){
    	CustomizerFragment newFrag = new CustomizerFragment();
     	return newFrag;        	
     }

    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_data_display, container, false);
         
        rGroup = (RadioGroup) rootView.findViewById(R.id.radiogrp);
        
        startDate = (EditText)rootView.findViewById(R.id.start);
        endDate = (EditText)rootView.findViewById(R.id.endDate);
        
        start = end = new Date();
        
        calendar.setTime(new Date());
        
        
        
        //default
    	strdate = curdate.format(new Date());
    	final String today = curdate.format(new Date());
    	startDate.setText(strdate);
    	endDate.setText(today);
    	
        
        startDate.setOnClickListener(datepicker);
        endDate.setOnClickListener(datepicker);
        calendar.setTime(new Date());
        
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
                // Changes the EditText's text to "Checked: example radiobutton text"
            	startDate.setEnabled(false);
                endDate.setEnabled(false);
                calendar.setTime(new Date());
                radioText =  checkedRadioButton.getText();
                if (radioText.equals("1 year")){                	
                	calendar.add(Calendar.YEAR, -1);
                	strdate = curdate.format(calendar.getTime());
                }else if (radioText.equals("1 month")){
                	calendar.add(Calendar.MONTH, -1);
                    strdate = curdate.format(calendar.getTime());
                }else if (radioText.equals("1 week")){
                	calendar.add(Calendar.DAY_OF_YEAR, -7);
                    strdate = curdate.format(calendar.getTime());
                }else  if (radioText.equals("Custom")){
                	startDate.setEnabled(true);
                	endDate.setEnabled(true);
                }
            }
            startDate.setText(strdate);
        	endDate.setText(today);
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
        new DatePickerFragment((EditText) v).show(getActivity().getSupportFragmentManager(), "datePicker");
    }
    
    @SuppressLint("ValidFragment")
	public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
 
    	public EditText activity_text;
    	
    	long max;
    	long min;
    	int pos;
    	
    	public DatePickerFragment(EditText tv) {
    	    activity_text = tv;
    	    if (tv.getId() == R.id.start){
    	    	max = new Date().getTime();
    	    	pos = 0;
    	    } else {
    	    	Editable date = startDate.getText();
    	    	pos = 1;
    	    	try {
					min = curdate.parse(date.toString()).getTime();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
    	    }
    	}

    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    	    // Use the current date as the default date in the picker
    	    final Calendar c = Calendar.getInstance();
    	    int year = c.get(Calendar.YEAR);
    	    int month = c.get(Calendar.MONTH);
    	    int day = c.get(Calendar.DAY_OF_MONTH);
    	    
    	    DatePickerDialog dpd = new DatePickerDialog(getActivity(), this, year, month, day);    	    
    	    DatePicker dp = dpd.getDatePicker();
    	    if (pos == 0)
    	    	dp.setMaxDate(max);
    	    else
    	    	dp.setMinDate(min);
    	 
    	    // Create a new instance of DatePickerDialog and return it
    	    return dpd;
    	}

    	    @Override
    	    public void onDateSet(DatePicker view, int year, int month, int day) {
    	    	
    	    	calendar.set(year, month, day);
    	        Date date = calendar.getTime();
    	    	String strdate = curdate.format(date);
    	        activity_text.setText(strdate);
    	        
    	        if (pos == 0)
    	    		start = date;
    	    	else
    	    		end = date;
    	    }
    	}

}
