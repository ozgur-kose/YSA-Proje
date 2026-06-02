import pandas as pd
from sklearn.ensemble import IsolationForest
import joblib

print("⚙️ ADIM 3: Yapay Zeka Model Eğitimi Başlıyor (Denetimsiz Sistem)...")

df_train = pd.read_csv("adim2_egitim_verisi.csv")

X_train = df_train.drop(columns=['Label'])

print(" Isolation Forest (İzolasyon Ormanı) ağaçları büyüyor...")
if_model = IsolationForest(n_estimators=100, contamination=0.5, random_state=42)

if_model.fit(X_train)

joblib.dump(if_model, 'sera_if_model.pkl')
print("Model başarıyla eğitildi ve 'sera_if_model.pkl' olarak kaydedildi.\n")