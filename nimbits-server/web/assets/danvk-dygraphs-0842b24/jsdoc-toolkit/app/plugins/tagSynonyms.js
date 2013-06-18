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
	"JSDOC.tagSynonyms",
	{
		onDocCommentSrc: function(comment) {
			comment.src = comment.src.replace(/@methodOf\b/i, "@function\n@memberOf");
			comment.src = comment.src.replace(/@fieldOf\b/i, "@field\n@memberOf");
		},

		onDocCommentTags: function(comment) {
			for (var i = 0, l = comment.tags.length; i < l; i++) {
				var title = comment.tags[i].title.toLowerCase();
				var syn;
				if ((syn = JSDOC.tagSynonyms.synonyms["="+title])) {
					comment.tags[i].title = syn;
				}
			}
		}
	}
);

new Namespace(
	"JSDOC.tagSynonyms",
	function() {
		JSDOC.tagSynonyms.synonyms = {
			"=member":             "memberOf",
			"=memberof":           "memberOf",
			"=description":        "desc",
			"=exception":          "throws",
			"=argument":           "param",
			"=returns":            "return",
			"=classdescription":   "class",
			"=fileoverview":       "overview",
			"=extends":            "augments",
			"=base":               "augments",
			"=projectdescription": "overview",
			"=classdescription":   "class",
			"=link":               "see",
			"=borrows":            "inherits",
			"=scope":              "lends",
			"=construct":          "constructor"
		}
	}
);