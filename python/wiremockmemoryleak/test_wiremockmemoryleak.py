from unittest import TestCase

from wiremock.constants import Config
from wiremock.server import WireMockServer
from wiremock.client import *
import requests

#
# wiremock test class
#
class MyWiremockTestClassBase(TestCase):
    @classmethod
    def setUpClass(cls):
        wm = cls.wiremock_server = WireMockServer()
        wm.start()
        Config.base_url = 'http://localhost:{}/__admin'.format(wm.port)
        print(f"started wiremock: {Config.base_url}")
        wiremock_port = wm.port
        cls.wiremock_url = f"http://localhost:{wiremock_port}"

        Mappings.create_mapping(
            Mapping(
                request=MappingRequest(method=HttpMethods.GET, url="/hello"),
                response=MappingResponse(status=200, body="hello"),
                persistent=False,
            )
        )

        # show pid
        wmpid = cls.wiremock_server._WireMockServer__subprocess.pid
        print(f"wiremock pid: {wmpid}")

    @classmethod
    def tearDownClass(cls):
        print(f"stopping wiremock: {Config.base_url}")
        cls.wiremock_server.stop()

    def test_use_wiremock(self):
        #wiremock_server = MyWiremockTestClassBase.wiremock_server
        #wiremock_server = self.__class__.wiremock_server
        #helloUrl = wiremock_server.get_url("/hello")

        helloUrl = f"{self.__class__.wiremock_url}/hello"

        response = requests.get(helloUrl)
        assert response.status_code == 200
        assert response.content == b"hello"
