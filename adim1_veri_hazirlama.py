import pandas as pd
import numpy as np

df = pd.read_csv("AgricultureDataset.csv", encoding="cp1254", sep=";")

gereksiz_kolonlar = ['Image_Source_ID', 'Image_Type', 'UAV_Timestamp', 'Energy_Consumed_mAh', 'Latency_ms', 'Migrated_To', 'Migration_Required', 'Migration_Timestamp', 'NDVI','NDRE','RGB_Damage_Score','NDI_Label','PDI_Label']
df = df.drop(columns=gereksiz_kolonlar, errors='ignore')

if df['Temperature'].dtype == object:
    df['Temperature'] = df['Temperature'].replace(',', '.', regex=True).astype(float)

hayati_kolonlar = ['Temperature', 'Humidity', 'Moisture', 'pH', 'N', 'P', 'K','Zone_ID','Current_Node']
df = df.dropna(subset=hayati_kolonlar)
df = df.fillna(0)
df = df[df['Temperature'] > 0] 

# sağlıklı 0 hasta 1
df_saglikli = df.copy()
df_saglikli['Label'] = 0

df_hasta = df.copy()
satir = len(df_hasta)

npk_carpan = np.where(np.random.rand(satir) > 0.5, np.random.uniform(0.4, 0.6, satir), np.random.uniform(1.4, 1.6, satir))  
df_hasta['N'] = df_hasta['N'] * npk_carpan
df_hasta['P'] = df_hasta['P'] * npk_carpan
df_hasta['K'] = df_hasta['K'] * npk_carpan

temp_carpan = np.where(np.random.rand(satir) > 0.5, np.random.uniform(0.7, 0.85, satir), np.random.uniform(1.15, 1.3, satir))
df_hasta['Temperature'] = df_hasta['Temperature'] * temp_carpan

nem_carpan = np.where(np.random.rand(satir) > 0.5, np.random.uniform(0.5, 0.7, satir), np.random.uniform(1.3, 1.5, satir))
df_hasta['Humidity'] = df_hasta['Humidity'] * nem_carpan
df_hasta['Moisture'] = df_hasta['Moisture'] * nem_carpan

ph_fark = np.where(np.random.rand(satir) > 0.5, np.random.uniform(-1.8, -1.2, satir), np.random.uniform(1.2, 1.8, satir))
df_hasta['pH'] = df_hasta['pH'] + ph_fark
df_hasta['Label'] = 1

# birleştirme ve kaydetme
df_final = pd.concat([df_saglikli, df_hasta], ignore_index=True).sample(frac=1, random_state=42).reset_index(drop=True)
df_final.to_csv("deneme.csv", index=False)
print(f"{len(df_final)} satırlık veri kaydedildi.\n")