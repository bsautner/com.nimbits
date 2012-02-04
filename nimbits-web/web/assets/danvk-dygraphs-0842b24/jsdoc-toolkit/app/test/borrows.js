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
@constructor
*/
function Layout(p) {
	/** initilize 1 */
	this.init = function(p) {
	}

	/** get the id */
	this.getId = function() {
	}

	/** @type string */
	this.orientation = "landscape";

	function getInnerElements(elementSecretId){
	}
}

/** A static method. */
Layout.units = function() {
}

/**
@constructor
@borrows Layout#orientation
@borrows Layout-getInnerElements
@borrows Layout.units
*/
function Page() {
	/** reset the page */
	this.reset = function(b) {
	}
}

/**
@constructor
@borrows Layout.prototype.orientation as this.orientation
@borrows Layout.prototype.init as #init
@inherits Page.prototype.reset as #reset
*/
function ThreeColumnPage() {
	/** initilize 2 */
	this.init = function(p) {
	}
}
