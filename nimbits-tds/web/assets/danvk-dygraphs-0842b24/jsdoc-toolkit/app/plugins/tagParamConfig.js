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

JSDOC.PluginManager.registerPlugin(
	"JSDOC.tagParamConfig",
	{
		onDocCommentTags: function(comment) {
			var currentParam = null;
			var tags = comment.tags;
			for (var i = 0, l = tags.length; i < l; i++) {

				if (tags[i].title == "param") {
					if (tags[i].name.indexOf(".") == -1) {
						currentParam = i;
					}
				}
				else if (tags[i].title == "config") {
					tags[i].title = "param";
					if (currentParam == null) {
						tags[i].name = "arguments"+"."+tags[i].name;
					}
					else if (tags[i].name.indexOf(tags[currentParam].name+".") != 0) {
						tags[i].name = tags[currentParam].name+"."+tags[i].name;
					}
					currentParam != null
					//tags[currentParam].properties.push(tags[i]);
				}
				else {
					currentParam = null;
				}
			}
		}
	}
);
