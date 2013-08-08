package com.nimbits.android.ui.point;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import com.nimbits.android.R;
import com.nimbits.android.content.ContentProvider;
import com.nimbits.android.ui.PointViewBaseFragment;
import com.nimbits.android.ui.entitylist.EntityListener;

/**
 * Created by benjamin on 8/8/13.
 */
public class PointFragment extends PointViewBaseFragment {
    private final static  String TAG = "PointFragment";


    @SuppressWarnings("unused")
    public PointFragment() {
        super();
    }

    public PointFragment(EntityListener activity) {
        super(activity);
    }

    public static PointFragment getInstance(Activity activity) {
        Log.v(TAG, "instance created");
        PointFragment instance = new PointFragment((EntityListener) activity);
        instance.listener = (EntityListener) activity;
        return instance;
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        view = inflater.inflate(R.layout.point_details_fragment, container, false);
        showEntity(getActivity());

        ImageButton saveButton = (ImageButton) view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) view.findViewById(R.id.new_value);
                if (editText != null && editText.getEditableText() != null) {
                    String value = editText.getEditableText().toString();
                    listener.onNewValue(ContentProvider.getCurrentEntity(), value);
                    editText.getEditableText().clear();
                }

            }
        });


        return view;
    }




    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public void onStop() {
        super.onStop();

    }

}
