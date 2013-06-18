/*
 * Copyright (c) 2010 Nimbits Inc.
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

package org.danvk;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * Methods for installing Dygraphs source in a GWT document.
 *
 * @author flooey@google.com (Adam Vartanian)
 */
public class Dygraphs {

  // Protected because the GWT compiler has to generate a subclass.
  protected interface Resources extends ClientBundle {
    @Source("org/danvk/dygraph-combined.js")
    TextResource dygraphs();
  }

  private static final Resources RESOURCES = GWT.create(Resources.class);
  private static boolean installed = false;

  /**
   * Install the Dygraphs JavaScript source into the current document.  This
   * method is idempotent.
   */
  public static synchronized void install() {
    if (!installed) {
      ScriptElement e = Document.get().createScriptElement();
      e.setText(RESOURCES.dygraphs().getText());
      Document.get().getBody().appendChild(e);
      installed = true;
    }
  }

  // Prevent construction
  private Dygraphs() { }

}
