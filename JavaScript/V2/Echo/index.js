module.exports = async function (context, req) {
    var expectedMessage, expectedResult;
    var response = {};

    if (expectedResult = extractParameter(req, "result"))
    {
        response.status = expectedResult;
    }
    if (expectedMessage = extractParameter(req, "message"))
    {
        response.body = expectedMessage;
    }
    
    context.log.info("Executing Echo Function with following parameters:\nmessage - %s\nresult - %s", expectedMessage, expectedResult);
    context.res = response;
};

function extractParameter(request, parameterName)
{
    if (request.query && request.query[parameterName])
        return request.query[parameterName];
    if (request.body && request.body[parameterName])
        return request.body[parameterName];
}