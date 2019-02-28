import unittest
import Echo
import azure.functions as func

class EchoTest(unittest.TestCase):
    def test_HelloWorld(self):
        request = func.HttpRequest(method= "POST",url= "http://myfunction.com/api/echo",params=dict(name="James") ,body= bytearray())
        response = Echo.main(request)
        self.assertEquals(response.get_body(),bytearray("Hello James!","utf-8"))

if __name__ == '__main__':
    unittest.main()

