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

$(function () {

    module("bootstrap-popover")

    test("should be defined on jquery object", function () {
        var div = $('<div></div>')
        ok(div.popover, 'popover method is defined')
    })

    test("should return element", function () {
        var div = $('<div></div>')
        ok(div.popover() == div, 'document.body returned')
    })

    test("should render popover element", function () {
        $.support.transition = false
        var popover = $('<a href="#" title="mdo" data-content="http://twitter.com/mdo">@mdo</a>')
            .appendTo('#qunit-runoff')
            .popover()
            .popover('show')

        ok($('.popover').length, 'popover was inserted')
        popover.popover('hide')
        ok(!$(".popover").length, 'popover removed')
        $('#qunit-runoff').empty()
    })

    test("should store popover instance in popover data object", function () {
        $.support.transition = false
        var popover = $('<a href="#" title="mdo" data-content="http://twitter.com/mdo">@mdo</a>')
            .popover()

        ok(!!popover.data('popover'), 'popover instance exists')
    })

    test("should get title and content from options", function () {
        $.support.transition = false
        var popover = $('<a href="#">@fat</a>')
            .appendTo('#qunit-runoff')
            .popover({
                title:function () {
                    return '@fat'
                }, content:function () {
                    return 'loves writing tests （╯°□°）╯︵ ┻━┻'
                }
            })

        popover.popover('show')

        ok($('.popover').length, 'popover was inserted')
        equals($('.popover .title').text(), '@fat', 'title correctly inserted')
        equals($('.popover .content').text(), 'loves writing tests （╯°□°）╯︵ ┻━┻', 'content correctly inserted')

        popover.popover('hide')
        ok(!$('.popover').length, 'popover was removed')
        $('#qunit-runoff').empty()
    })

    test("should get title and content from attributes", function () {
        $.support.transition = false
        var popover = $('<a href="#" title="@mdo" data-content="loves data attributes (づ｡◕‿‿◕｡)づ ︵ ┻━┻" >@mdo</a>')
            .appendTo('#qunit-runoff')
            .popover()
            .popover('show')

        ok($('.popover').length, 'popover was inserted')
        equals($('.popover .title').text(), '@mdo', 'title correctly inserted')
        equals($('.popover .content').text(), "loves data attributes (づ｡◕‿‿◕｡)づ ︵ ┻━┻", 'content correctly inserted')

        popover.popover('hide')
        ok(!$('.popover').length, 'popover was removed')
        $('#qunit-runoff').empty()
    })

})
