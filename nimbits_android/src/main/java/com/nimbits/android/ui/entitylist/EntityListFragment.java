package com.nimbits.android.ui.entitylist;

import android.app.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.nimbits.android.content.ContentProvider;
import com.nimbits.android.R;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityName;

/**
 * @Author: benjamin
 */
public class EntityListFragment extends Fragment implements EntityListener {
    private final static  String TAG = "EntityListFragment";

    private ListView list;
    private EntityListener listener;
    private View view;
    private Context context;
    private EntityListAdapter adapter;

    public EntityListFragment() {
    }

    public static final EntityListFragment getInstance(Activity activity) {
        Log.v(TAG, "instance created");
        EntityListFragment instance = new EntityListFragment();
        instance.listener = (EntityListener) activity;
        return instance;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.entity_list, container, false);

        Log.v(TAG, "view created " + (adapter == null));
        showEntity(getActivity());

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume" + (context == null) + " " + (view==null));
        showEntity(context);
    }

    @Override
    public void onEntityClicked(Entity entity) {
        if (listener != null) {
            listener.onEntityClicked(entity);
        }
        ContentProvider.setCurrentEntity(entity);
        showEntity(context);


    }

    @Override
    public void onNewEntity(Entity parent, EntityType type, EntityName name) {
        listener.onNewEntity(parent, type, name);
    }

    public void showEntity(Context context) {
        this.context = context;
        Log.v(TAG, "showEntity" + (context == null));
        //       if (view == null) {
        //  view = inflater.inflate(R.layout.entity_list, container, false);
        adapter = EntityListAdapter.getInstance(context, R.id.listView, ContentProvider.getChildEntities(), this);
        list = (ListView) view.findViewById(R.id.listView);
        list.setAdapter(adapter);
//        }
        TextView name = (TextView) view.findViewById(R.id.entity_name);
        name.setText(ContentProvider.currentEntity.getName().getValue());

        //   if (view != null) {

        // }
    }
}
