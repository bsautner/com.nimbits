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

    module("bootstrap-twipsy")

    test("should be defined on jquery object", function () {
        var div = $("<div></div>")
        ok(div.twipsy, 'popover method is defined')
    })

    test("should return element", function () {
        var div = $("<div></div>")
        ok(div.twipsy() == div, 'document.body returned')
    })

    test("should expose default settings", function () {
        ok(!!$.fn.twipsy.defaults, 'defaults is defined')
    })

    test("should remove title attribute", function () {
        var twipsy = $('<a href="#" rel="twipsy" title="Another twipsy"></a>').twipsy()
        ok(!twipsy.attr('title'), 'title tag was removed')
    })

    test("should add data attribute for referencing original title", function () {
        var twipsy = $('<a href="#" rel="twipsy" title="Another twipsy"></a>').twipsy()
        equals(twipsy.attr('data-original-title'), 'Another twipsy', 'original title preserved in data attribute')
    })

    test("should place tooltips relative to placement option", function () {
        $.support.transition = false
        var twipsy = $('<a href="#" rel="twipsy" title="Another twipsy"></a>')
            .appendTo('#qunit-runoff')
            .twipsy({placement:'below'})
            .twipsy('show')

        ok($(".twipsy").hasClass('fade below in'), 'has correct classes applied')
        twipsy.twipsy('hide')
        ok(!$(".twipsy").length, 'twipsy removed')
        $('#qunit-runoff').empty()
    })

    test("should add a fallback in cases where elements have no title tag", function () {
        $.support.transition = false
        var twipsy = $('<a href="#" rel="twipsy"></a>')
            .appendTo('#qunit-runoff')
            .twipsy({fallback:'@fat'})
            .twipsy('show')

        equals($(".twipsy").text(), "@fat", 'has correct default text')
        twipsy.twipsy('hide')
        ok(!$(".twipsy").length, 'twipsy removed')
        $('#qunit-runoff').empty()
    })

    test("should not allow html entities", function () {
        $.support.transition = false
        var twipsy = $('<a href="#" rel="twipsy" title="<b>@fat</b>"></a>')
            .appendTo('#qunit-runoff')
            .twipsy()
            .twipsy('show')

        ok(!$('.twipsy b').length, 'b tag was not inserted')
        twipsy.twipsy('hide')
        ok(!$(".twipsy").length, 'twipsy removed')
        $('#qunit-runoff').empty()
    })

    test("should allow html entities if html option set to true", function () {
        $.support.transition = false
        var twipsy = $('<a href="#" rel="twipsy" title="<b>@fat</b>"></a>')
            .appendTo('#qunit-runoff')
            .twipsy({html:true})
            .twipsy('show')

        ok($('.twipsy b').length, 'b tag was inserted')
        twipsy.twipsy('hide')
        ok(!$(".twipsy").length, 'twipsy removed')
        $('#qunit-runoff').empty()
    })

})