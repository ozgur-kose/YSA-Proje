import pandas as pd
import sqlite3
from datetime import datetime, timedelta

print("Veritabanı ve Otonom Karar Mekanizması başlatılıyor")


df = pd.read_csv('final_teshis_raporu.csv')


simdi = datetime.now()
tarihler = [(simdi - timedelta(minutes=i)).strftime('%Y-%m-%d %H:%M:%S') for i in range(len(df))]
tarihler.reverse() # Kronolojik sıra için listeyi ters çeviriyoruz
df['Tarih'] = tarihler


def otonom_karar_motoru(row):

    satir_metni = str(row.values).lower()
    if 'anomali' in satir_metni or 'anomaly' in satir_metni or 'stres' in satir_metni:
        return "Stres Algılandı: Sulamayı Başlat & Havalandırmayı Aç!"
    else:
        return "Sistem Stabil: Rutin Kontrol"


df['Otonom_Karar'] = df.apply(otonom_karar_motoru, axis=1)


baglanti = sqlite3.connect('sera_otomasyon.db')
df.to_sql('teshis_kayitlari', baglanti, if_exists='replace', index=False)

print("-" * 60)
print("BAŞARILI: 'sera_otomasyon.db' otonom sistem gereksinimleriyle güncellendi!")
print(f"Toplam {len(df)} adet sensör kaydı başarıyla loglandı.")
print(" Hafıza Sistemi: Tüm kayıtlara geçmişe dönük gerçekçi 'Tarih' damgaları basıldı.")
print(" Otonom Karar: Anomali anları için 'Sulamayı Başlat' komutları veritabanına işlendi.")
print("-" * 60)

baglanti.close()