package com.nimbits.android.ui.entitylist;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nimbits.android.main.PointViewHelper;
import com.nimbits.cloudplatform.Nimbits;
import com.nimbits.android.R;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.simple.SimpleValue;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.android.main.async.LoadValueTask;

import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 12/28/12
 * Time: 2:19 PM
 */
public class EntityListAdapter extends ArrayAdapter<Entity> {

    private List<Entity> items;
    private EntityListener entityClickedListener;
    private Context context;
    public EntityListAdapter(Context context, int textViewResourceId, List<Entity> objects) {
        super(context, textViewResourceId, objects);
    }

    public final static EntityListAdapter getInstance(Context context, int textViewResourceId,
                             List<Entity> objects, EntityListener entityClickedListener) {
        EntityListAdapter instance = new EntityListAdapter(context, textViewResourceId, objects);
        instance.context = context;
        instance.items = objects;
        instance.entityClickedListener = entityClickedListener;
        return instance;
    }






    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;


        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.entity_list_item, null);
        }



        Resources res = getContext().getResources();
        final Entity entity = items.get(position);

        if (v != null) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    entityClickedListener.onEntityClicked(entity);


                }
            });
        }

        if (entity != null && v != null) {
            TextView text = (TextView) v.findViewById(R.id.entity_name);

            text.setText(entity.getName().getValue());





            final TextView currentValue = (TextView) v.findViewById((R.id.value));
          //  final TextView note = (TextView) v.findViewById((R.id.note));
            final TextView timestamp = (TextView) v.findViewById((R.id.timestamp));
            currentValue.setVisibility(View.GONE);
            timestamp.setVisibility(View.GONE);
            final ImageView entityImage = (ImageView) v.findViewById(R.id.entity_image);
            switch (entity.getEntityType()) {

                case calculation:

                    entityImage.setImageDrawable(res.getDrawable(R.drawable.calc));
                    break;
                case category:
                    entityImage.setImageDrawable(res.getDrawable(R.drawable.folder));
                    break;
                case user:
                    break;
                case point:
                    // entityImage.setImageDrawable(res.getDrawable(R.drawable.led_off));
                    currentValue.setVisibility(View.VISIBLE);
                    timestamp.setVisibility(View.VISIBLE);
                    if (entity.getParent().equals(Nimbits.session.getKey())) {

                        LoadValueTask.getInstance(new LoadValueTask.LoadValueTaskListener() {
                            @Override
                            public void onSuccess(Value response) {
                                PointViewHelper.setViews(response, currentValue, timestamp, entityImage, SimpleValue.getEmptyInstance());
                            }
                        }).execute(entity, context);
                    }
                case subscription:
                    break;


                case summary:
                    entityImage.setImageDrawable(res.getDrawable(R.drawable.expand));

                case accessKey:
                    entityImage.setImageDrawable(res.getDrawable(R.drawable.expand));
            }
        }


        return v;
    }

    private boolean isParent(Entity entity) {
        List<Entity> tree = Nimbits.tree;
        boolean isParent = false;
        for (Entity child : tree) {
            if (child.getParent().equals(entity.getKey())) {
                isParent = true;
                break;
            }


        }
        return isParent;
    }

}
