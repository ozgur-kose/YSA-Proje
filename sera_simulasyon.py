import pandas as pd
import requests
import time

API_URL = "http://127.0.0.1:8000/tahmin-yap"

print("Gizli kasa (test) verileri yükleniyor...")
# okunmasini istedigimiz veri seti
df = pd.read_csv("gizli_kasa_test_verisi.csv")

# okunmasi gereken kolonlar
hayati_kolonlar = ['N', 'P', 'K', 'Moisture', 'pH', 'Temperature', 'Humidity']
df_sensor = df[hayati_kolonlar]

print("\n--- SERA CANLI VERİ AKIŞI BAŞLATILDI ---")


# verideki her satiri tek tek apiye gonderme
for index, row in df_sensor.iterrows():
    
    # Satırdaki verileri Python listesine çevir
    sensor_degerleri = row.tolist()
    
    # json formati paketi hazirla
    gonderilecek_paket = {
        "degerler": sensor_degerleri
    }
    
    try:
        # apiye gonder 
        cevap = requests.post(API_URL, json=gonderilecek_paket)
        
        # sonuca bak
        print(f"[Zaman: T+{index} sn] Sensör: {sensor_degerleri}")
        print(f"Yapay Zeka: {cevap.json()}\n")
        
    except requests.exceptions.ConnectionError:
        print(f"[HATA] API'ye ulaşılamıyor!")
        print("Lütfen önce arka planda API'yi çalıştırın.")
        break
        
    # yavas akmasi icin 1 saniye bekle
    time.sleep(1)