<!DOCTYPE html>

<%@ page import="com.nimbits.client.constants.Const" %><%


    response.setContentType(Const.CONTENT_TYPE_HTML);
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Access-Control-Allow-Origin", "*");
%>

<html>
<head>
    <title>All Points, Everywhere</title>

    <meta charset="UTF-8">
    <style type="text/css">
        html, body, #map_canvas {
            margin: 0;
            padding: 0;
            height: 100%;
        }
    </style>
    <script type="text/javascript"  src="http://maps.googleapis.com/maps/api/js?key=AIzaSyB6gLOFuTMJU2cERQk-NTLkBLXi8gXIpzo&sensor=false"></script>
    <script type="text/javascript">





        var map;
        ${TEXT}
        var markers = [];
        var iterator = 0;
        function initialize() {
            var myOptions = {
                zoom: 1,
                center: new google.maps.LatLng(39.964491,-75.163754),
                mapTypeId: google.maps.MapTypeId.ROADMAP
            };
            map = new google.maps.Map(document.getElementById('map_canvas'),
                    myOptions);
            drop();

        }

        google.maps.event.addDomListener(window, 'load', initialize);

        function drop() {
            for (var i = 0; i < neighborhoods.length; i++) {
                addMarker(uuids[iterator], desc[iterator]);

//                setTimeout(function() {
//                    addMarker(uuids[iterator], desc[iterator]);
//                }, i * 100);
            }
        }

        function addMarker(u, d) {
            var marker =  new google.maps.Marker({
                position: neighborhoods[iterator],
                map: map,
                draggable: false,
                animation: google.maps.Animation.DROP,
                title: d
            });
            markers.push(marker);

            google.maps.event.addListener(marker, 'click', function(e) {
                popUp(u);
            });
            iterator++;
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
    </script>
</head>
<body>
<div id="map_canvas"></div>
</body>
</html>

<%--${TEXT}--%>