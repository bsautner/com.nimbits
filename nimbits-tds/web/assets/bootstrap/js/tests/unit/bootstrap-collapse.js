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

    module("bootstrap-collapse")

      test("should be defined on jquery object", function () {
        ok($(document.body).collapse, 'collapse method is defined')
      })

      test("should return element", function () {
        ok($(document.body).collapse()[0] == document.body, 'document.body returned')
      })

      test("should show a collapsed element", function () {
        var el = $('<div class="collapse"></div>').collapse('show')
        ok(el.hasClass('in'), 'has class in')
        ok(/height/.test(el.attr('style')), 'has height set')
      })

      test("should hide a collapsed element", function () {
        var el = $('<div class="collapse"></div>').collapse('hide')
        ok(!el.hasClass('in'), 'does not have class in')
        ok(/height/.test(el.attr('style')), 'has height set')
      })

      test("should not fire shown when show is prevented", function () {
        $.support.transition = false
        stop();
        $('<div class="collapse"/>')
          .bind('show', function (e) {
            e.preventDefault();
            ok(true);
            start();
          })
          .bind('shown', function () {
            ok(false);
          })
          .collapse('show')
      })

      test("should reset style to auto after finishing opening collapse", function () {
        $.support.transition = false
        stop();
        $('<div class="collapse" style="height: 0px"/>')
          .bind('show', function () {
            ok(this.style.height == '0px')
          })
          .bind('shown', function () {
            ok(this.style.height == 'auto')
            start()
          })
          .collapse('show')
      })

})