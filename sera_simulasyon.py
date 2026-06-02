import pandas as pd
import requests
import time

API_URL = "http://127.0.0.1:8000/tahmin-yap"

print("sera başlatılıyor..")

try:
    df = pd.read_csv("deneme.csv")
except FileNotFoundError:
    print("🚨 HATA: 'deneme.csv' bulunamadı!")
    exit()

# bakılacak kolonlar
sensor_kolonlari = ['Temperature', 'Humidity', 'Moisture', 'pH', 'N', 'P', 'K']

# bitkilerin konum bilgileri
gerekli_kolonlar = ['Zone_ID', 'Current_Node'] + sensor_kolonlari

# rastgele 50 satır seçelim
df_sensor = df[gerekli_kolonlar].sample(50, random_state=42)

print("\nsera canlı veri akışı başlatılıyor..\n")

for index, row in df_sensor.iterrows():
    
    # konum bilgilerini alma
    zone = str(row['Zone_ID'])
    node = str(row['Current_Node'])
    gercek_konum = f"{zone}-{node}" # Örn: Zone_4-Node_12
    
    # 7 veriyi listeye çevirme
    sensor_degerleri = row[sensor_kolonlari].tolist()
    
    # paketle ve apiye gönder
    gonderilecek_paket = {
        "sensor_id": gercek_konum,
        "degerler": sensor_degerleri
    }
    
    try:
        cevap = requests.post(API_URL, json=gonderilecek_paket)
        
        if cevap.status_code == 200:
            sonuc = cevap.json()
            
            # kontrol için konsola yazdırma
            print(f"[ {gercek_konum}] T:{sensor_degerleri[0]:.1f}°C, Nem:%{sensor_degerleri[1]:.0f}, Su:%{sensor_degerleri[2]:.0f}, pH:{sensor_degerleri[3]:.1f}, NPK:[{sensor_degerleri[4]:.0f}, {sensor_degerleri[5]:.0f}, {sensor_degerleri[6]:.0f}]")
            print(f"🤖 Yapay Zeka: {sonuc.get('durum', 'Bilinmiyor')} (Güven: {sonuc.get('model_guven_skoru', '%0')})")
            
            # bitki hasta ise ne yapılması gerektiği bilgisi de gelmesi
            if "ANOMALİ" in sonuc.get('durum', ''):
                print(f"    Teşhis: {sonuc.get('teshis', 'Sistem Analizi Yapılamadı')}")
                print(f"    Çözüm:  {sonuc.get('aksiyon', 'Manuel Kontrol')}")
                
        else:
            print(f"[HATA] API Hatası! Code: {cevap.status_code}")
            
        print("-" * 75)
        
    except requests.exceptions.ConnectionError:
        print(f"[HATA] API'ye ulaşılamıyor! Lütfen önce arka planda Uvicorn ile API'yi çalıştırın.")
        break
        
    # 5 saniye bekleme
    time.sleep(5)