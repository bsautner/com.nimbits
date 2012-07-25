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
@constructor
*/
function Layout(p) {
	this.init = function(p) {
	}

	this.getId = function() {
	}

	/** @type Page */
	this.orientation = "landscape";
}

/**
@constructor
@augments Layout
*/
function Page() {
	this.reset = function(b) {
	}
}

/**
@extends Page
@constructor
*/
function ThreeColumnPage() {
	this.init = function(resetCode) {
	}
}
