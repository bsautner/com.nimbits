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


!function ($) {

    "use strict"

    /* DROPDOWN PLUGIN DEFINITION
     * ========================== */

    $.fn.dropdown = function (selector) {
        return this.each(function () {
            $(this).delegate(selector || d, 'click', function (e) {
                var li = $(this).parent('li')
                    , isActive = li.hasClass('open')

                clearMenus()
                !isActive && li.toggleClass('open')
                return false
            })
        })
    }

    /* APPLY TO STANDARD DROPDOWN ELEMENTS
     * =================================== */

    var d = 'a.menu, .dropdown-toggle'

    function clearMenus() {
        $(d).parent('li').removeClass('open')
    }

    $(function () {
        $('html').bind("click", clearMenus)
        $('body').dropdown('[data-dropdown] a.menu, [data-dropdown] .dropdown-toggle')
    })

}(window.jQuery || window.ender);
