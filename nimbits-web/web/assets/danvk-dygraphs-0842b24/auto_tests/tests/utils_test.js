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
 * @fileoverview Tests for stand-alone functions in dygraph-utils.js
 *
 * @author danvdk@gmail.com (Dan Vanderkam)
 */

var UtilsTestCase = TestCase("utils-tests");

UtilsTestCase.prototype.testUpdate = function() {
  var a = {
    a: 1,
    b: [1, 2, 3],
    c: { x: 1, y: 2},
    d: { f: 10, g: 20}
  };
  assertEquals(1, a['a']);
  assertEquals([1, 2, 3], a['b']);
  assertEquals({x: 1, y: 2}, a['c']);
  assertEquals({f: 10, g: 20}, a['d']);

  Dygraph.update(a, { c: { x: 2 } });
  assertEquals({x: 2}, a['c']);

  Dygraph.update(a, { d: null });
  assertEquals(null, a['d']);

  Dygraph.update(a, { a: 10, b: [1, 2] });
  assertEquals(10, a['a']);
  assertEquals([1, 2], a['b']);
  assertEquals({x: 2}, a['c']);
  assertEquals(null, a['d']);
};

UtilsTestCase.prototype.testUpdateDeep = function() {
  var a = {
    a: 1,
    b: [1, 2, 3],
    c: { x: 1, y: 2},
    d: { f: 10, g: 20}
  };
  assertEquals(1, a['a']);
  assertEquals([1, 2, 3], a['b']);
  assertEquals({x: 1, y: 2}, a['c']);
  assertEquals({f: 10, g: 20}, a['d']);

  Dygraph.updateDeep(a, { c: { x: 2 } });
  assertEquals({x: 2, y: 2}, a['c']);

  Dygraph.updateDeep(a, { d: null });
  assertEquals(null, a['d']);

  Dygraph.updateDeep(a, { a: 10, b: [1, 2] });
  assertEquals(10, a['a']);
  assertEquals([1, 2], a['b']);
  assertEquals({x: 2, y: 2}, a['c']);
  assertEquals(null, a['d']);
};

UtilsTestCase.prototype.testUpdateDeepDecoupled = function() {
  var a = {
    a: 1,
    b: [1, 2, 3],
    c: { x: "original", y: 2},
  };

  var b = {};
  Dygraph.updateDeep(b, a);

  b.a = 2;
  assertEquals(1, a.a);

  b.b[0] = 2;
  assertEquals(1, a.b[0]);

  b.c.x = "new value";
  assertEquals("original", a.c.x);
};
