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

(function(root, factory) {
  if (typeof define === 'function' && define.amd) {
    // AMD.
    define(['expect.js', '../../src/index'], factory);
  } else if (typeof module === 'object' && module.exports) {
    // CommonJS-like environments that support module.exports, like Node.
    factory(require('expect.js'), require('../../src/index'));
  } else {
    // Browser globals (root is window)
    factory(root.expect, root.NimbitsApi);
  }
}(this, function(expect, NimbitsApi) {
  'use strict';

  var instance;

  beforeEach(function() {
    instance = new NimbitsApi.TopicApi();
  });

  var getProperty = function(object, getter, property) {
    // Use getter method if present; otherwise, get the property directly.
    if (typeof object[getter] === 'function')
      return object[getter]();
    else
      return object[property];
  }

  var setProperty = function(object, setter, property, value) {
    // Use setter method if present; otherwise, set the property directly.
    if (typeof object[setter] === 'function')
      object[setter](value);
    else
      object[property] = value;
  }

  describe('TopicApi', function() {
    describe('addTopic', function() {
      it('should call addTopic successfully', function(done) {
        //uncomment below and update the code to test addTopic
        //instance.addTopic(function(error) {
        //  if (error) throw error;
        //expect().to.be();
        //});
        done();
      });
    });
    describe('deleteTopic', function() {
      it('should call deleteTopic successfully', function(done) {
        //uncomment below and update the code to test deleteTopic
        //instance.deleteTopic(function(error) {
        //  if (error) throw error;
        //expect().to.be();
        //});
        done();
      });
    });
    describe('getDataTable', function() {
      it('should call getDataTable successfully', function(done) {
        //uncomment below and update the code to test getDataTable
        //instance.getDataTable(function(error) {
        //  if (error) throw error;
        //expect().to.be();
        //});
        done();
      });
    });
    describe('getGroup', function() {
      it('should call getGroup successfully', function(done) {
        //uncomment below and update the code to test getGroup
        //instance.getGroup(function(error) {
        //  if (error) throw error;
        //expect().to.be();
        //});
        done();
      });
    });
    describe('getGroups', function() {
      it('should call getGroups successfully', function(done) {
        //uncomment below and update the code to test getGroups
        //instance.getGroups(function(error) {
        //  if (error) throw error;
        //expect().to.be();
        //});
        done();
      });
    });
    describe('getSnapshot', function() {
      it('should call getSnapshot successfully', function(done) {
        //uncomment below and update the code to test getSnapshot
        //instance.getSnapshot(function(error) {
        //  if (error) throw error;
        //expect().to.be();
        //});
        done();
      });
    });
    describe('getTopic', function() {
      it('should call getTopic successfully', function(done) {
        //uncomment below and update the code to test getTopic
        //instance.getTopic(function(error) {
        //  if (error) throw error;
        //expect().to.be();
        //});
        done();
      });
    });
    describe('postSnapshot', function() {
      it('should call postSnapshot successfully', function(done) {
        //uncomment below and update the code to test postSnapshot
        //instance.postSnapshot(function(error) {
        //  if (error) throw error;
        //expect().to.be();
        //});
        done();
      });
    });
  });

}));
