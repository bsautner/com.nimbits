/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

/*
 * Simple connect server for phantom.js
 * Adapted from Modernizr
 */

var connect = require('connect')
  , http = require('http')
  , fs   = require('fs')
  , app = connect()
      .use(connect.static(__dirname + '/../../'));

http.createServer(app).listen(3000);

fs.writeFileSync(__dirname + '/pid.txt', process.pid, 'utf-8')