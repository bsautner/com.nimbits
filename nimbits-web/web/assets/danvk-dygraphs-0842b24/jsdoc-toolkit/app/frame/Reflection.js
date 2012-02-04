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

/**@constructor*/
function Reflection(obj) {
	this.obj = obj;
}

Reflection.prototype.getConstructorName = function() {
	if (this.obj.constructor.name) return this.obj.constructor.name;
	var src = this.obj.constructor.toSource();
	var name = src.substring(name.indexOf("function")+8, src.indexOf('(')).replace(/ /g,'');
	return name;
}

Reflection.prototype.getMethod = function(name) {
	for (var p in this.obj) {
		if (p == name && typeof(this.obj[p]) == "function") return this.obj[p];
	}
	return null;
}

Reflection.prototype.getParameterNames = function() {
	var src = this.obj.toSource();
	src = src.substring(
		src.indexOf("(", 8)+1, src.indexOf(")")
	);
	return src.split(/, ?/);
}
