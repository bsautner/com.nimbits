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

var url = "/service/auth";
var seriesService = "/service/series";
var pointService = "/service/point";

var pointCurrentValue = "/service/currentvalue";
var invocation = new XMLHttpRequest();
function getcurrentValue(_callback, _base, _uuid) {


    var params = "uuid=" + _uuid + "&format=json";

    var g = _base + pointCurrentValue + "?" + params;

    invocation.open('GET', g, true);

    invocation.onreadystatechange = handler;
    invocation.send(null);

    function handler() {
        if (invocation.readyState == 4 && invocation.status == 200) {


            var value = (eval('(' + invocation.responseText + ')'));

            _callback(value);

        }


    }
}

function getPoint(_callback, _base, _uuid, _count, _start, _end) {


    var params = "uuid=" + _uuid + "&format=json";
    if (_count != null) {
        params += "&count=" + _count;
    }
    else if (_start != null && _end != null) {
        params += "&sd=" + _start + "&ed=" + _end;
    }

    var g = _base + pointService + "?" + params;

    invocation.open('GET', g, true);

    invocation.onreadystatechange = pointHandler;
    invocation.send(null);

    function pointHandler() {
        if (invocation.readyState == 4 && invocation.status == 200) {


            var point = (eval('(' + invocation.responseText + ')'));
            var current = (eval('(' + point.value + ')'));
            var values = (eval(point.values));
            point.value = current;
            point.values = values;
            _callback(point);

        }


    }

}


