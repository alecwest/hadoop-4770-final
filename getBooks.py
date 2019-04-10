import requests
import re

location = 'http://www.textfiles.com/etext/FICTION/'
request = requests.get(location)
urls = re.sub(r'.*A HREF="(\S+)".*', location + r'\1', request.text)
urls = re.sub(r'\n?<.*\n?', r'', urls).split('\n')
for url in urls:
    print(url)
    r = requests.get(url)
    open(re.sub(r'([^\.]+)(\.txt)?', r'/path/to/word-counter/input/' + r'\1.txt', url.split('/')[-1], 1), 'wb').write(r.content) # CHANGE THE PATH HERE
