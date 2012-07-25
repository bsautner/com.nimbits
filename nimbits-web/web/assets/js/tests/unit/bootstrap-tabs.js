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

$(function () {

    module("bootstrap-tabs")

    test("should be defined on jquery object", function () {
        ok($(document.body).tabs, 'tabs method is defined')
    })

    test("should return element", function () {
        ok($(document.body).tabs()[0] == document.body, 'document.body returned')
    })

    test("should activate element by tab id", function () {
        var $tabsHTML = $('<ul class="tabs">'
            + '<li class="active"><a href="#home">Home</a></li>'
            + '<li><a href="#profile">Profile</a></li>'
            + '</ul>')


        $('<ul><li id="home"></li><li id="profile"></li></ul>').appendTo("#qunit-runoff")

        $tabsHTML.tabs().find('a').last().click()
        equals($("#qunit-runoff").find('.active').attr('id'), "profile")

        $tabsHTML.tabs().find('a').first().click()
        equals($("#qunit-runoff").find('.active').attr('id'), "home")

        $("#qunit-runoff").empty()
    })

    test("should activate element by pill id", function () {
        var $pillsHTML = $('<ul class="pills">'
            + '<li class="active"><a href="#home">Home</a></li>'
            + '<li><a href="#profile">Profile</a></li>'
            + '</ul>')


        $('<ul><li id="home"></li><li id="profile"></li></ul>').appendTo("#qunit-runoff")

        $pillsHTML.pills().find('a').last().click()
        equals($("#qunit-runoff").find('.active').attr('id'), "profile")

        $pillsHTML.pills().find('a').first().click()
        equals($("#qunit-runoff").find('.active').attr('id'), "home")

        $("#qunit-runoff").empty()
    })

    test("should trigger change event on activate", function () {
        var $tabsHTML = $('<ul class="tabs">'
            + '<li class="active"><a href="#home">Home</a></li>'
            + '<li><a href="#profile">Profile</a></li>'
            + '</ul>')
            , $target
            , count = 0
            , relatedTarget
            , target

        $tabsHTML
            .tabs()
            .bind("change", function (e) {
                target = e.target
                relatedTarget = e.relatedTarget
                count++
            })

        $target = $tabsHTML
            .find('a')
            .last()
            .click()

        equals(relatedTarget, $tabsHTML.find('a').first()[0])
        equals(target, $target[0])
        equals(count, 1)
    })

})