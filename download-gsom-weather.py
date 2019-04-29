import os
import glob
import pandas as pd
import requests
import tarfile

location = 'https://www.ncei.noaa.gov/data/global-summary-of-the-day/archive/'
years = [i for i in range(1929, 2020)]
os.chdir('weather-input')

for year in years:
    file_name = str(year) + ".tar.gz"
    tar_location = location + file_name
    url = location + file_name
    if not os.path.isfile(file_name):
        print(url)
        response = requests.get(url)
        if response.status_code == 200:
            with open(file_name, 'wb') as f:
                f.write(response.content)
            print('extracting ' + file_name)
            tf = tarfile.open(file_name)
            tf.extractall()
            # combine files into one csv for the year
            filenames = tf.getnames()
            combined_csv = pd.concat([pd.read_csv(f) for f in filenames])
            print('combined {} files'.format(len(filenames)))
            combined_csv.to_csv('{}.csv'.format(year), index=False, encoding='utf-8')
            tf.close()
            # delete all files
            os.remove(file_name)
            for f in filenames:
                os.remove(f)
        else:
            print('could not get url ' + url)
