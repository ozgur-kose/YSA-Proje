import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import MinMaxScaler 
import joblib

print("ADIM 2: Veri Bölme ve Normalizasyon Başlıyor...")

df = pd.read_csv("adim1_cikis_verisi.csv")

X = df[['Temperature', 'Humidity', 'Moisture', 'pH', 'N', 'P', 'K']]
y = df['Label']

# Veriyi %80 Eğitim, %20 Test olarak ayır
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# normalizasyon
scaler = MinMaxScaler() 
X_train_scaled = scaler.fit_transform(X_train)
X_test_scaled = scaler.transform(X_test)

# API için scaler'ı kaydet
joblib.dump(scaler, 'sensor_scaler.pkl')


pd.DataFrame(X_train_scaled, columns=X.columns).assign(Label=y_train.values).to_csv("adim2_egitim_verisi.csv", index=False)
pd.DataFrame(X_test_scaled, columns=X.columns).assign(Label=y_test.values).to_csv("adim2_test_verisi.csv", index=False)

print("Normalizasyon tamamlandı ('sensor_scaler.pkl' güncellendi).\n")