import os
import requests
import tarfile

location = 'https://www.ncei.noaa.gov/data/global-summary-of-the-day/archive/'
years = [i for i in range(1929, 2020)]
base_path = './weather-input/'

for year in years:
    file_name = str(year) + ".tar.gz"
    file_path = base_path + file_name
    tar_location = location + file_path
    url = location + file_name
    if not os.path.isfile(file_path):
        print(url)
        response = requests.get(url)
        if response.status_code == 200:
            with open(file_path, 'wb') as f:
                f.write(response.content)
            print('extracting ' + file_path)
            tf = tarfile.open(file_path)
            tf.extractall(path=base_path)
            tf.close()
            os.remove(file_path)
        else:
            print('could not get url ' + url)
