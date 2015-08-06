package com.cavsusa.ccastconsole;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cavsusa.ccastconsole.R;
import com.cavsusa.ccastconsole.ListViewAdapter.ViewHolder;

public class ListViewAdapter  extends BaseAdapter
	{
		private ArrayList<String> channelName;
	    private ArrayList<String> channelNumber;
	  //  private ArrayList<Integer> channelPicture;
	    private DirectTvChannels activity;
	 
	    public ListViewAdapter(DirectTvChannels directTvChannels,ArrayList<String> channelName, ArrayList<String> channelNumber) {
	        super();
	        this.channelName = channelName;
	        this.channelNumber = channelNumber;
	   //     this.channelPicture = channelPicture;
	        this.activity = directTvChannels;
	    }
	 
	    @Override
	    public int getCount() {
	        // TODO Auto-generated method stub
	        return channelNumber.size();
	    }
	 
	    @Override
	    public String getItem(int position) {
	        // TODO Auto-generated method stub
	        return channelNumber.get(position);
	    }
	 
	    @Override
	    public long getItemId(int position) {
	        // TODO Auto-generated method stub
	        return 0;
	    }
	 
	    public static class ViewHolder
	    {
	     //   public ImageView channelPicture;
	        public TextView channelNumber;
	        public TextView channelName;
	    }
	 
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        // TODO Auto-generated method stub
	        ViewHolder view;
	        LayoutInflater inflator = activity.getLayoutInflater(null);
	 
	        if(convertView==null)
	        {
	            view = new ViewHolder();
	            convertView = inflator.inflate(R.layout.direct_tv_channels_row, null);
	            
	            view.channelName = (TextView) convertView.findViewById(R.id.DirectTvChannelNumber);
	            view.channelNumber = (TextView) convertView.findViewById(R.id.DirectTvChannelName);
	            //view.channelPicture = (ImageView) convertView.findViewById(R.id.imageView1);
	 
	            convertView.setTag(view);
	        }
	        else
	        {
	            view = (ViewHolder) convertView.getTag();
	        }
	        
	        view.channelName.setText(channelName.get(position));
	        view.channelNumber.setText(channelNumber.get(position));
	    //    view.channelPicture.setImageResource(channelPicture.get(position));
	 
	        return convertView;
	    }
	}

