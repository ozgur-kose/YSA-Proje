import pandas as pd
import sqlite3

print("📊 SERA OTOMASYON VERİTABANI RAPORU 📊\n")

try:
    
    baglanti = sqlite3.connect('sera_otomasyon.db')
    df = pd.read_sql_query("SELECT * FROM teshis_kayitlari", baglanti)
    baglanti.close()
    
    if len(df) == 0:
        print("Veritabanı şu an boş. Lütfen simülasyonu çalıştırıp veri yollayın.")
    else:
        print(f"Toplam İşlenen Kayıt Sayısı: {len(df)}\n")
        
        print("--- 🔴 EN SON GELEN 5 CANLI KAYIT ---")
        
        son_5 = df[['Tarih', 'Temperature', 'Humidity', 'Yapay_Zeka_Karari', 'Otonom_Karar']].tail(5)
        print(son_5.to_string(index=False))
        
        print("\n--- 📈 GENEL DURUM ÖZETİ (TÜM ZAMANLAR) ---")
        print(df['Yapay_Zeka_Karari'].value_counts().to_string())
        
        print("\n--- 🚜 OTONOM SİSTEM AKSİYONLARI ---")
        print(df['Otonom_Karar'].value_counts().to_string())
        
except sqlite3.OperationalError:
    print(" HATA: Veritabanı bulunamadı. Lütfen önce simülasyonu çalıştırın.")