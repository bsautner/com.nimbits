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

JSDOC.PluginManager.registerPlugin(
	"JSDOC.commentSrcJson",
	{
		onDocCommentSrc: function(comment) {
			var json;
			if (/^\s*@json\b/.test(comment)) {
				comment.src = new String(comment.src).replace("@json", "");

				eval("json = "+comment.src);
				var tagged = "";
				for (var i in json) {
					var tag = json[i];
					// todo handle cases where tag is an object
					tagged += "@"+i+" "+tag+"\n";
				}
				comment.src = tagged;
			}
		}
	}
);