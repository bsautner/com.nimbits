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
 * @fileoverview Mocked-out jstestdriver api that lets me test locally.
 *
 * @author konigsberg@google.com (Robert Konigsberg)
 */
var jstestdriver = {
  jQuery : jQuery
};

var jstd = {
  include : function(name) {
    this.sucker("Not including " + name);
  },
  sucker : function(text) {
    console.log(text + ", sucker!");
  }
};

var testCaseList = [];

function TestCase(name) {
  this.name = name;
  this.toString = function() {
    return "Fake test case " + name;
  };

  var testCase = function() { return this; };
  testCase.prototype.setUp = function() { };
  testCase.prototype.tearDown = function() { };
  /**
   * name can be a string, which is looked up in this object, or it can be a
   * function, in which case it's run.
   *
   * Examples:
   * var tc = new MyTestCase();
   * tc.runTest("testThis");
   * tc.runTest(tc.testThis);
   *
   * The duplication tc in runTest is irritating, but it plays well with
   * Chrome's console completion.
   */
  testCase.prototype.runTest = function(func) {
    try {
      this.setUp();

      var fn = null;
      var parameterType = typeof(func);
      if (typeof(func) == "function") {
        fn = func;
      } else if (typeof(func) == "string") {
        fn = this[func];
      } else {
        fail("can't supply " + typeof(func) + " to runTest");
      }

      fn.apply(this, []);
      this.tearDown();
      return true;
    } catch (e) {
      console.log(e);
      if (e.stack) {
        console.log(e.stack);
      }
      return false;
    }
  };
  testCase.prototype.runAllTests = function() {
    // what's better than for ... in for non-array objects?
    var tests = {};
    for (var name in this) {
      if (name.indexOf('test') == 0 && typeof(this[name]) == 'function') {
        console.log("Running " + name);
        var result = this.runTest(name);
        tests[name] = result;
      }
    }
    console.log(prettyPrintEntity_(tests));
  };

  testCaseList.push(testCase);
  return testCase;
};

// Note: this creates a bunch of global variables intentionally.
function addGlobalTestSymbols() {
  globalTestDb = {};  // maps test name -> test function wrapper

  var num_tests = 0;
  for (var i = 0; i < testCaseList.length; i++) {
    var tc_class = testCaseList[i];
    for (var name in tc_class.prototype) {
      if (name.indexOf('test') == 0 && typeof(tc_class.prototype[name]) == 'function') {
        if (globalTestDb.hasOwnProperty(name)) {
          console.log('Duplicated test name: ' + name);
        } else {
          globalTestDb[name] = function(name, tc_class) {
            return function() {
              var tc = new tc_class;
              return tc.runTest(name);
            };
          }(name, tc_class);
          eval(name + " = globalTestDb['" + name + "'];");
          num_tests += 1;
        }
      }
    }
  }
  console.log('Loaded ' + num_tests + ' tests in ' +
              testCaseList.length + ' test cases');
}
