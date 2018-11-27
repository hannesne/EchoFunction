using System;
using System.Collections.Specialized;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using System.Web;
using System.Web.Http;
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
            HttpRequestMessage request = HttpRequestSetup(new NameValueCollection(), null);
            ILogger logger = new Mock<ILogger>().Object;

            HttpResponseMessage response = await Echo.Echo.Run(request, logger);

            Assert.AreEqual(HttpStatusCode.OK, response.StatusCode);
            Assert.IsNull(response.Content);
        }

        [TestMethod]
        public async Task ReturnsQueryParametersInResult()
        {
            HttpStatusCode expectedCode = HttpStatusCode.Continue;
            string expectedMessage = "test message";

            HttpRequestMessage request = HttpRequestSetup(new NameValueCollection{{"message",expectedMessage},{"result",expectedCode.ToString()}},"" );
            ILogger logger = new Mock<ILogger>().Object;

            HttpResponseMessage response =await Echo.Echo.Run(request, logger);
            
            Assert.AreEqual(expectedCode, response.StatusCode);
            dynamic actualMessage = await (dynamic) response.Content.ReadAsAsync<object>();
            Assert.AreEqual(expectedMessage,actualMessage);
        }

        [TestMethod]
        public async Task ReturnsBodyInResult()
        {
            HttpStatusCode expectedCode = HttpStatusCode.Continue;
            string expectedMessage = "test message";
            dynamic body = new 
            {
                message = expectedMessage, result = expectedCode.ToString()
            };
            HttpRequestMessage request = HttpRequestSetup(new NameValueCollection( ), body);
            ILogger logger = new Mock<ILogger>().Object;

            HttpResponseMessage response = await Echo.Echo.Run(request, logger);

            Assert.AreEqual(expectedCode, response.StatusCode);
            dynamic actualMessage = await (dynamic)response.Content.ReadAsAsync<object>();
            Assert.AreEqual(expectedMessage, actualMessage);
        }

        private HttpRequestMessage HttpRequestSetup(NameValueCollection query, object body)
        {
            Uri uri = new Uri("http://localhost/api/Echo");

            if (query.Count > 0)
            {
                NameValueCollection httpValueCollection = HttpUtility.ParseQueryString(uri.Query);
                httpValueCollection.Add(query);

                uri = new UriBuilder(uri) { Query = httpValueCollection.ToString() }.Uri;
            }

            HttpRequestMessage request = new HttpRequestMessage(HttpMethod.Get, uri);

            string tempPath = Environment.GetEnvironmentVariable("temp");
            HttpRouteCollection routeCollection = new HttpRouteCollection(tempPath);
            request.SetConfiguration(new HttpConfiguration(routeCollection));

            request.Content = new StringContent(JsonConvert.SerializeObject(body), Encoding.UTF8, "application/json");
            return request;
        }
    }
}
