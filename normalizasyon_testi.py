import pandas as pd
import matplotlib.pyplot as plt
from tensorflow.keras.models import Model
from tensorflow.keras.layers import Input, Dense


df_ham = pd.read_csv("adim2_saglikli_egitim_verisi.csv")
X_ham = df_ham.values

df_normal = pd.read_csv("adim3_egitim_verisi.csv")
X_normal = df_normal.values

sensor_sayisi = X_ham.shape[1]


def derin_model_olustur():
    giris = Input(shape=(sensor_sayisi,))
    
    
    encoded1 = Dense(16, activation='relu')(giris)
    encoded2 = Dense(8, activation='relu')(encoded1)
    
    
    darbogaz = Dense(4, activation='relu')(encoded2)
    
    
    decoded1 = Dense(8, activation='relu')(darbogaz)
    decoded2 = Dense(16, activation='relu')(decoded1)
    
   
    cikis = Dense(sensor_sayisi, activation='linear')(decoded2)
    
    model = Model(inputs=giris, outputs=cikis)
    model.compile(optimizer='adam', loss='mse')
    return model

#
print("1. TEST: Ham Veri Asıl Modelde Eğitiliyor")
model_ham = derin_model_olustur()
gecmis_ham = model_ham.fit(X_ham, X_ham, epochs=100, batch_size=32, verbose=0)


print("2. TEST: Normalize Veri Asıl Modelde Eğitiliyor")
model_normal = derin_model_olustur()
gecmis_normal = model_normal.fit(X_normal, X_normal, epochs=100, batch_size=32, verbose=0)


plt.figure(figsize=(10, 6))

plt.plot(gecmis_ham.history['loss'], label='Ham Veri ', color='red', linewidth=2)
plt.plot(gecmis_normal.history['loss'], label='Normalize Veri ', color='green', linewidth=2)

plt.title('Derin Mimaride Normalizasyonun Etkisi', fontsize=14, fontweight='bold')
plt.xlabel('Eğitim Turu (Epoch)', fontsize=12)
plt.ylabel('Hata Payı (MSE Loss)', fontsize=12)
plt.legend(fontsize=12)
plt.grid(True, linestyle='--', alpha=0.7)

plt.savefig('Normalizasyon_Etkisi.png', dpi=300, bbox_inches='tight')

print("\nİŞLEM TAMAM! 'Normalizasyon_Etkisi.png' dosyası hazır")