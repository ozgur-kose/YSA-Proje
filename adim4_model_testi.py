import pandas as pd
import joblib
from sklearn.metrics import classification_report, accuracy_score, confusion_matrix, f1_score

print("ADIM 4: Model Sınavı ve Gelişmiş Akademik Rapor (Denetimsiz Sistem)...\n")


df_test = pd.read_csv("adim2_test_verisi.csv")
X_test = df_test.drop(columns=['Label'])


y_test_raw = df_test['Label']


y_test = y_test_raw.map({0: 1, 1: -1})


model = joblib.load('sera_if_model.pkl')


y_pred = model.predict(X_test)


f1_skoru = f1_score(y_test, y_pred, average='weighted')

print("="*55)
print(f"NİHAİ TEST BAŞARISI (Accuracy) : % {accuracy_score(y_test, y_pred)*100:.2f}")
print(f"F1 Skoru (Ağırlıklı)           : {f1_skoru:.4f}")
print("="*55)

print("\AYRINTILI KARNE:")
print(classification_report(y_test, y_pred, target_names=['Hasta/Anomali (-1)', 'Sağlıklı (1)']))

print("\Kafa Karışıklığı Matrisi (Confusion Matrix):")
print(confusion_matrix(y_test, y_pred))