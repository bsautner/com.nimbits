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
 * @fileoverview Tests for rolling averages.
 *
 * @author danvk@google.com (Dan Vanderkam)
 */
var rollingAverageTestCase = TestCase("rolling-average");

rollingAverageTestCase.prototype.setUp = function() {
  document.body.innerHTML = "<div id='graph'></div>";
};

rollingAverageTestCase.prototype.tearDown = function() {
};

rollingAverageTestCase.prototype.getLegend = function() {
  return document.getElementsByClassName("dygraph-legend")[0].textContent;
};

rollingAverageTestCase.prototype.testRollingAverage = function() {
  var opts = {
    width: 480,
    height: 320,
    rollPeriod: 1,
    showRoller: true
  };
  var data = "X,Y\n" +
      "0,0\n" +
      "1,1\n" +
      "2,2\n" +
      "3,3\n"
  ;

  var graph = document.getElementById("graph");
  var g = new Dygraph(graph, data, opts);

  g.setSelection(0); assertEquals("0: Y:0", this.getLegend());
  g.setSelection(1); assertEquals("1: Y:1", this.getLegend());
  g.setSelection(2); assertEquals("2: Y:2", this.getLegend());
  g.setSelection(3); assertEquals("3: Y:3", this.getLegend());
  assertEquals(1, g.rollPeriod());

  g.updateOptions({rollPeriod: 2});
  g.setSelection(0); assertEquals("0: Y:0", this.getLegend());
  g.setSelection(1); assertEquals("1: Y:0.5", this.getLegend());
  g.setSelection(2); assertEquals("2: Y:1.5", this.getLegend());
  g.setSelection(3); assertEquals("3: Y:2.5", this.getLegend());
  assertEquals(2, g.rollPeriod());

  g.updateOptions({rollPeriod: 3});
  g.setSelection(0); assertEquals("0: Y:0", this.getLegend());
  g.setSelection(1); assertEquals("1: Y:0.5", this.getLegend());
  g.setSelection(2); assertEquals("2: Y:1", this.getLegend());
  g.setSelection(3); assertEquals("3: Y:2", this.getLegend());
  assertEquals(3, g.rollPeriod());

  g.updateOptions({rollPeriod: 4});
  g.setSelection(0); assertEquals("0: Y:0", this.getLegend());
  g.setSelection(1); assertEquals("1: Y:0.5", this.getLegend());
  g.setSelection(2); assertEquals("2: Y:1", this.getLegend());
  g.setSelection(3); assertEquals("3: Y:1.5", this.getLegend());
  assertEquals(4, g.rollPeriod());
};

rollingAverageTestCase.prototype.testRollBoxDoesntDisapper = function() {
  var opts = {
    showRoller: true
  };
  var data = "X,Y\n" +
      "0,0\n" +
      "1,1\n" +
      "2,2\n" +
      "3,3\n"
  ;

  var graph = document.getElementById("graph");
  var g = new Dygraph(graph, data, opts);

  var roll_box = graph.getElementsByTagName("input");
  assertEquals(1, roll_box.length);
  assertEquals("1", roll_box[0].value);

  graph.style.width = "500px";
  g.resize();
  assertEquals(1, roll_box.length);
  assertEquals("1", roll_box[0].value);
};

