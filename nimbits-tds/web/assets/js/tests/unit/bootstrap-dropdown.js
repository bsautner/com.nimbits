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

    module("bootstrap-dropdowns")

    test("should be defined on jquery object", function () {
        ok($(document.body).dropdown, 'dropdown method is defined')
    })

    test("should return element", function () {
        ok($(document.body).dropdown()[0] == document.body, 'document.body returned')
    })

    test("should add class open to menu if clicked", function () {
        var dropdownHTML = '<ul class="tabs">'
            + '<li class="dropdown">'
            + '<a href="#" class="dropdown-toggle">Dropdown</a>'
            + '<ul class="dropdown-menu">'
            + '<li><a href="#">Secondary link</a></li>'
            + '<li><a href="#">Something else here</a></li>'
            + '<li class="divider"></li>'
            + '<li><a href="#">Another link</a></li>'
            + '</ul>'
            + '</li>'
            + '</ul>'
            , dropdown = $(dropdownHTML).dropdown()

        dropdown.find('.dropdown-toggle').click()
        ok(dropdown.find('.dropdown').hasClass('open'), 'open class added on click')
    })

    test("should remove open class if body clicked", function () {
        var dropdownHTML = '<ul class="tabs">'
            + '<li class="dropdown">'
            + '<a href="#" class="dropdown-toggle">Dropdown</a>'
            + '<ul class="dropdown-menu">'
            + '<li><a href="#">Secondary link</a></li>'
            + '<li><a href="#">Something else here</a></li>'
            + '<li class="divider"></li>'
            + '<li><a href="#">Another link</a></li>'
            + '</ul>'
            + '</li>'
            + '</ul>'
            , dropdown = $(dropdownHTML).dropdown().appendTo('#qunit-runoff')

        dropdown.find('.dropdown-toggle').click()
        ok(dropdown.find('.dropdown').hasClass('open'), 'open class added on click')
        $('body').click()
        ok(!dropdown.find('.dropdown').hasClass('open'), 'open class removed')
        dropdown.remove()
    })

})