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
 * @fileoverview Test cases that ensure Dygraphs works at all.
 *
 * @author konigsberg@google.com (Robert Konigsberg)
 */
var DEAD_SIMPLE_DATA = [[ 10, 2100 ]];
var ZERO_TO_FIFTY = [[ 10, 0 ] , [ 20, 50 ]];

var SanityTestCase = TestCase("dygraphs-sanity");

SanityTestCase.prototype.setUp = function() {
  document.body.innerHTML = "<div id='graph'></div>";
};

/**
 * The sanity test of sanity tests.
 */
SanityTestCase.prototype.testTrue = function() {
  assertTrue(true);
};

/**
 * Sanity test that ensures the graph element exists.
 */
SanityTestCase.prototype.testGraphExists = function() {
  var graph = document.getElementById("graph");
  assertNotNull(graph);
};

// TODO(konigsberg): Move the following tests to a new package that
// tests all kinds of toDomCoords, toDataCoords, toPercent, et cetera.

/**
 * A sanity test of sorts, by ensuring the dygraph is created, and
 * isn't just some piece of junk object.
 */
SanityTestCase.prototype.testToString = function() {
  var graph = document.getElementById("graph");
  var g = new Dygraph(graph, DEAD_SIMPLE_DATA, {});
  assertNotNull(g);
  assertEquals("[Dygraph graph]", g.toString());
};

/**
 * Test that when no valueRange is specified, the y axis range is
 * adjusted by 10% on top.
 */
SanityTestCase.prototype.testYAxisRange_default = function() {
  var graph = document.getElementById("graph");
  assertEquals(0, graph.style.length);
  var g = new Dygraph(graph, ZERO_TO_FIFTY, {});
  assertEquals([0, 55], g.yAxisRange(0));
};

/**
 * Test that valueRange matches the y-axis range specifically.
 */
SanityTestCase.prototype.testYAxisRange_custom = function() {
  var graph = document.getElementById("graph");
  var g = new Dygraph(graph, ZERO_TO_FIFTY, { valueRange: [0,50] });
  assertEquals([0, 50], g.yAxisRange(0));
};

/**
 * Test that valueRange matches the y-axis range specifically.
 *
 * This is based on the assumption that 20 pixels are dedicated to the
 * axis label and tick marks.
 * TODO(konigsberg): change yAxisLabelWidth to 0 (or 20) and try again.
 */
SanityTestCase.prototype.testToDomYCoord = function() {
  var graph = document.getElementById("graph");
  var g = new Dygraph(graph, ZERO_TO_FIFTY, { height: 70, valueRange: [0,50] });

  assertEquals(50, g.toDomYCoord(0));
  assertEquals(0, g.toDomYCoord(50));

  for (var x = 0; x <= 50; x++) {
    assertEqualsDelta(50 - x, g.toDomYCoord(x), 0.00001);
  }
};
