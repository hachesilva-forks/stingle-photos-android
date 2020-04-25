package org.stingle.photos.Gallery.Helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.stingle.photos.AsyncTasks.Gallery.AddAlbumAsyncTask;
import org.stingle.photos.AsyncTasks.OnAsyncTaskFinish;
import org.stingle.photos.Db.Objects.StingleDbAlbum;
import org.stingle.photos.Db.Query.AlbumsDb;
import org.stingle.photos.GalleryActivity;
import org.stingle.photos.R;
import org.stingle.photos.Sync.SyncManager;

public class GalleryHelpers {
	public static void addAlbum(Context context, OnAsyncTaskFinish onFinish){
		MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
		builder.setView(R.layout.add_album_dialog);
		builder.setCancelable(true);
		AlertDialog addAlbumDialog = builder.create();
		addAlbumDialog.show();

		Button okButton = addAlbumDialog.findViewById(R.id.okButton);
		Button cancelButton = addAlbumDialog.findViewById(R.id.cancelButton);
		final EditText albumNameText = addAlbumDialog.findViewById(R.id.album_name);

		final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

		okButton.setOnClickListener(v -> {
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			String albumName = albumNameText.getText().toString();
			(new AddAlbumAsyncTask(context, albumName, new OnAsyncTaskFinish() {
				@Override
				public void onFinish(Object album) {
					super.onFinish(album);
					addAlbumDialog.dismiss();
					onFinish.onFinish(album);
				}

				@Override
				public void onFail() {
					super.onFail();
					addAlbumDialog.dismiss();
					onFinish.onFail();
				}
			})).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		});

		cancelButton.setOnClickListener(v -> {
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			addAlbumDialog.dismiss();
		});
	}

	public static StingleDbAlbum getCurrentAlbum(GalleryActivity activity){
		String albumId = activity.getCurrentAlbumId();
		if(activity.getCurrentSet() == SyncManager.ALBUM && albumId != null){

			AlbumsDb albumsDb = new AlbumsDb(activity);
			StingleDbAlbum album = albumsDb.getAlbumById(albumId);
			albumsDb.close();

			return album;
		}

		return null;
	}
}