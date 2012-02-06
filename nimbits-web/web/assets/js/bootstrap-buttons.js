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

    function setState(el, state) {
        var d = 'disabled'
            , $el = $(el)
            , data = $el.data()

        state = state + 'Text'
        data.resetText || $el.data('resetText', $el.html())

        $el.html(data[state] || $.fn.button.defaults[state])

        setTimeout(function () {
            state == 'loadingText' ?
                $el.addClass(d).attr(d, d) :
                $el.removeClass(d).removeAttr(d)
        }, 0)
    }

    function toggle(el) {
        $(el).toggleClass('active')
    }

    $.fn.button = function (options) {
        return this.each(function () {
            if (options == 'toggle') {
                return toggle(this)
            }
            options && setState(this, options)
        })
    }

    $.fn.button.defaults = {
        loadingText:'loading...'
    }

    $(function () {
        $('body').delegate('.btn[data-toggle]', 'click', function () {
            $(this).button('toggle')
        })
    })

}(window.jQuery || window.ender);