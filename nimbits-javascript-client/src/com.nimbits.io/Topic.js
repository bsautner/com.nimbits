/**
 * Nimbits API
 * Nimbits Server Provides deep automation via a REST API
 *
 * OpenAPI spec version: 5.0
 * Contact: support@nimbits.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 *
 */


import ApiClient from '../ApiClient';





/**
* The Topic model module.
* @module com.nimbits.io/Topic
* @version 5.0
*/
export default class Topic {
    /**
    * Constructs a new <code>Topic</code>.
    * @alias module:com.nimbits.io/Topic
    * @class
    */

    constructor() {
        

        
        

        

        
    }

    /**
    * Constructs a <code>Topic</code> from a plain JavaScript object, optionally creating a new instance.
    * Copies all relevant properties from <code>data</code> to <code>obj</code> if supplied or a new instance if not.
    * @param {Object} data The plain JavaScript object bearing properties of interest.
    * @param {module:com.nimbits.io/Topic} obj Optional instance to populate.
    * @return {module:com.nimbits.io/Topic} The populated <code>Topic</code> instance.
    */
    static constructFromObject(data, obj) {
        if (data) {
            obj = obj || new Topic();

            
            
            

            if (data.hasOwnProperty('description')) {
                obj['description'] = ApiClient.convertToType(data['description'], 'String');
            }
            if (data.hasOwnProperty('id')) {
                obj['id'] = ApiClient.convertToType(data['id'], 'String');
            }
            if (data.hasOwnProperty('name')) {
                obj['name'] = ApiClient.convertToType(data['name'], 'String');
            }
            if (data.hasOwnProperty('topic_type')) {
                obj['topic_type'] = ApiClient.convertToType(data['topic_type'], 'String');
            }
            if (data.hasOwnProperty('type')) {
                obj['type'] = ApiClient.convertToType(data['type'], 'String');
            }
            if (data.hasOwnProperty('unit')) {
                obj['unit'] = ApiClient.convertToType(data['unit'], 'String');
            }
        }
        return obj;
    }

    /**
    * @member {String} description
    */
    description = undefined;
    /**
    * @member {String} id
    */
    id = undefined;
    /**
    * @member {String} name
    */
    name = undefined;
    /**
    * @member {module:com.nimbits.io/Topic.TopicTypeEnum} topic_type
    */
    topic_type = undefined;
    /**
    * @member {String} type
    */
    type = undefined;
    /**
    * @member {String} unit
    */
    unit = undefined;






    /**
    * Allowed values for the <code>topic_type</code> property.
    * @enum {String}
    * @readonly
    */
    static TopicTypeEnum = {
    
        /**
         * value: "basic"
         * @const
         */
        "basic": "basic",
    
        /**
         * value: "cumulative"
         * @const
         */
        "cumulative": "cumulative",
    
        /**
         * value: "timespan"
         * @const
         */
        "timespan": "timespan",
    
        /**
         * value: "flag"
         * @const
         */
        "flag": "flag",
    
        /**
         * value: "high"
         * @const
         */
        "high": "high",
    
        /**
         * value: "low"
         * @const
         */
        "low": "low",
    
        /**
         * value: "heartbeat"
         * @const
         */
        "heartbeat": "heartbeat",
    
        /**
         * value: "geo"
         * @const
         */
        "geo": "geo",
    
        /**
         * value: "json"
         * @const
         */
        "json": "json",
    
        /**
         * value: "random"
         * @const
         */
        "random": "random"    
    };



}


