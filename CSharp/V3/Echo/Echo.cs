using System;
using System.IO;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Http.Internal;
using Microsoft.AspNetCore.Mvc.Formatters.Internal;
using Microsoft.AspNetCore.WebUtilities;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;

namespace Echo
{
    public static class Echo
    {
        [FunctionName("Echo")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "get", "post", Route = null)] HttpRequest req,
            ILogger log)
        {
            string requestBody;
            using (StreamReader streamReader = new StreamReader(req.Body))
            {
                requestBody = await streamReader.ReadToEndAsync();
            }

            string expectedMessage = await ExtractParameter(req, "message", requestBody);
            string expectedResult = await ExtractParameter(req, "result", requestBody);

            if (!Enum.TryParse(expectedResult, out HttpStatusCode httpResult))
                httpResult = HttpStatusCode.OK;

            return new ObjectResult(expectedMessage)
            {
                StatusCode = (int)httpResult
            };

        }

        private static async Task<string> ExtractParameter(HttpRequest req, string parameterName,
            string requestBody)
        {

            string value = req.Query[parameterName];
            
            if (!string.IsNullOrEmpty(value))
                return value;

            

            dynamic data = JsonConvert.DeserializeObject(requestBody);
            
            value = data?[parameterName];

            return value;

        }
    }
}
