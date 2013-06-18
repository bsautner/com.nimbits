package com.nimbits.cloudplatform.main;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nimbits.cloudplatform.Nimbits;
import com.nimbits.cloudplatform.R;
import com.nimbits.cloudplatform.ToastHelper;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.simple.SimpleValue;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.main.async.LoadValueTask;

import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 12/28/12
 * Time: 2:19 PM
 */
public class EntityListAdapter extends ArrayAdapter<Entity> {

    private List<Entity> items;
    private Context context;
    private final EntityClickedListener entityClickedListener;
    private final ExpandListener expandListener;
    public EntityListAdapter(Context context, int textViewResourceId,
                             List<Entity> objects) {
        super(context, textViewResourceId, objects);
        this.items = objects;
        this.context = context;
        expandListener = (ExpandListener) context;
        entityClickedListener = (EntityClickedListener) context;
    }

    //;

    public interface ExpandListener {
        public void onEntityExpand(Entity entity);

    }



    public interface EntityClickedListener {
        public void onEntityClicked(Entity entity);

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

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (entity.getEntityType()) {

                    case user:
                        break;
                    case point:
                        entityClickedListener.onEntityClicked(entity);
                        break;
                    case category:
                        expandListener.onEntityExpand(entity);
                        break;
                    case subscription:
                        break;

                    case calculation:
                        break;

                    case summary:
                        break;

                    case accessKey:
                        break;
                }
            }
        });

        if (entity != null) {
            TextView text = (TextView) v.findViewById(R.id.entity_name);

            text.setText(entity.getName().getValue());


            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                       entityClickedListener.onEntityClicked(entity);
                    } catch (Exception e) {
                        ToastHelper.show(getContext(), e.getMessage());


                    }
                }
            });

            ImageView expand = (ImageView) v.findViewById(R.id.image_expand);
            expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expandListener.onEntityExpand(entity);
                }
            });

            expand.setVisibility(isParent(entity) ? View.VISIBLE : View.INVISIBLE);

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
                                PointViewHelper.setViews(response, currentValue, timestamp,  entityImage, SimpleValue.getEmptyInstance());
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
