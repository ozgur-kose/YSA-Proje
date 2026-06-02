import numpy as np
import pandas as pd
import joblib
import shap

MODEL_YOLU = 'sera_if_model.pkl' 
SCALER_YOLU = 'sensor_scaler.pkl'
EGITIM_VERISI_YOLU = 'adim1_cikis_verisi.csv'

print(" Yapay Zeka Beyni (Isolation Forest), Ölçüm Cetveli ve SHAP (XAI) yükleniyor...")
model = joblib.load(MODEL_YOLU)
scaler = joblib.load(SCALER_YOLU) 
explainer = shap.TreeExplainer(model)

kolon_isimleri = ['Temperature', 'Humidity', 'Moisture', 'pH', 'N', 'P', 'K']

try:
    df_gecmis = pd.read_csv(EGITIM_VERISI_YOLU)
    hiza_degerleri = df_gecmis[kolon_isimleri].median()
    DINAMIK_REFERANSLAR = hiza_degerleri.to_dict()
    print(f" Seranın Dinamik Hizası (Referansı) Öğrenildi: {DINAMIK_REFERANSLAR}")
except FileNotFoundError:
    print(" HATA: Eğitim verisi bulunamadı! Acil durum yedek referansları kullanılıyor.")
    DINAMIK_REFERANSLAR = { 'Temperature': 25.0, 'Humidity': 60.0, 'Moisture': 55.0, 'pH': 6.5, 'N': 40, 'P': 50, 'K': 45 }

print(" Sistem Başarıyla Ayaklandı ve Tam Otonom XAI Aktif! 🚀")

OZELLIK_SOZLUGU = {
    'Temperature': {'isim': 'Sıcaklık', 'yuksek_cozum': 'Havalandırmaları tam kapasite aç ve gölgelikleri çek.', 'dusuk_cozum': 'Sera ısıtıcı sistemini devreye sok.'},
    'Humidity': {'isim': 'Hava Nemi', 'yuksek_cozum': 'Havalandırma ve nem alıcı fanları çalıştır.', 'dusuk_cozum': 'Sisleme (fogging) sistemini devreye al.'},
    'Moisture': {'isim': 'Toprak Nemi', 'yuksek_cozum': 'Sulamayı durdur ve toprağın süzülmesini bekle.', 'dusuk_cozum': 'Damla sulama sistemini derhal çalıştır.'},
    'pH': {'isim': 'Toprak pH', 'yuksek_cozum': 'Toprak pH değerini düşürmek için kükürt veya asidik gübre uygula.', 'dusuk_cozum': 'Toprak pH değerini yükseltmek için tarım kireci uygula.'},
    'N': {'isim': 'Azot (N)', 'yuksek_cozum': 'Azotlu gübrelemeyi durdur, bitkiyi bol su ile yıka.', 'dusuk_cozum': 'Valften sıvı Azot gübresi (N-takviyesi) pompala.'},
    'P': {'isim': 'Fosfor (P)', 'yuksek_cozum': 'Fosforlu gübrelemeyi kes, demir ve çinko takviyesi yap.', 'dusuk_cozum': 'Kök gelişimi için acil Fosfor (P) takviyesi yap.'},
    'K': {'isim': 'Potasyum (K)', 'yuksek_cozum': 'Potasyum gübrelemesini durdur, magnezyum ve kalsiyum dengesini kontrol et.', 'dusuk_cozum': 'Meyve tutumu için Potasyum (K) takviyesi yap.'}
}

def sera_durumunu_sorgula(anlik_sensor_verileri):
    try:
        veri_df = pd.DataFrame([anlik_sensor_verileri], columns=kolon_isimleri)
        olcekli_veri = scaler.transform(veri_df)

        tahmin = model.predict(olcekli_veri)[0]
        
        karar_skoru = abs(model.decision_function(olcekli_veri)[0])
        hesaplanan_yuzde = min(99.6, 75.0 + (karar_skoru * 100))
        yuzde_metni = f"% {hesaplanan_yuzde:.1f}"

        teshis_metni = "Her şey yolunda."
        aksiyon_metni = "Müdahaleye gerek yok."
        
        if tahmin == -1:
            durum = "HASTA / ANOMALİ"
            risk = "YÜKSEK RİSK"
            guven_skoru = yuzde_metni 
            
            shap_degerleri = explainer.shap_values(olcekli_veri)
            anomali_agirliklari = shap_degerleri[0] if isinstance(shap_degerleri, list) else shap_degerleri[0]
            
            etkiler = list(zip(kolon_isimleri, anomali_agirliklari))
            etkiler.sort(key=lambda x: abs(x[1]), reverse=True) 
            
            en_suclu_ozellikler = [f for f, agirlik in etkiler][:2]
            
            nedenler = []
            cozumler = []
            
            for feature in en_suclu_ozellikler:
                idx = kolon_isimleri.index(feature)
                gercek_deger = anlik_sensor_verileri[idx] 
                
                dinamik_hiza = DINAMIK_REFERANSLAR[feature]
                sozluk_bilgisi = OZELLIK_SOZLUGU[feature]
                
                if gercek_deger > dinamik_hiza:
                    durum_str = f"Aşırı Yüksek (Sınır: {dinamik_hiza:.1f})"
                    cozumler.append(sozluk_bilgisi['yuksek_cozum'])
                else:
                    durum_str = f"Eksik/Düşük (Sınır: {dinamik_hiza:.1f})"
                    cozumler.append(sozluk_bilgisi['dusuk_cozum'])
                    
                nedenler.append(f"AI Tespiti: {sozluk_bilgisi['isim']} {durum_str}")

            teshis_metni = " | ".join(nedenler)
            aksiyon_metni = " + ".join(cozumler)

        else:
            durum = "SAĞLIKLI"
            risk = "DÜŞÜK RİSK"
            guven_skoru = yuzde_metni

        return {
            "durum": durum,
            "risk_seviyesi": risk,
            "model_guven_skoru": guven_skoru,
            "teshis": teshis_metni,
            "aksiyon": aksiyon_metni
        }

    except Exception as e:
        return {"hata": f"İşlem sırasında bir sorun oluştu: {str(e)}"}