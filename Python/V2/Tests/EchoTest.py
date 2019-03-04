import unittest
import Echo
import azure.functions as func
import json

class EchoTest(unittest.TestCase):
    TEST_URL = "http://myfunction.com/api/echo"
    EXPECTED_CODE = 204
    EXPECTED_MESSAGE = "test message"

    def test_returns_defaults_in_result_when_no_parameters_provided(self):        
        request = func.HttpRequest(method= "POST",url= EchoTest.TEST_URL,body= bytearray())

        response = Echo.main(request)

        self.assertEquals(response.status_code,200)
        self.assertEqual(response.get_body(), bytearray())

    def test_returns_query_parameters_in_result(self):
        

        request = func.HttpRequest(method= "POST",
            url= EchoTest.TEST_URL,
            params=dict(message=EchoTest.EXPECTED_MESSAGE,result=EchoTest.EXPECTED_CODE), 
            body= bytearray())

        response = Echo.main(request)

        self.assertEquals(response.status_code,EchoTest.EXPECTED_CODE)
        self.assertEqual(response.get_body(), bytearray(EchoTest.EXPECTED_MESSAGE,"utf-8"))

    def test_returns_body_in_result(self):
        
        body = {'message':EchoTest.EXPECTED_MESSAGE, 'result':EchoTest.EXPECTED_CODE}
        request = func.HttpRequest(method= "POST",
            url= EchoTest.TEST_URL,
            body= bytearray(json.dumps(body), "utf-8"))

        response = Echo.main(request)

        self.assertEquals(response.status_code,EchoTest.EXPECTED_CODE)
        self.assertEqual(response.get_body(), bytearray(EchoTest.EXPECTED_MESSAGE,"utf-8"))

if __name__ == '__main__':
    unittest.main()

