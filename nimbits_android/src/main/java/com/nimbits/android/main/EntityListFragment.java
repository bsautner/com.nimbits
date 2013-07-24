package com.nimbits.android.main;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.nimbits.cloudplatform.R;
import com.nimbits.cloudplatform.client.model.entity.Entity;

import java.util.List;

/**
 * @Author: benjamin
 */
public class EntityListFragment extends Fragment {

    private List<Entity> entityList;
    private final Context context;
    ListView list;

    public EntityListFragment(Context context, List<Entity> entityList) {
        this.context = context;
        this.entityList = entityList;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {



        final View view = inflater.inflate(R.layout.entity_list, container, false);
        list = (ListView) view.findViewById(R.id.listView);
        list.setAdapter(new EntityListAdapter(context, R.id.listView, entityList));

      //  TextView name = (TextView) view.findViewById(R.id.entity_name);
       // name.setText("hello world");

        return view;

    }

    public void refresh(List<Entity> childEntities) {
        this.entityList = childEntities;

        list.setAdapter(new EntityListAdapter(context, R.id.listView, entityList));
    }
}
