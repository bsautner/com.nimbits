
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
	Get the entire flavor.
	@name flavor^3
	@function
	@returns {Object} The entire flavor hash.
*/
/**
	Get a named flavor.
	@name flavor^2
	@function
	@param {String} name The name of the flavor to get.
	@returns {String} The value of that flavor.
*/
/**
	Set the flavor.
	@param {String} name The name of the flavor to set.
	@param {String} value The value of the flavor.
	@returns {String} The value of that flavor.
*/
function flavor(name, value) {
	if (arguments.length > 1) flavor[name] = value;
	else if (arguments.length == 1) return flavor[name];
	else return flavor;
}