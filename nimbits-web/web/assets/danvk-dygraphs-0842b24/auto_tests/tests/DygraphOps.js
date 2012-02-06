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


/**
 * @fileoverview Utility functions for Dygraphs.
 *
 * @author konigsberg@google.com (Robert Konigsberg)
 */
var DygraphOps = {};

DygraphOps.defaultEvent_ = {
  type : '',
  canBubble : true,
  cancelable : true,
  view : document.defaultView,
  detail : 0,
  screenX : 0,
  screenY : 0,
  clientX : 0,
  clientY : 0,
  ctrlKey : false,
  altKey : false,
  shiftKey : false,
  metaKey : false,
  button : 0,
  relatedTarget : null
};

/**
 * Create an event. Sets default event values except for special ones
 * overridden by the 'custom' parameter.
 *
 * @param command the command to create.
 * @param custom an associative array of event attributes and their new values.
 */
DygraphOps.createEvent = function(command, custom) {

  var copy = function(from, to) {
    if (from != null) {
      for (var prop in from) {
        if(from.hasOwnProperty(prop)) {
          to[prop] = from[prop];
        }
      }
    }
  }

  var e = {};
  copy(DygraphOps.defaultEvent_, e);
  copy(command, e);
  copy(custom, e);

  var event = document.createEvent('MouseEvents');
  event.initMouseEvent(
    e.type,
    e.canBubble,
    e.cancelable,
    e.view,
    e.detail,
    e.screenX,
    e.screenY,
    e.clientX,
    e.clientY,
    e.ctrlKey,
    e.altKey,
    e.shiftKey,
    e.metaKey,
    e.button,
    e.relatedTarget);
  return event;
}

/**
 * Dispatch an event onto the graph's canvas.
 */
DygraphOps.dispatchCanvasEvent = function(g, event) {
  g.canvas_.dispatchEvent(event);
}

DygraphOps.dispatchDoubleClick = function(g, custom) {
  var opts = {
    type : 'dblclick',
    detail : 2
  };
  var event = DygraphOps.createEvent(opts, custom);
  DygraphOps.dispatchCanvasEvent(g, event);
};

DygraphOps.dispatchMouseDown_Point = function(g, x, y, custom) {
  var pageX = Dygraph.findPosX(g.canvas_) + x;
  var pageY = Dygraph.findPosY(g.canvas_) + y;

  var opts = {
    type : 'mousedown',
    detail : 1,
    screenX : pageX,
    screenY : pageY,
    clientX : pageX,
    clientY : pageY,
  };

  var event = DygraphOps.createEvent(opts, custom);
  DygraphOps.dispatchCanvasEvent(g, event);
}

DygraphOps.dispatchMouseMove_Point = function(g, x, y, custom) {
  var pageX = Dygraph.findPosX(g.canvas_) + x;
  var pageY = Dygraph.findPosY(g.canvas_) + y;

  var opts = {
    type : 'mousemove',
    screenX : pageX,
    screenY : pageY,
    clientX : pageX,
    clientY : pageY,
  };

  var event = DygraphOps.createEvent(opts, custom);
  DygraphOps.dispatchCanvasEvent(g, event);
};

DygraphOps.dispatchMouseUp_Point = function(g, x, y, custom) {
  var pageX = Dygraph.findPosX(g.canvas_) + x;
  var pageY = Dygraph.findPosY(g.canvas_) + y;

  var opts = {
    type : 'mouseup',
    screenX : pageX,
    screenY : pageY,
    clientX : pageX,
    clientY : pageY,
  };

  var event = DygraphOps.createEvent(opts, custom);
  DygraphOps.dispatchCanvasEvent(g, event);
};

/**
 * Dispatches a mouse down using the graph's data coordinate system.
 * (The y value mapped to the first axis.)
 */
DygraphOps.dispatchMouseDown = function(g, x, y, custom) {
  DygraphOps.dispatchMouseDown_Point(
      g,
      g.toDomXCoord(x),
      g.toDomYCoord(y),
      custom);
};

/**
 * Dispatches a mouse move using the graph's data coordinate system.
 * (The y value mapped to the first axis.)
 */
DygraphOps.dispatchMouseMove = function(g, x, y, custom) {
  DygraphOps.dispatchMouseMove_Point(
      g,
      g.toDomXCoord(x),
      g.toDomYCoord(y),
      custom);
};

/**
 * Dispatches a mouse up using the graph's data coordinate system.
 * (The y value mapped to the first axis.)
 */
DygraphOps.dispatchMouseUp = function(g, x, y, custom) {
  DygraphOps.dispatchMouseUp_Point(
      g,
      g.toDomXCoord(x),
      g.toDomYCoord(y),
      custom);
};

