/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

var alertState = ["Low", "High", "Idle", "OK"];
var alertLabel = ["label label-notice", "label label-important", "label label-warning", "label label-success"];
var path = window.location.pathname.replace("report.html", "");
var server = "http://" + window.location.host + path;
var values;
var graph;

var uuid = getParameterByName("uuid");
var type = getParameterByName("type");
var count = getParameterByName("count");
var responseObject;
var hist = getParameterByName("hist");
var labels = [];
var pointNames = [];


if (count == "") {
    count = 25;
}

if (type == null) {
    type = "1";
}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
    var regexS = "[\\?&]" + name + "=([^&#]*)";
    var regex = new RegExp(regexS);
    var results = regex.exec(window.location.href);
    if (results == null)
        return "";
    else
        return decodeURIComponent(results[1].replace(/\+/g, " "));
}

$(document).ready(function () {

    getPoint();
});

$.ajaxSetup({
    error: function (xhr, status, error) {
        alert("An AJAX error occured: " + status + "\nError: " + error + "\nError detail: " + xhr.responseText);
    }
});
function set(_element, _val) {
    document.getElementById(_element).value = _val;

}


function getPoint() {

    $.get(server + "service/v2/entity",
        {uuid: uuid, type: type})
        .fail(function () {
            updateProgress("The server returned an error loading this data point, the point may no longer exist.")
        })
        .done(
        function (data) {

            var obj = eval(data);
            if (obj.unit == undefined) {
                obj.unit = "";
            }

            //document.getElementById("all").style.display = "block";

            document.getElementById("name").innerHTML = obj.name;
            document.getElementById("desc").innerHTML = obj.description;
            //document.getElementById("uuid").innerHTML = "<a href='report.html?uuid=" + uuid + "'>" + uuid + "</a>";

            document.getElementById("QR").src = "https://chart.googleapis.com/chart?chs=100x100&cht=qr&chl=" +
            server + "/report.html?uuid=" + uuid;

            if (type == "1") {
                getCurrentValue(obj);

            }

            getSeries();


        }
    );
}

function updateGraph(url) {


    if (graph == null) {
        graph = new Dygraph(document.getElementById("div_g"), url,
            {

                // legend: 'always',
                //showRangeSelector: true,
                connectSeparated: true,
                // customBars: true,
                animatedZooms: true,
                drawCallback: function (g) {
                    updateProgress("Done");
                    document.getElementById("update").disabled = false;
                }
            });
    }
    else {

        graph.updateOptions({'file': url});
    }

}

function getSeries() {
    updateProgress("Loading data please wait...");
    document.getElementById("update").disabled = true;
    var sd = new Date(document.getElementById("from").value).getTime();
    var ed = new Date(document.getElementById("to").value).getTime();

    var url = server + "service/v2/series?format=csv&uuid=" + uuid + "&type=" + type;

    if (!isNaN(sd) && !isNaN(ed) && sd < ed) {

        url += "&sd=" + sd + "&ed=" + ed;

    }
    updateGraph(url);

}

function getCurrentValue(point) {

    $.get(server + "service/v2/value",
        {uuid: uuid},
        function (data) {
            var value = eval(data);
            document.getElementById("status").innerHTML =
                "<span class='" + alertLabel[value.st] + "'>"
//                                + alertState[value.st] + ": "
            + value.d + point.unit + "  " + value.n
            + alertState[value.st]
            + "</span><br />";

            document.getElementById("dataText").value = value.dx;


        }
    ).fail(function () {
            updateProgress("The server returned an error loading the current value of this point")
        });
}

function updateProgress(text) {
    document.getElementById("progress").innerHTML = text;
}

function setStep() {

    graph.updateOptions({
        stepPlot: document.getElementById("stepOption").checked
    });
}

(function () {
    var po = document.createElement('script');
    po.type = 'text/javascript';
    po.async = true;
    po.src = 'https://apis.google.com/js/plusone.js';
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(po, s);
})();


$(function () {
    $(function () {
        $("#from").datepicker({
            showOtherMonths: true,
            selectOtherMonths: true,
            changeMonth: true,
            changeYear: true


        });
        $("#to").datepicker({
            showOtherMonths: true,
            selectOtherMonths: true,
            changeMonth: true,
            changeYear: true
        });
    });
});
