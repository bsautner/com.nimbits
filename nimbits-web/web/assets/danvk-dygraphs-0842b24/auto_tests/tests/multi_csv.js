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
 * @fileoverview Test cases for how axis labels are chosen and formatted.
 *
 * @author dan@dygraphs.com (Dan Vanderkam)
 */
var MultiCsvTestCase = TestCase("multi-csv");

MultiCsvTestCase.prototype.setUp = function() {
  document.body.innerHTML = "<div id='graph'></div>";
};

MultiCsvTestCase.prototype.tearDown = function() {
};

function getXLabels() {
  var x_labels = document.getElementsByClassName("dygraph-axis-label-x");
  var ary = [];
  for (var i = 0; i < x_labels.length; i++) {
    ary.push(x_labels[i].innerHTML);
  }
  return ary;
}

MultiCsvTestCase.prototype.testOneCSV = function() {
  var opts = {
    width: 480,
    height: 320
  };
  var data = "X,Y\n" +
      "0,-1\n" +
      "1,0\n" +
      "2,1\n" +
      "3,0\n"
  ;

  var graph = document.getElementById("graph");
  var g = new Dygraph(graph, data, opts);

  assertEquals(['0','0.5','1','1.5','2','2.5'], getXLabels());
};

MultiCsvTestCase.prototype.testTwoCSV = function() {
  var opts = {
    width: 480,
    height: 320
  };
  var data = "X,Y\n" +
      "0,-1\n" +
      "1,0\n" +
      "2,1\n" +
      "3,0\n"
  ;

  var graph = document.getElementById("graph");
  var g = new Dygraph(graph, data, opts);

  assertEquals(['0','0.5','1','1.5','2','2.5'], getXLabels());

  g.updateOptions({file: data});

  assertEquals(['0','0.5','1','1.5','2','2.5'], getXLabels());
};
