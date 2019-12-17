using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Http.Internal;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using Newtonsoft.Json;

namespace EchoUnitTest
{
    [TestClass]
    public class EchoTests
    {
        [TestMethod]
        public async Task ReturnsDefaultsInResultWhenNoParametersProvided()
        {
            HttpRequest request = CreateRequest();
            ILogger logger = new Mock<ILogger>().Object;

            ObjectResult response = (ObjectResult) await Echo.Echo.Run(request, logger);

            Assert.AreEqual((int) HttpStatusCode.OK, response.StatusCode);
            Assert.IsNull(response.Value);
        }

        [TestMethod]
        public async Task ReturnsQueryParametersInResult()
        {
            HttpStatusCode expectedCode = HttpStatusCode.Continue;
            string expectedMessage = "test message";

            HttpRequest request = CreateRequest(new[] { new KeyValuePair<string, string>("message", expectedMessage), new KeyValuePair<string, string>("result", expectedCode.ToString())});
            ILogger logger = new Mock<ILogger>().Object;

            ObjectResult response = (ObjectResult)await Echo.Echo.Run(request, logger);


            Assert.AreEqual((int)expectedCode, response.StatusCode);
            Assert.AreEqual(expectedMessage, response.Value);
        }

        [TestMethod]
        public async Task ReturnsBodyInResult()
        {
            HttpStatusCode expectedCode = HttpStatusCode.Continue;
            string expectedMessage = "test message";
            dynamic body = CreateBody(expectedMessage, expectedCode);

            string serializedBody = JsonConvert.SerializeObject(body);
            HttpRequest request = CreateRequest(serializedBody);
            ILogger logger = new Mock<ILogger>().Object;

            ObjectResult response = (ObjectResult)await Echo.Echo.Run(request, logger);

            Assert.AreEqual((int)expectedCode, response.StatusCode);
            Assert.AreEqual(expectedMessage, response.Value);
        }

        private static dynamic CreateBody(string expectedMessage, HttpStatusCode expectedCode)
        {
            dynamic body = new
            {
                message = expectedMessage,
                result = expectedCode.ToString()
            };
            return body;
        }

        private HttpRequest CreateRequest(string body = null) 
            => CreateRequest(Enumerable.Empty<KeyValuePair<string, string>>(), body);

        private HttpRequest CreateRequest(IEnumerable<KeyValuePair<string, string>> parameters, string body = null)
        {
            HttpRequest request = new DefaultHttpRequest(new DefaultHttpContext())
            {
                QueryString = QueryString.Create(parameters),
            };

            if (!string.IsNullOrWhiteSpace(body))
            {
                request.Body = new MemoryStream(Encoding.UTF8.GetBytes(body));
            }
            
            return request;
        }
    }
}
