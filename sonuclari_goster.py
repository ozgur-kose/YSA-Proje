import pandas as pd

df = pd.read_csv('final_teshis_raporu.csv')

print(df[['Temperature', 'Humidity', 'Durum']].head(10))

print("\n--- GENEL DURUM ÖZETİ ---")
print(df['Durum'].value_counts())