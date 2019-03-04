import logging
import azure.functions as func


def main(req: func.HttpRequest) -> func.HttpResponse:
    logging.info('Python HTTP trigger function processed a request.')
    
    try:
        json_body = req.get_json()
    except ValueError:
        json_body = {}
    
    expected_status_code = __load_value(req, json_body, 'result')
    expected_message = __load_value(req, json_body, 'message')

    return func.HttpResponse(body=expected_message,status_code=expected_status_code)

def __load_value(req, json_body, variable_name):
    variable_value = req.params.get(variable_name)

    if variable_value is None and variable_name in json_body.keys():
        variable_value = json_body[variable_name]
    return variable_value
    
