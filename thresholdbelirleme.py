import pandas as pd
import numpy as np
from tensorflow.keras.models import load_model
import joblib


print("Dosyalar yükleniyor...")


model = load_model('egitilmis_saglikli_sera_modeli.h5', compile=False)
scaler = joblib.load('sensor_scaler.pkl')
train_data = pd.read_csv('adim3_egitim_verisi.csv')

print("Tahminler yapılıyor...")
reconstructions = model.predict(train_data)


train_loss = np.mean(np.abs(reconstructions - train_data), axis=1)

threshold = np.mean(train_loss) + np.std(train_loss)

print("-" * 30)
print(f"Ortalama Hata: {np.mean(train_loss):.4f}")
print(f" Standart Sapma: {np.std(train_loss):.4f}")
print(f" ÖNERİLEN THRESHOLD (EŞİK): {threshold:.4f}")
print("-" * 30)