import pandas as pd

# Veriyi okuyoruz
df = pd.read_csv("AgricultureDataset.csv", encoding="cp1254", sep=";")

gereksiz_kolonlar = [
    'Zone_ID', 'Image_Source_ID', 'Image_Type', 'UAV_Timestamp', 
    'Energy_Consumed_mAh', 'Latency_ms', 'Current_Node', 
    'Migrated_To', 'Migration_Required', 'Migration_Timestamp',
    'NDVI','NDRE','RGB_Damage_Score','NDI_Label','PDI_Label'
]

# İŞTE DÜZELTİLEN 2. HATA BURASI (axis=1 silindi):
df = df.drop(columns=gereksiz_kolonlar)

print(f"Hiçbir işlem yapmadan önceki satır sayısı: {len(df)}")

# İŞTE DÜZELTİLEN 1. HATA BURASI (Başına df eklendi):
df['Temperature'] = df['Temperature'].replace(',', '.', regex=True).astype(float)

# kullancağımız kolonlar 
hayati_kolonlar = ['Temperature', 'Humidity', 'Moisture', 'pH', 'N', 'P', 'K']
df = df.dropna(subset=hayati_kolonlar)

print(f"Hayati boşluklar silindikten sonra kalan satır: {len(df)}")

# null değerleri 0 yap
df = df.fillna(0)

# mantıksız değerleri sil
df = df[df['Temperature'] > 0] 

# kontrol
print("\nKalan kolonlar:\n", df.columns)
print(f"\nkalan veri sayısı: {len(df)} ")

# kaydetme
df.to_csv("adim1_temizlenmis_veri.csv", index=False)