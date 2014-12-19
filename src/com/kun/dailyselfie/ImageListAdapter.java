package com.kun.dailyselfie;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.kun.dailyselfie.PicturesFragment.ListSelectionListener;

/**
 * Implementation of the adapter used to retrieve and manage images
 * @author Tuan
 *
 */
public class ImageListAdapter extends BaseAdapter{
	
	private ArrayList<String> mImagesPath = new ArrayList<String>();
	private Context mContext;
	
	//the listener is used only in the fragment of list of pictures, it is null for one picture fragment
	private ListSelectionListener mListener;
	
	//size of pictures
	private int mDimX,mDimY;
	

	public ImageListAdapter(Context context, int dimX, int dimY, ListSelectionListener listener){
		mContext = context;
		mDimX = dimX;
		mDimY = dimY;
		mListener = listener;
	}
	
	//add the path of the picture
	public void addImagePath(String path){
		mImagesPath.add(path);
	}	
		
	public ArrayList<String> getImagesPath() {
		return mImagesPath;
	}

	public void setImagesPath(ArrayList<String> imagesPath) {
		mImagesPath = imagesPath;
	}

	@Override
	public int getCount() {			
		return mImagesPath.size();
	}

	@Override
	public Object getItem(int arg0) {		
		return mImagesPath.get(arg0);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
	    ImageView img = (ImageView) convertView;
        if (img == null) {
        	img = new ImageView(mContext); 
        }
        
        //set the listener for each picture if this adapter is used by picturesFragment
        if (mListener != null){
        	img.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					mListener.onListSelection(position);
					
				}
			});
        }
        
        //load the image into the view img before returning it
        loadBitmap(mImagesPath.get(position),img);
	    
        return img;
	}
	/**
	 * Load the image into the view
	 * @param path
	 * @param mImageView
	 */
	private void loadBitmap(String path, ImageView mImageView){
		int targetW,targetH;

	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    
	    BitmapFactory.decodeFile(path, bmOptions);
	    
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;

	    //if the dimensions of pictures are passed in, used them to scale the picture
		if (mDimX > 0 && mDimY > 0){
			targetW = mDimX;
			targetH = mDimY;
		//otherwise, display the picture as it is
		}else{
			targetW = photoW;
			targetH = photoH;
		}


	    // Determine how much to scale down the image
	    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;

	    Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
	    mImageView.setImageBitmap(bitmap);
	}
	
}
