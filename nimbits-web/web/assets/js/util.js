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

var _gaq = _gaq || [];
_gaq.push(['_setAccount', 'UA-11739682-12']);
_gaq.push(['_trackPageview']);

(function () {
    var ga = document.createElement('script');
    ga.type = 'text/javascript';
    ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(ga, s);
})();


function loadPage(page) {
    var objFrame = document.getElementById("mainframe");
    objFrame.src = page;

}

function trackClick(_section) {

    _gaq.push(
        ['_setAccount', 'UA-11739682-12'],
        ['_setDomainName', 'nimbits.com'],
        ['_setCustomVar', 1, 'Section', _section, 3],
        ['_trackPageview']
    );


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


function popUp(url) {
    var width = 1200;
    var height = 800;
    var left = (screen.width - width) / 2;
    var top = (screen.height - height) / 2;
    var params = 'width=' + width + ', height=' + height;
    params += ', top=' + top + ', left=' + left;
    params += ', directories=no';
    params += ', location=yes';
    params += ', menubar=yes';
    params += ', resizable=yes';
    params += ', scrollbars=yes';
    params += ', status=yes';
    params += ', toolbar=no';
    newwin = window.open(url, 'windowname5', params);
    if (window.focus) {
        newwin.focus()
    }
    return false;
}
function smallPopUp(url) {
    var width = 300;
    var height = 200;
    var left = (screen.width - width) / 2;
    var top = (screen.height - height) / 2;
    var params = 'width=' + width + ', height=' + height;
    params += ', top=' + top + ', left=' + left;
    params += ', directories=no';
    params += ', location=no';
    params += ', menubar=no';
    params += ', resizable=no';
    params += ', scrollbars=no';
    params += ', status=no';
    params += ', toolbar=no';
    newwin = window.open(url, 'windowname5', params);
    if (window.focus) {
        newwin.focus()
    }
    return false;
}
var request;

function getHTTPObject() {
    var xhr = false;
    if (window.XMLHttpRequest) {
        xhr = new XMLHttpRequest();
    } else if (window.ActiveXObject) {
        try {
            xhr = new ActiveXObject("Msxml2.XMLHTTP");
        }
        catch (e) {
            try {
                xhr = new ActiveXObject("Microsoft.XMLHTTP");
            }
            catch (e) {
                xhr = false;
            }
        }
    }
    return xhr;
}
function getContent(page) {

    if (! page.sets)
    _gaq.push(['_trackEvent', page, 'load'])
    request = getHTTPObject();
    request.onreadystatechange = sendData;
    request.open("POST", page, true);
    request.send(null);

}

function sendData() {
    var dC = document.getElementById("content");
    if (request.readyState == 4) {
        dC.innerHTML = request.responseText;
        prettyPrint();
    }
    else if (request.readyState == 1) {
       dC.innerHTML = "Requesting content..."
    }
}

$(document).ready(function () {
    $("body").bind("click", function (e) {
        $('a.menu').parent("li").removeClass("open");
    });

    $("a.menu").click(function (e) {
        var $li = $(this).parent("li").toggleClass('open');
        return false;
    });
});


function loadRequestedPage() {
    var page = getParameterByName("content");

    if (page != "") {
        trackClick(page);
        loadPage(getContent('pages/' + page + '.html'));


    }


}