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
 * @fileoverview Assertions and other code used to test a canvas proxy.
 *
 * @author konigsberg@google.com (Robert Konigsberg)
 */

var CanvasAssertions = {};

/**
 * Assert that a line is drawn between the two points
 *
 * This merely looks for one of these four possibilities:
 * moveTo(p1) -> lineTo(p2)
 * moveTo(p2) -> lineTo(p1)
 * lineTo(p1) -> lineTo(p2)
 * lineTo(p2) -> lineTo(p1)
 *
 * attrs is meant to be used when you want to track things like
 * color and stroke width.
 */
CanvasAssertions.assertLineDrawn = function(proxy, p1, p2, attrs) {
  // found = 1 when prior loop found p1.
  // found = 2 when prior loop found p2.
  var priorFound = 0;
  for (var i = 0; i < proxy.calls__.length; i++) {
    var call = proxy.calls__[i];

    // This disables lineTo -> moveTo pairs.
    if (call.name == "moveTo" && priorFound > 0) {
      priorFound = 0;
    }

    var found = 0;
    if (call.name == "moveTo" || call.name == "lineTo") {
      var matchp1 = CanvasAssertions.matchPixels(p1, call.args);
      var matchp2 = CanvasAssertions.matchPixels(p2, call.args);
      if (matchp1 || matchp2) {
        if (priorFound == 1 && matchp2) {
// TODO -- add property test here  CanvasAssertions.matchAttributes(attrs, call.properties)
          return;
        }
        if (priorFound == 2 && matchp1) {
       // TODO -- add property test here  CanvasAssertions.matchAttributes(attrs, call.properties)
          return;
        }
        found = matchp1 ? 1 : 2;
      }
    }
    priorFound = found;
  }

  var toString = function(x) {
    var s = "{";
    for (var prop in x) {
      if (x.hasOwnProperty(prop)) {
        if (s.length > 1) {
          s = s + ", ";
        }
        s = s + prop + ": " + x[prop];
      }
    }
    return s + "}";
  };
  fail("Can't find a line drawn between " + p1 +
      " and " + p2 + " with attributes " + toString(attrs));
}

/**
 * Checks how many lines of the given color have been drawn.
 * @return {Integer} The number of lines of the given color.
 */
CanvasAssertions.numLinesDrawn = function(proxy, color) {
  var num_lines = 0;
  for (var i = 0; i < proxy.calls__.length; i++) {
    var call = proxy.calls__[i];
    if (call.name == "lineTo" && call.properties.strokeStyle == color) {
      num_lines++;
    }
  }
  return num_lines;
}

CanvasAssertions.matchPixels = function(expected, actual) {
  // Expect array of two integers. Assuming the values are within one
  // integer unit of each other. This should be tightened down by someone
  // who knows what pixel a value of 5.8888 results in.
  return Math.abs(expected[0] - actual[0]) < 1 &&
      Math.abs(expected[1] - actual[1]) < 1;
}

CanvasAssertions.matchAttributes = function(expected, actual) {
  for (var attr in expected) {
    if (expected.hasOwnProperty(attr) && expected[attr] != actual[attr]) {
      return false;
    }
  }
  return true;
}
