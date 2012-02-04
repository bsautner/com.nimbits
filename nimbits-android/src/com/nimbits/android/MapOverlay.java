/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.android;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import java.util.ArrayList;
import java.util.List;

public class MapOverlay extends ItemizedOverlay{
	private final List<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private final Context mContext;


	public MapOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		  mContext = context;
		}


	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	@Override
	protected boolean onTap(int index) {


   OverlayItem item = mOverlays.get(index);
    Toast.makeText(mContext,item.getTitle() + "  " + item.getSnippet()
    		, Toast.LENGTH_LONG).show();
// 	  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
// 	  dialog.setTitle(item.getTitle());
// 	  dialog.setMessage(item.getSnippet());
//    dialog.show();
	  return true;
	}
}
