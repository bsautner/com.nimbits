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
 * @fileoverview Test cases for drawing simple lines.
 *
 * @author konigsberg@google.com (Robert Konigsberg)
 */
var ZERO_TO_FIFTY = [[ 10, 0 ] , [ 20, 50 ]];

var SimpleDrawingTestCase = TestCase("simple-drawing");

var _origFunc = Dygraph.getContext;
SimpleDrawingTestCase.prototype.setUp = function() {
  document.body.innerHTML = "<div id='graph'></div>";
  Dygraph.getContext = function(canvas) {
    return new Proxy(_origFunc(canvas));
  }
};

SimpleDrawingTestCase.prototype.tearDown = function() {
  Dygraph.getContext = _origFunc;
};

SimpleDrawingTestCase.prototype.testDrawSimpleRangePlusOne = function() {
  var opts = {
    drawXGrid: false,
    drawYGrid: false,
    drawXAxis: false,
    drawYAxis: false,
    valueRange: [0,51] }

  var graph = document.getElementById("graph");
  var g = new Dygraph(graph, ZERO_TO_FIFTY, opts);
  htx = g.hidden_ctx_;

  CanvasAssertions.assertLineDrawn(htx, [0,320], [475,6.2745], {
    strokeStyle: "#008080",
    lineWidth: 1
  });
}
