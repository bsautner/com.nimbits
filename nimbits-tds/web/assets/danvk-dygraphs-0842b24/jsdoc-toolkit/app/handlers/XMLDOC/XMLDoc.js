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

LOG.inform("XMLDOC.symbolize loaded");

/**
 * Convert the source file to a set of symbols
 */
XMLDOC.symbolize = function(srcFile, src) {

   LOG.inform("Symbolizing file '" + srcFile + "'");

   // XML files already have a defined structure, so we don't need to
   // do anything but parse them.  The DOM reader can create a symbol
   // table from the parsed XML.
   var dr = new XMLDOC.DomReader(XMLDOC.Parser.parse(src));
   return dr.getSymbols(srcFile);

};
