import pandas as pd
import numpy as np
import tensorflow as tf
from tensorflow.keras.models import Model
from tensorflow.keras.layers import Input, Dense
from tensorflow.keras.callbacks import EarlyStopping

df_normalized = pd.read_csv("adim3_egitim_verisi.csv")
egitim_matrisi = df_normalized.values
sensor_sayisi = egitim_matrisi.shape[1] 

print(f"Sisteme giren sensör sayısı: {sensor_sayisi}")

# === YENİ VE DERİN AUTOENCODER MİMARİSİ ===
giris_katmani = Input(shape=(sensor_sayisi,))

# Encoder (Kademeli Sıkıştırma)
encoded1 = Dense(16, activation='relu')(giris_katmani)
encoded2 = Dense(8, activation='relu')(encoded1)

# Darboğaz (Bottleneck)
darbogaz = Dense(4, activation='relu')(encoded2)

# Decoder (Kademeli Geri Açma)
decoded1 = Dense(8, activation='relu')(darbogaz)
decoded2 = Dense(16, activation='relu')(decoded1)

# Çıkış Katmanı
cikis_katmani = Dense(sensor_sayisi, activation='sigmoid')(decoded2)

autoencoder = Model(inputs=giris_katmani, outputs=cikis_katmani)
autoencoder.summary()

autoencoder.compile(optimizer='adam', loss='mse')

# Aşırı öğrenmeyi (Overfitting) engellemek ve en iyi yerde durdurmak için:
erken_durma = EarlyStopping(monitor='val_loss', patience=10, restore_best_weights=True)

print("Yapay Zeka Eğitimi Başlıyor (Gelişmiş Mimari)...")

autoencoder.fit(
    x=egitim_matrisi, 
    y=egitim_matrisi, 
    epochs=100,         # Kademeli olduğu için epoch'u 100'e çıkardık
    batch_size=32,      
    shuffle=True,       
    validation_split=0.1,
    callbacks=[erken_durma] # Sistem 10 epoch boyunca gelişmezse otomatik durup en iyi hali kaydedecek
)

autoencoder.save("egitilmis_saglikli_sera_modeli.h5")
print("Yeni beyin fırından çıktı ve kaydedildi! 🚀")