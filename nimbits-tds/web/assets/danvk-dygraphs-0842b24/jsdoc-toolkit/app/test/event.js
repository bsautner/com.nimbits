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

/**
 * @name Kitchen
 * @constructor
 * @fires Bakery#event:donutOrdered
 */

/**
 * Fired when some cake is eaten.
 * @name Kitchen#event:cakeEaten
 * @function
 * @param {Number} pieces The number of pieces eaten.
 */

/**
 * Find out if cake was eaten.
 * @name Kitchen#cakeEaten
 * @function
 * @param {Boolean} wasEaten
 */

/**
 * @name getDesert
 * @function
 * @fires Kitchen#event:cakeEaten
 */

/**
 * @name Bakery
 * @constructor
 * @extends Kitchen
 */

/**
 * Fired when a donut order is made.
 * @name Bakery#event:donutOrdered
 * @event
 * @param {Event} e The event object.
 * @param {String} [e.topping] Optional sprinkles.
 */

/**
 * @constructor
 * @borrows Bakery#event:donutOrdered as this.event:cakeOrdered
 */
function CakeShop() {
}

/** @event */
CakeShop.prototype.icingReady = function(isPink) {
}

/** @event */
function amHungry(/**Boolean*/enoughToEatAHorse) {
}