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
function Article() {
}

Article.prototype = {
	/** @constructor */
	Title: function(title) {
		/** the value of the Title instance */
		this.title = title;
	},

	init: function(pages) {
		/** the value of the pages of the Article instance */
		this.pages = pages;
	}
}

f = new Article();
f.init("one two three");

t = new f.Title("my title");

print(f.pages);
print(t.title);