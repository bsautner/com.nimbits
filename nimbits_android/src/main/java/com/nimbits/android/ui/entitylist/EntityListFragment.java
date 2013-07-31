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
import com.nimbits.android.main.PointViewHelper;
import com.nimbits.android.main.async.LoadValueTask;
import com.nimbits.cloudplatform.Nimbits;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.simple.SimpleValue;
import com.nimbits.cloudplatform.client.model.value.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author: benjamin
 */
public class EntityListFragment extends Fragment implements EntityListener  {
    private final static  String TAG = "EntityListFragment";
    private Timer timer;
    private ListView list;
    private EntityListener listener;
    private View view;
    private Context context;
    private EntityListAdapter adapter;
    private final static int REFRESH_RATE = 5000;
    private static TimerTask updateTask;

    public EntityListFragment() {
    }

    public static EntityListFragment getInstance(Activity activity) {
        Log.v(TAG, "instance created");
        EntityListFragment instance = new EntityListFragment();
        instance.listener = (EntityListener) activity;
        instance.timer = new Timer();
        updateTask = new UpdateValuesTask(instance);
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
        Log.v(TAG, "onResume context::" + (context == null) + " view::" + (view==null) + " timer::" + (timer==null) );
        showEntity(context);
        if (this.timer != null) {
            Log.v(TAG, "onResume restarting timer");
            this.timer = new Timer();
            this.timer.scheduleAtFixedRate(updateTask, 0, REFRESH_RATE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause context::" + (context == null) + " view::" + (view==null) + " timer::" + (timer==null) );
        if (this.timer != null) {
            Log.v(TAG, "onPause stopping timer");
            this.timer.cancel();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop context::" + (context == null) + " view::" + (view==null) + " timer::" + (timer==null) );

        if (this.timer != null) {
            Log.v(TAG, "onStop stopping timer");
            this.timer.cancel();
        }
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

    @Override
    public void onValueUpdated(Entity entity, Value response) {
        adapter.setValues(ContentProvider.getChildEntities());
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onNewValue(Entity entity, String entry) {
        listener.onNewValue(entity, entry);
    }

    @Override
    public void newValuePrompt(Entity entity) {
        listener.newValuePrompt(entity);
    }

    public void showEntity(Context context) {
        this.context = context;
        Log.v(TAG, "showEntity" + (context == null));
        adapter = EntityListAdapter.getInstance(context, R.id.listView, ContentProvider.getChildEntities(), this);
        list = (ListView) view.findViewById(R.id.listView);
        list.setAdapter(adapter);
//        }
        TextView name = (TextView) view.findViewById(R.id.entity_name);
        name.setText(ContentProvider.currentEntity.getName().getValue());

        //   if (view != null) {

        // }
    }



    static class UpdateValuesTask extends TimerTask {
        EntityListener listener;

        UpdateValuesTask(EntityListener listener) {
            this.listener = listener;
        }



        public void run() {

            List<Entity> children = ContentProvider.getChildEntities();
            List<Entity> refresh = new ArrayList<Entity>(children.size() +1);
            if (ContentProvider.getCurrentEntity().getEntityType().equals(EntityType.point)) {
                refresh.add(ContentProvider.getCurrentEntity());
            }
            for (Entity e : children) {
                if (e.getEntityType().equals(EntityType.point)) {
                    refresh.add(e);
                }
            }

//            refresh.add(ContentProvider.getCurrentEntity());
            for (final Entity entity : refresh) {

                LoadValueTask.getInstance(new LoadValueTask.LoadValueTaskListener() {
                    @Override
                    public void onSuccess(Value response) {
                        Log.v(TAG, "Got Value" + response.getValueWithNote());
                        ContentProvider.updateCurrentValue(entity, response);
                        listener.onValueUpdated(entity, response);
                    }
                }).execute(entity);
            }



        }
    }




}
