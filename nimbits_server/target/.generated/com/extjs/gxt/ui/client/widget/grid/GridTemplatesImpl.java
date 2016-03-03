package com.extjs.gxt.ui.client.widget.grid;

import com.extjs.gxt.ui.client.core.Template;
import com.google.gwt.core.client.GWT;
import com.extjs.gxt.ui.client.core.MarkupBase;
import com.extjs.gxt.ui.client.core.TemplatesBase;
import com.google.gwt.user.client.Element;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.Templates;
import com.extjs.gxt.ui.client.core.Markup;
import com.extjs.gxt.ui.client.core.TemplatesCache;

public class GridTemplatesImpl extends com.extjs.gxt.ui.client.core.TemplatesBase implements com.extjs.gxt.ui.client.widget.grid.GridTemplates {
    public java.lang.String body(java.lang.String rows) {
      String cached = (String)TemplatesCache.INSTANCE.get("com.extjs.gxt.ui.client.widget.grid.GridTemplates#body",rows);if (cached != null) return cached;
      StringBuilder sb = new StringBuilder();
      {
        sb.append(rows);
      }
      String result = sb.toString();
      TemplatesCache.INSTANCE.put(result, "com.extjs.gxt.ui.client.widget.grid.GridTemplates#body",rows);return result;
    }
    public java.lang.String endGroup() {
      String cached = (String)TemplatesCache.INSTANCE.get("com.extjs.gxt.ui.client.widget.grid.GridTemplates#endGroup");if (cached != null) return cached;
      StringBuilder sb = new StringBuilder();
      {
        sb.append("</div></div>");
      }
      String result = sb.toString();
      TemplatesCache.INSTANCE.put(result, "com.extjs.gxt.ui.client.widget.grid.GridTemplates#endGroup");return result;
    }
    public java.lang.String master(java.lang.String body, java.lang.String header) {
      String cached = (String)TemplatesCache.INSTANCE.get("com.extjs.gxt.ui.client.widget.grid.GridTemplates#master",body,header);if (cached != null) return cached;
      StringBuilder sb = new StringBuilder();
      {
        sb.append("<div class=\"x-grid3\" role=\"presentation\"><div class=\"x-grid3-viewport\" role=\"presentation\"><div class=\"x-grid3-header\" role=\"presentation\"><div class=\"x-grid3-header-inner\" role=\"presentation\"><div class=\"x-grid3-header-offset\" role=\"presentation\">");
        sb.append(header);
        sb.append("</div></div><div class=\"x-clear\"></div></div><div class=\"x-grid3-scroller\" role=\"presentation\"><div class=\"x-grid3-body\" role=\"presentation\">");
        sb.append(body);
        sb.append("</div><a href=\"#\" class=\"x-grid3-focus\" tabIndex=\"-1\"></a></div></div><div class=\"x-grid3-resize-marker\">&#160;</div><div class=\"x-grid3-resize-proxy\">&#160;</div></div>");
      }
      String result = sb.toString();
      TemplatesCache.INSTANCE.put(result, "com.extjs.gxt.ui.client.widget.grid.GridTemplates#master",body,header);return result;
    }
    public java.lang.String startGroup(java.lang.String groupId, java.lang.String cls, java.lang.String style, java.lang.String group) {
      String cached = (String)TemplatesCache.INSTANCE.get("com.extjs.gxt.ui.client.widget.grid.GridTemplates#startGroup",groupId,cls,style,group);if (cached != null) return cached;
      StringBuilder sb = new StringBuilder();
      {
        sb.append("<div id=\"");
        sb.append(groupId);
        sb.append("\" class=\"x-grid-group ");
        sb.append(cls);
        sb.append("\"><div id=\"");
        sb.append(groupId);
        sb.append("-hd\" class=\"x-grid-group-hd\" style=\"");
        sb.append(style);
        sb.append("\" aria-level=\"1\" role=\"row\"><div class=\"x-grid-group-div\" role=\"presentation\">");
        sb.append(group);
        sb.append("</div></div><div id=\"");
        sb.append(groupId);
        sb.append("-bd\" class=\"x-grid-group-body\">");
      }
      String result = sb.toString();
      TemplatesCache.INSTANCE.put(result, "com.extjs.gxt.ui.client.widget.grid.GridTemplates#startGroup",groupId,cls,style,group);return result;
    }
}
