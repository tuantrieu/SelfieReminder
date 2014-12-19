package com.kun.dailyselfie;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * The fragment to display a list of pictures 
 * @author Tuan
 *
 */
public class PicturesFragment extends Fragment {
	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final int DIMX = 64;
	private static final int DIMY = 64;
	
	private ListSelectionListener mListener = null;
	
	private ImageListAdapter mAdapter;
	private File mPhotoFile;
	private GridView mGridView;
	private Context mContext;
		
	public interface ListSelectionListener {
		public void onListSelection(int imgPathId);
	}
	
	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);
		
		mListener = (MainActivity) activity;
		mContext= activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
	}	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		//locate the directory containing existing pictures
		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);	
		
		//make an adapter list of pictures with each has size DIMX and DIMY
		mAdapter = new ImageListAdapter(mContext,DIMX,DIMY,mListener);
		File[] files = storageDir.listFiles();
		for(File file: files){
			if (file.isFile() && file.getName().contains(".jpg")){
				//add picture into the adapter
				mAdapter.addImagePath(file.getAbsolutePath());
			}
		}

		mGridView.setAdapter(mAdapter);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.pictures_grid, null);
		mGridView = (GridView) v.findViewById(R.id.grid_view);
		
		return v; 
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		inflater.inflate(R.menu.main, menu);		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getItemId() == R.id.menu_item_taking_picture ) {
			
		    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		    if (takePictureIntent.resolveActivity(this.getActivity().getPackageManager()) != null) {
	
		    	mPhotoFile = null;
		        try {
		        	//create a file for the picture to be taken
		        	mPhotoFile = createImageFile();
		        } catch (IOException ex) {
		        	ex.printStackTrace();
		        }
		        // Continue only if the File was successfully created
		        if (mPhotoFile != null) {
		        	//take the picture using the camera and save it into the file created 
		            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(mPhotoFile));	            
		            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		            
		            
		        }
		    }
		}

		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
	    	
	    	//if a picture has been taken and saved successfully, update the adapter 
	    	if (mPhotoFile != null){
	    		mAdapter.addImagePath(mPhotoFile.getAbsolutePath());
	    		mAdapter.notifyDataSetChanged();
	    		mListener.onListSelection(mAdapter.getImagesPath().size() - 1);
	    	}
	    	
	    }
	}
	
	/**
	 * Create a file to store the picture taken by the camera
	 * @return
	 * @throws IOException
	 */
	private File createImageFile() throws IOException {
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis()));
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES);
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );
	    
	    return image;
	}
	
	
}
