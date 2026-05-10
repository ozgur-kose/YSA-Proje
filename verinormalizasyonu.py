import pandas as pd
from sklearn.preprocessing import MinMaxScaler
from sklearn.model_selection import train_test_split
import joblib
import os

input_file = "adim2_saglikli_egitim_verisi.csv"

if os.path.exists(input_file):
    df = pd.read_csv(input_file)
    print(f"'{input_file}' başarıyla yüklendi. İşlem başlıyor...")
else:
    print(f"HATA: '{input_file}' bulunamadı!")
    exit()

# min-max normalizasyonu 
scaler = MinMaxScaler(feature_range=(0, 1))
df_normalized = pd.DataFrame(scaler.fit_transform(df), columns=df.columns)

print("Veriler 0-1 arasına sıkıştırıldı.")

# verinin %20'sini test için ayırıyoruz, geri kalan %80 eğitim için kalacak
test_orani = 0.2 
train_df, test_df = train_test_split(df_normalized, test_size=test_orani, random_state=42)

print(f"Veri bölme tamamlandı: %{int((1-test_orani)*100)} Eğitim, %{int(test_orani*100)} Test.")

# Backend ekibi canlı veride kullanacak
joblib.dump(scaler, 'sensor_scaler.pkl')


train_df.to_csv("adim3_egitim_verisi.csv", index=False)
test_df.to_csv("adim3_test_verisi.csv", index=False)

print("\n--- İŞLEM BAŞARIYLA TAMAMLANDI ---")
print("Oluşturulan dosyalar:")
print("- sensor_scaler.pkl (Ölçekleyici)")
print("- adim3_egitim_verisi.csv ")
print("- adim3_test_verisi.csv ")