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

package com.nimbits.client.panels;

import com.extjs.gxt.ui.client.widget.*;
import com.nimbits.client.model.category.*;
import com.nimbits.client.model.diagram.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.model.value.*;

import java.util.*;

public abstract class NavigationEventProvider extends LayoutContainer {
    private final List<CategoryClickedListener> categoryClickedListeners = new ArrayList<CategoryClickedListener>();
    private final List<CategoryDeletedListener> categoryDeletedListeners = new ArrayList<CategoryDeletedListener>();
    private final List<PointDeletedListener> pointDeletedListeners = new ArrayList<PointDeletedListener>();
    private final List<PointClickedListener> pointClickedListeners = new ArrayList<PointClickedListener>();
    private final List<DiagramClickedListener> diagramClickedListeners = new ArrayList<DiagramClickedListener>();
    private final List<UrlClickedListener> urlClickedListeners = new ArrayList<UrlClickedListener>();
    private final List<DiagramDeletedListener> diagramDeletedListeners = new ArrayList<DiagramDeletedListener>();
    private final List<ValueEnteredListener> valueEnteredListeners = new ArrayList<ValueEnteredListener>();
    private final List<ChartRemovedListener> chartRemovedListeners = new ArrayList<ChartRemovedListener>();
    private final List<DiagramRemovedListener> diagramRemovedListeners = new ArrayList<DiagramRemovedListener>();

    private final List<SubscriptionAddedListener> subscriptionAddedListeners = new ArrayList<SubscriptionAddedListener>();


    public interface SubscriptionAddedListener {
        void onSubscriptionAdded(final Subscription model);
    }

    public void addSubscriptionAddedListener(final SubscriptionAddedListener listener) {
        subscriptionAddedListeners.add(listener);
    }


    void notifySubscriptionAddedListener(final Subscription model)  {
        for (SubscriptionAddedListener SubscriptionAddedListener : subscriptionAddedListeners) {
            SubscriptionAddedListener.onSubscriptionAdded(model);
        }
    }


    // Category Click Handlers
    public interface CategoryClickedListener {
        void onCategoryClicked(final Category c, final boolean readOnly);
    }

    void addCategoryDeletedListeners(final CategoryDeletedListener listener) {
        categoryDeletedListeners.add(listener);
    }

    public void addCategoryClickedListeners(final CategoryClickedListener listener) {
        categoryClickedListeners.add(listener);
    }

    void notifyCategoryClickedListener(final Category c, final boolean readOnly)  {
        for (CategoryClickedListener categoryClickedListener : categoryClickedListeners) {
            categoryClickedListener.onCategoryClicked(c, readOnly);
        }
    }

    void notifyCategoryDeletedListener(final Category c, final boolean readOnly)  {
        for (CategoryDeletedListener categoryDeletedListener : categoryDeletedListeners) {
            categoryDeletedListener.onCategoryDeleted(c, readOnly);
        }
    }

    public interface CategoryDeletedListener {
        void onCategoryDeleted(final Category c, final boolean readOnly) ;

    }

    // ChartRemoved Click Handlers
    public interface ChartRemovedListener {
        void onChartRemovedClicked(final String chartName);
    }

    void addChartRemovedClickedListeners(final ChartRemovedListener listener) {
        chartRemovedListeners.add(listener);
    }

    void notifyChartRemovedListener(final String chartName) {
        for (ChartRemovedListener ChartRemovedClickedListener : chartRemovedListeners) {
            ChartRemovedClickedListener.onChartRemovedClicked(chartName);
        }
    }

    // Value entered Handlers
    public interface ValueEnteredListener {
        void onValueEntered(final Point point, final Value value);

    }

    void addValueEnteredListeners(final ValueEnteredListener listener) {
        valueEnteredListeners.add(listener);
    }

    void notifyValueEnteredListener(final Point point, final Value value) {
        for (final ValueEnteredListener valueEnteredListener : valueEnteredListeners) {
            valueEnteredListener.onValueEntered(point, value);
        }
    }

    // Point Click Handlers
    public interface PointClickedListener {
        void onPointClicked(final Point c);

    }

    public void addPointClickedListeners(final PointClickedListener listener) {
        pointClickedListeners.add(listener);
    }

    void notifyPointClickedListener(final Point c)  {

        for (PointClickedListener pointClickedListener : pointClickedListeners) {
            pointClickedListener.onPointClicked(c);
        }
    }

    // Diagram Click Handlers
    public interface DiagramClickedListener {
        void onDiagramClicked(final Diagram d);

    }

    public void addDiagramClickedListeners(final DiagramClickedListener listener) {
        diagramClickedListeners.add(listener);
    }

    void notifyDiagramClickedListener(final Diagram d) {
        for (DiagramClickedListener diagramClickedListener : diagramClickedListeners) {
            diagramClickedListener.onDiagramClicked(d);
        }
    }

    // Diagram Click Handlers
    public interface UrlClickedListener {
        void onUrlClicked(final String url, final String target);

    }

    public void addUrlClickedListeners(final UrlClickedListener listener) {
        urlClickedListeners.add(listener);
    }

    void notifyUrlClickedListener(final String url, final String target) {
        for (UrlClickedListener urlClickedListener : urlClickedListeners) {
            urlClickedListener.onUrlClicked(url, target);
        }
    }

    public interface DiagramDeletedListener {
        void onDiagramDeleted(final Diagram c, final boolean readOnly);

    }

    void addDiagramDeletedListeners(final DiagramDeletedListener listener) {
        diagramDeletedListeners.add(listener);
    }

    void notifyDiagramDeletedListener(final Diagram c, final boolean readOnly) {
        for (DiagramDeletedListener diagramDeletedListener : diagramDeletedListeners) {
            diagramDeletedListener.onDiagramDeleted(c, readOnly);
        }
    }

    public interface DiagramRemovedListener {
        void onDiagramRemovedClicked(final Diagram diagram);
    }

    void addDiagramRemovedClickedListeners(final DiagramRemovedListener listener) {
        diagramRemovedListeners.add(listener);
    }

    void notifyDiagramRemovedListener(final Diagram diagram) {
        for (DiagramRemovedListener diagramRemovedClickedListener : diagramRemovedListeners) {
            diagramRemovedClickedListener.onDiagramRemovedClicked(diagram);
        }
    }


    public interface PointDeletedListener {
        void onPointDeleted(Point c);

    }

    void addPointDeletedListeners(PointDeletedListener listener) {
        pointDeletedListeners.add(listener);
    }

    void notifyPointDeletedListener(Point c)  {
        for (PointDeletedListener pointDeletedListener : pointDeletedListeners) {
            pointDeletedListener.onPointDeleted(c);
        }
    }


}
