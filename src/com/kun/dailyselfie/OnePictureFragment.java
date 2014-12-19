package com.kun.dailyselfie;

import java.io.File;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.Toast;

/**
 * The fragment to display one picture
 * @author Tuan
 *
 */
public class OnePictureFragment extends Fragment {
	
	private Gallery mView;
	private ImageListAdapter mAdapter;
	private Context mContext;
	private EmptyListListener mListener;
	
	//The interface allows to call the main activity to remove this fragment when there is no picture to diplay  
	public interface EmptyListListener{
		public void removeFragment(Fragment oneFragment);
	}

	//allow the main activity to specify the picture to be displayed
	public void showImage(int pathId){
		mView.setSelection(pathId);

		
	}
	@Override
	public void onAttach(Activity activity) {		
		super.onAttach(activity);
		mContext = activity;
		mListener= (EmptyListListener) activity;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		
		mAdapter = new ImageListAdapter(mContext,0,0,null);
		//locate the pictures
		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);	

		File[] files = storageDir.listFiles();
		//add all picture paths into the adapter
		for(File file: files){
			if (file.isFile() && file.getName().contains(".jpg")){
				mAdapter.addImagePath(file.getAbsolutePath());
			}
		}
		
		mView.setAdapter(mAdapter);
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mView = (Gallery) inflater.inflate(R.layout.one_image_gallery,container,false);
		return mView;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.one_picture_options, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		//if delete is selected
		if (item.getItemId() == R.id.menu_item_delete_picture){
			int currentId = (int)mView.getSelectedItemId();
			File file = new File(mAdapter.getImagesPath().get(currentId));
			//if deleting the picture successfully, update the adapter
			if (file.delete()){
				mAdapter.getImagesPath().remove(currentId);
				mAdapter.notifyDataSetChanged();
				//display the previous picture of the deleted one
				if (mAdapter.getImagesPath().size() > 0){
					currentId = (currentId - 1) % mAdapter.getImagesPath().size(); 
					showImage(currentId);
				//if no picture is left, return to the main fragment to allow take more pictures	
				}else{
					mListener.removeFragment(this);
				}
				
				Toast.makeText(mContext, "The picture has been deleted!", Toast.LENGTH_SHORT).show();
			}
			
		}
		return super.onOptionsItemSelected(item);
	}

	
}
