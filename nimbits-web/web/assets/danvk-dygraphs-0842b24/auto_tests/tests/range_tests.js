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
 * @fileoverview Test valueRange and dateWindow changes.
 *
 * @author konigsberg@google.com (Robert Konigsberg)
 */
var ZERO_TO_FIFTY = [[ 10, 0 ] , [ 20, 50 ]];
var ZERO_TO_FIFTY_STEPS = function() {
  var a = [];
  var x = 10;
  var y = 0;
  var step = 0;
  for (step = 0; step <= 50; step++) {
    a.push([x + (step * .2), y + step]);
  }
  return a;
} ();

var RangeTestCase = TestCase("range-tests");

RangeTestCase.prototype.setUp = function() {
  document.body.innerHTML = "<div id='graph'></div>";
};

RangeTestCase.prototype.createGraph = function(opts) {
  var graph = document.getElementById("graph");
  var g = new Dygraph(graph, ZERO_TO_FIFTY_STEPS, opts);

  assertEquals([10, 20], g.xAxisRange());
  assertEquals([0, 55], g.yAxisRange(0));

  return g;
};

/**
 * Test that changes to valueRange and dateWindow are reflected
 * appropriately.
 */
RangeTestCase.prototype.testRangeSetOperations = function() {
  var g = this.createGraph({valueRange : [ 0, 55 ]});

  g.updateOptions({ dateWindow : [ 12, 18 ] });
  assertEquals([12, 18], g.xAxisRange());
  assertEquals([0, 55], g.yAxisRange(0));

  g.updateOptions({ valueRange : [ 10, 40 ] });
  assertEquals([12, 18], g.xAxisRange());
  assertEquals([10, 40], g.yAxisRange(0));

  g.updateOptions({  });
  assertEquals([12, 18], g.xAxisRange());
  assertEquals([10, 40], g.yAxisRange(0));

  g.updateOptions({ dateWindow : null, valueRange : null });
  assertEquals([10, 20], g.xAxisRange());
  assertEquals([0, 55], g.yAxisRange(0));
};

/**
 * Verify that when zoomed in by mouse operations, an empty call to
 * updateOptions doesn't change the displayed ranges.
 */
RangeTestCase.prototype.zoom = function(g, xRange, yRange) {
  var originalXRange = g.xAxisRange();
  var originalYRange = g.yAxisRange(0);

  DygraphOps.dispatchMouseDown(g, xRange[0], yRange[0]);
  DygraphOps.dispatchMouseMove(g, xRange[1], yRange[0]); // this is really necessary.
  DygraphOps.dispatchMouseUp(g, xRange[1], yRange[0]);

  assertEqualsDelta(xRange, g.xAxisRange(), 0.2);
  // assertEqualsDelta(originalYRange, g.yAxisRange(0), 0.2); // Not true, it's something in the middle.

  var midX = (xRange[1] - xRange[0]) / 2;
  DygraphOps.dispatchMouseDown(g, midX, yRange[0]);
  DygraphOps.dispatchMouseMove(g, midX, yRange[1]); // this is really necessary.
  DygraphOps.dispatchMouseUp(g, midX, yRange[1]);

  assertEqualsDelta(xRange, g.xAxisRange(), 0.2);
  assertEqualsDelta(yRange, g.yAxisRange(0), 0.2);
}


/**
 * Verify that when zoomed in by mouse operations, an empty call to
 * updateOptions doesn't change the displayed ranges.
 */
RangeTestCase.prototype.testEmptyUpdateOptions_doesntUnzoom = function() {
  var g = this.createGraph();
  this.zoom(g, [ 11, 18 ], [ 35, 40 ]);

  assertEqualsDelta([11, 18], g.xAxisRange(), 0.1);
  assertEqualsDelta([35, 40], g.yAxisRange(0), 0.2);

  g.updateOptions({});

  assertEqualsDelta([11, 18], g.xAxisRange(), 0.1);
  assertEqualsDelta([35, 40], g.yAxisRange(0), 0.2);
}

/**
 * Verify that when zoomed in by mouse operations, a call to
 * updateOptions({ dateWindow : null, valueRange : null }) fully
 * unzooms.
 */
RangeTestCase.prototype.testRestoreOriginalRanges_viaUpdateOptions = function() {
  var g = this.createGraph();
  this.zoom(g, [ 11, 18 ], [ 35, 40 ]);

  g.updateOptions({ dateWindow : null, valueRange : null });

  assertEquals([0, 55], g.yAxisRange(0));
  assertEquals([10, 20], g.xAxisRange());
}
