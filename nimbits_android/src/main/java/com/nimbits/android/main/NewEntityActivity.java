/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.android.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.nimbits.cloudplatform.Nimbits;
import com.nimbits.android.R;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.enums.ProtectionLevel;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModelFactory;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.android.settings.PointSettingsActivity;


/**
 * Author: Benjamin Sautner
 * Date: 1/8/13
 * Time: 1:36 PM
 */
public class NewEntityActivity extends Activity {
    private Entity parent;

    public void onCreate(Bundle savedInstanceState) {

        //TODO set protextion level to the parent

        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_entity_layout);
        Intent intent = getIntent();
        final String type = intent.getStringExtra("type");
        parent = (Entity) intent.getSerializableExtra("parent");

        final TextView parentLabel = (TextView) findViewById(R.id.parent_message);
        parentLabel.setText("New entity will be a child of " + parent.getName().getValue());


        final TextView descEntry = (TextView) findViewById(R.id.entity_desc);
        final TextView nameEntry = (TextView) findViewById(R.id.entity_name);
        nameEntry.setInputType(InputType.TYPE_CLASS_TEXT);
        descEntry.setInputType(InputType.TYPE_CLASS_TEXT);

        final EntityType entityType = EntityType.valueOf(type);
        final ImageButton button = (ImageButton) findViewById(R.id.save_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    EntityName entityName = CommonFactory.createName(String.valueOf(nameEntry.getText()), entityType);
                    Entity entity = EntityModelFactory.createEntity(entityName, String.valueOf(descEntry.getText()), entityType, ProtectionLevel.everyone, parent.getKey(),
                            Nimbits.session.getOwner());

                    switch (entityType) {

                        case user:
                            break;
                        case point:
                            Intent intent = new Intent(getApplicationContext(), PointSettingsActivity.class);
                            intent.putExtra(Parameters.entity.name(), entity);
                            startActivity(intent);
                            finish();
                        case category:
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


                } catch (Exception e) {
                    Log.e(this.getClass().getSimpleName(), e.getMessage());


                }


                setContentView(R.layout.progress);


            }
        });

    }

}