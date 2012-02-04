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
   This is the main container for the FOODOC handler.
   @namespace
*/
FOODOC = {
};

/** The current version string of this application. */
FOODOC.VERSION = "1.0";

FOODOC.handle = function(srcFile, src) {
	LOG.inform("Handling file '" + srcFile + "'");

	return [
		new JSDOC.Symbol(
			"foo",
			[],
			"VIRTUAL",
			new JSDOC.DocComment("/** This is a foo. */")
		)
	];
};

FOODOC.publish = function(symbolgroup) {
	LOG.inform("Publishing symbolgroup.");
};
