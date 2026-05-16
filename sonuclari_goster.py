import pandas as pd

# Raporu oku
df = pd.read_csv('final_teshis_raporu.csv')

# İlk 10 satırı ve teşhisleri göster
print("--- İLK 10 VERİ VE YAPAY ZEKA TEŞHİSİ ---")
print(df[['Temperature', 'Humidity', 'Durum']].head(10))

print("\n--- GENEL DURUM ÖZETİ ---")
print(df['Durum'].value_counts())