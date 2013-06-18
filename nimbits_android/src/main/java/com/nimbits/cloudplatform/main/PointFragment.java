package com.nimbits.cloudplatform.main;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.nimbits.cloudplatform.R;
import com.nimbits.cloudplatform.client.model.entity.Entity;

/**
 * @Author: benjamin
 */
public class PointFragment extends Fragment {
    private  Entity  entity ;
    private final Context context;


    public PointFragment(Context context, Entity  entity ) {
        this.context = context;
        this.entity = entity;
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {



        final View view = inflater.inflate(R.layout.point, container, false);

        TextView name = (TextView) view.findViewById(R.id.entity_name);
        name.setText(entity.getName().getValue());


        return view;

    }


}
