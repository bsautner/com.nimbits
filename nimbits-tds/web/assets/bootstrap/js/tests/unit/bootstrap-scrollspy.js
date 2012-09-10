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

$(function () {

    module("bootstrap-scrollspy")

      test("should be defined on jquery object", function () {
        ok($(document.body).scrollspy, 'scrollspy method is defined')
      })

      test("should return element", function () {
        ok($(document.body).scrollspy()[0] == document.body, 'document.body returned')
      })

      test("should switch active class on scroll", function () {
        var sectionHTML = '<div id="masthead"></div>'
          , $section = $(sectionHTML).append('#qunit-fixture')
          , topbarHTML ='<div class="topbar">'
          + '<div class="topbar-inner">'
          + '<div class="container">'
          + '<h3><a href="#">Bootstrap</a></h3>'
          + '<ul class="nav">'
          + '<li><a href="#masthead">Overview</a></li>'
          + '</ul>'
          + '</div>'
          + '</div>'
          + '</div>'
          , $topbar = $(topbarHTML).scrollspy()

        ok($topbar.find('.active', true))
      })

})