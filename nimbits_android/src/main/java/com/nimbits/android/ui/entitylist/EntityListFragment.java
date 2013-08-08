package com.nimbits.android.ui.entitylist;

import android.app.Activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.nimbits.android.content.ContentProvider;
import com.nimbits.android.R;
import com.nimbits.android.ui.PointViewBaseFragment;

/**
 * @Author: benjamin
 */
public class EntityListFragment extends PointViewBaseFragment {
    private final static  String TAG = "EntityListFragment";

    private ListView list;


    private Context context;
    private EntityListAdapter adapter;

    @SuppressWarnings("unused")
    public EntityListFragment() {
        super();

    }

    public EntityListFragment(EntityListener activity) {
        super(activity);
    }

    public static EntityListFragment getInstance(Activity activity) {
        Log.v(TAG, "instance created");
        EntityListFragment instance = new EntityListFragment((EntityListener) activity);
        instance.listener = (EntityListener) activity;

        return instance;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.entity_list_fragment, container, false);

        Log.v(TAG, "view created " + (adapter == null));

        showEntity(getActivity());

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        showEntity(context);

    }

    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public void onStop() {
        super.onStop();


    }



    @Override
    public void showEntity(Context context) {
        super.showEntity(context);
        this.context = context;
        Log.v(TAG, "showEntity" + (context == null));
        adapter = new EntityListAdapter(context, R.id.listView, ContentProvider.getChildEntities());
        adapter.setEntityListener(listener);
        list = (ListView) view.findViewById(R.id.listView);
        list.setAdapter(adapter);


    }








}
