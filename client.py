import requests

APIHOST='http://172.17.0.1:3233/api/v1'

headers = {
    'Authorization': 'Basic MjNiYzQ2YjEtNzFmNi00ZWQ1LThjNTQtODE2YWE0ZjhjNTAyOjEyM3pPM3haQ0xyTU42djJCS0sxZFhZRnBYbFBrY2NPRnFtMTJDZEFzTWdSVTRWck5aOWx5R1ZDR3VNREdJd1A='
}


#Get avaliable actions
#url = APIHOST+'/namespaces/guest/actions'
#response = requests.get(url, headers=headers)
#for action in response.json(): print(action['name'])


# Invoke add action
    
url = APIHOST+'/namespaces/guest/actions/add?blocking=true&result=true&workers=3'
req_body = {"param1": 11, "param2": 6}
response = requests.post(url, json=req_body, headers=headers)
print(response.text)
