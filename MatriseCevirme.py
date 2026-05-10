import pandas as pd
import numpy as np
import tensorflow as tf
from tensorflow.keras.models import Model
from tensorflow.keras.layers import Input, Dense

df_normalized = pd.read_csv("adim3_egitim_verisi.csv")

egitim_matrisi = df_normalized.values

sensor_sayisi = egitim_matrisi.shape[1] 
print(f"Sisteme giren sensör sayısı: {sensor_sayisi}")

giris_katmani = Input(shape=(sensor_sayisi,))

darbogaz = Dense(3, activation='relu')(giris_katmani)

cikis_katmani = Dense(sensor_sayisi, activation='sigmoid')(darbogaz)

autoencoder = Model(inputs=giris_katmani, outputs=cikis_katmani)

autoencoder.summary()
autoencoder.compile(optimizer='adam', loss='mse')

print("Yapay Zeka Eğitimi Başlıyor...")

autoencoder.fit(
    x=egitim_matrisi, 
    y=egitim_matrisi, 
    epochs=50,          
    batch_size=32,      
    shuffle=True,       
    validation_split=0.1 
)
autoencoder.save("egitilmis_saglikli_sera_modeli.h5")
