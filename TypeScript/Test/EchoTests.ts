import echoFunction from '../Echo/index';
import context from './fixtures/testContext';
var should = require('chai').should();

describe('Echo', function() {
    it ("returns defaults in result when no parameters provided", async function(){
        var request = {};

        await echoFunction(context, request)

        context.res.should.not.have.property('status');
        context.res.should.not.have.property('body');
    });
    it ("returns query parameters in result", async function() {
        var expectedCode = 301;            
        var expectedMessage = "test message";
        var request = { 
            query: {
                result: expectedCode,
                message: expectedMessage
            } 
        };

        await echoFunction(context, request)

        context.res.status.should.equal(expectedCode);
        context.res.body.should.equal(expectedMessage);
    });
    it ("returns body in result", async function() {
        var expectedCode = 301;            
        var expectedMessage = "test message";
        var request = { 
            body: {
                result: expectedCode,
                message: expectedMessage
            } 
        };

        await echoFunction(context, request)

        context.res.status.should.equal(expectedCode);
        context.res.body.should.equal(expectedMessage);
    });
});