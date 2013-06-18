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

/** @constructor */
function Zop() {
}

/**
 @class
*/
Foo = function(id) {
	// this is a bit twisted, but if you call Foo() you will then
	// modify Foo(). This is kinda, sorta non-insane, because you
	// would have to call Foo() 100% of the time to use Foo's methods
	Foo.prototype.methodOne = function(bar) {
	  alert(bar);
	};

	// same again
	Foo.prototype.methodTwo = function(bar2) {
	  alert(bar2);
	};

	// and these are only executed if the enclosing function is actually called
	// and who knows if that will ever happen?
	Bar = function(pez) {
	  alert(pez);
	};
	Zop.prototype.zap = function(p){
		alert(p);
	};

	// but this is only visible inside Foo
	function inner() {
	}
};
