using System;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.Extensions.Logging;

namespace Echo
{
    public static class Echo
    {
        [FunctionName("Echo")]
        public static async Task<HttpResponseMessage> Run([HttpTrigger(AuthorizationLevel.Anonymous, "get", "post", Route = null)]HttpRequestMessage req, ILogger log)
        {
            // ReadAsAsync is not atomic on streams, so we only want to do this once.
            dynamic data = await req.Content.ReadAsAsync<object>();

            string expectedMessage = ExtractParameter(req, "message", data);
            string expectedResult = ExtractParameter(req, "result", data);

            if (!Enum.TryParse(expectedResult, out HttpStatusCode httpResult))
                httpResult = HttpStatusCode.OK;

            log.LogInformation("Executing Echo Function with following parameters:\nmessage - {expectedMessage}\nresult - {expectedResult}", expectedMessage, expectedResult);
            
            return expectedMessage == null ? 
                req.CreateResponse(httpResult) : 
                req.CreateResponse(httpResult,expectedMessage);
            
        }

        private static string ExtractParameter(HttpRequestMessage req, string parameterName, dynamic data)
        {
            string parameter = req.GetQueryNameValuePairs()
                .FirstOrDefault(q => string.Compare(q.Key, parameterName, StringComparison.OrdinalIgnoreCase) == 0)
                .Value;

            if (parameter != null)
                return parameter;

            if (data != null && data.ToString() != string.Empty)
            {
                parameter = data[parameterName];
            }

            return parameter;
        }
    }
}
