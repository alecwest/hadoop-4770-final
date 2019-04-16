import os
import requests
import re

genres = ['FICTION/', 'NONFICTION/', 'MODERN/', 'REFERENCE/']
location = 'http://www.textfiles.com/etext/'
base_path = './input/'

for genre in genres:
    genre_location = location + genre
    request = requests.get(genre_location)
    urls = re.sub(r'.*A HREF="(\S+)".*', genre_location + r'\1', request.text)
    urls = re.sub(r'\n?<.*\n?', r'', urls).split('\n')
    for url in urls:
        file_path = re.sub(r'([^\.]+)(\.txt)?', base_path + r'\1.txt', url.split('/')[-1], 1)
        if not os.path.isfile(file_path):
            print(url)
            r = requests.get(url)
            if r.status_code == 200:
                open(file_path, 'wb').write(r.content)
            else:
                print('could not get url ' + url)

