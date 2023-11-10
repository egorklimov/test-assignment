import pandas as pd
import json

with open("trace.json") as f:
  trace_data = json.load(f)
  spans = trace_data['data'][0]['spans']

if __name__ == "__main__":
  df = pd.DataFrame(spans)
  print(df.head())
