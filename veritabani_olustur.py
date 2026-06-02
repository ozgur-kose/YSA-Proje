import pandas as pd
import sqlite3
from datetime import datetime, timedelta


def canli_veriyi_veritabanina_yaz(sensor_degerleri, yz_sonucu):
    
    
    durum = yz_sonucu.get("durum", "SAĞLIKLI")
    guven = yz_sonucu.get("model_guven_skoru", "% 0.00")
    
    if 'ANOMALİ' in durum or 'HASTA' in durum:
        karar = "Stres Algılandı: Sulamayı Başlat & Havalandırmayı Aç!"
    else:
        karar = "Sistem Stabil: Rutin Kontrol"

    
    kolon_isimleri = ['Temperature', 'Humidity', 'Moisture', 'pH', 'N', 'P', 'K'] 
    yeni_veri = pd.DataFrame([sensor_degerleri], columns=kolon_isimleri) 
    
    yeni_veri['Tarih'] = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    yeni_veri['Yapay_Zeka_Karari'] = durum
    yeni_veri['Guven_Skoru'] = guven
    yeni_veri['Otonom_Karar'] = karar

    
    baglanti = sqlite3.connect('sera_otomasyon.db')
    yeni_veri.to_sql('teshis_kayitlari', baglanti, if_exists='append', index=False)
    baglanti.close()
    
    print(f"💾 DB'ye İşlendi: {durum} | {karar}")


if __name__ == "__main__":
    print("Veritabanı ve Otonom Karar Mekanizması Sıfırdan Kuruluyor...")
    try:
        
        df = pd.read_csv('adim1_cikis_verisi.csv').sample(50, random_state=42)
        
        simdi = datetime.now()
        tarihler = [(simdi - timedelta(minutes=i)).strftime('%Y-%m-%d %H:%M:%S') for i in range(len(df))]
        tarihler.reverse() 
        df['Tarih'] = tarihler

        
        def gecmis_veriyi_doldur(row):
            if row['Label'] == 1:
                return pd.Series(["HASTA / ANOMALİ", "% 96.50", "Stres Algılandı: Sulamayı Başlat & Havalandırmayı Aç!"])
            else:
                return pd.Series(["SAĞLIKLI", "% 99.00", "Sistem Stabil: Rutin Kontrol"])

        df[['Yapay_Zeka_Karari', 'Guven_Skoru', 'Otonom_Karar']] = df.apply(gecmis_veriyi_doldur, axis=1)
        df = df.drop(columns=['Label'])

        baglanti = sqlite3.connect('sera_otomasyon.db')
        df.to_sql('teshis_kayitlari', baglanti, if_exists='replace', index=False)
        baglanti.close()

        print("-" * 60)
        print(" BAŞARILI: 'sera_otomasyon.db' sıfırdan kuruldu ve geçmiş verilerle beslendi!")
        print("-" * 60)
    except FileNotFoundError:
        print(" HATA: Önce 'adim1_veri_hazirlama.py' çalıştırılmalı!")