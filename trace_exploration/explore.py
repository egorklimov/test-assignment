import pandas as pd
import json


def read_traces(file):
    with open(file) as f:
        trace_data = json.load(f)
        return pd.DataFrame(trace_data['data'][0]['spans'])


if __name__ == "__main__":
    df = read_traces("./traces/trace_get_all_cats.json")
    print(df.head())
