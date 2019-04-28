import os
import glob
import pandas as pd
os.chdir("weather-input")
extension = 'csv'
all_filenames = [i for i in glob.glob('*.{}'.format(extension))]

combined_csv = pd.concat([pd.read_csv(f) for f in all_filenames])

# Combine every 1000 files into one csv file
for i in range(0, len(all_filenames), 1000):
    combined_csv = pd.concat([pd.read_csv(f) for f in all_filenames[i:i+1000]])
    print('combined {} files'.format(len(all_filenames[i:i+1000])))
    combined_csv.to_csv("combined_csv_" + str(i) + "_" + str(i + len(all_filenames[i:i+1000])) + ".csv", index=False, encoding='utf-8')


