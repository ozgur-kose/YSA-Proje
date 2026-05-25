import numpy as np
from tensorflow.keras.models import load_model
import joblib


MODEL_YOLU = 'egitilmis_saglikli_sera_modeli.h5'
SCALER_YOLU = 'sensor_scaler.pkl'

THRESHOLD = 0.2361

print("Yapay Zeka Beyni ve Ölçüm Cetveli yükleniyor...")
model = load_model(MODEL_YOLU, compile=False)
scaler = joblib.load(SCALER_YOLU)
print("Sistem Başarıyla Ayaklandı ve Veri Bekliyor! 🚀")


def sera_durumunu_sorgula(anlik_sensor_verileri):
    """
    anlik_sensor_verileri: Sensörlerden gelen sayıların listesi.
    Örnek: [Sıcaklık, Nem, Işık, CO2, vs...]
    """
    try:
        veri_matrisi = np.array(anlik_sensor_verileri).reshape(1, -1)

        olcekli_veri = scaler.transform(veri_matrisi)

        yeniden_olusturulan = model.predict(olcekli_veri, verbose=0)


        hata_payi = np.mean(np.square(olcekli_veri - yeniden_olusturulan))

        if hata_payi > THRESHOLD:
            durum = "HASTA / ANOMALİ"
            risk = "YÜKSEK RİSK"
        else:
            durum = "SAĞLIKLI"
            risk = "DÜŞÜK RİSK"

        return {
            "durum": durum,
            "hata_payi": round(float(hata_payi), 5), 
            "risk_seviyesi": risk,
            "esik_deger": THRESHOLD
        }

    except Exception as e:
        return {"hata": f"İşlem sırasında bir sorun oluştu: {str(e)}"}


if __name__ == "__main__":
 
    ornek_gelen_veri = [80, 45, 30, 25.5, 60.2, 6.5, 40] 
    
    print("\n--- Canlı Veri Simülasyonu Başlıyor ---")
    sonuc = sera_durumunu_sorgula(ornek_gelen_veri)
    
    print("\n--- YAPAY ZEKA RAPORU ---")
    for anahtar, deger in sonuc.items():
        print(f"{anahtar.upper()}: {deger}")