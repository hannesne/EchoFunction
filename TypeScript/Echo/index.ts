import { AzureFunction, Context, HttpRequest } from "@azure/functions"

const httpTrigger: AzureFunction = async function (context: Context, req: HttpRequest): Promise<void> {
    var expectedMessage, expectedResult : string;
    var response: any = {};

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

function extractParameter(request: HttpRequest, parameterName: string) : string
{
    if (request.query && request.query[parameterName])
        return request.query[parameterName];
    if (request.body && request.body[parameterName])
        return request.body[parameterName];
}

export default httpTrigger;
