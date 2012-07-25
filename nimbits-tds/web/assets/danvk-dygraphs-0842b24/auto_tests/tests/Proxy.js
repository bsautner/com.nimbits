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

/**
 * @fileoverview A general purpose object proxy that logs all method calls.
 *
 * @author konigsberg@google.com (Robert Konigsberg)
 */

var Proxy = function(delegate) {
  this.delegate__ = delegate;
  this.calls__ = [];
  this.propertiesToTrack__ = [];

  for (var propname in delegate) {
    var type = typeof(delegate[propname]);

    // Functions are passed through to the delegate, and are logged
    // prior to the call.
    if (type == "function") {
      function makeFunc(name) {
        return function() {
          this.log__(name, arguments);
          this.delegate__[name].apply(this.delegate__, arguments);
        }
      };
      this[propname] = makeFunc(propname);
    } else if (type == "string" || type == "number") {
      // String and number properties are just passed through to the delegate.
      this.propertiesToTrack__.push(propname);
      function makeSetter(name) {
        return function(x) {
          this.delegate__[name] = x;
        }
      };
      this.__defineSetter__(propname, makeSetter(propname));

      function makeGetter(name) {
        return function() {
          return this.delegate__[name];
        }
      };
      this.__defineGetter__(propname, makeGetter(propname));
    }
  }
};

Proxy.prototype.log__ = function(name, args) {
  var properties = {};
  for (var propIdx in this.propertiesToTrack__) {
    var prop = this.propertiesToTrack__[propIdx];
    properties[prop] = this.delegate__[prop];
  }
  var call = { name : name, args : args, properties: properties };
  this.calls__.push(call);
};

