import pandas as pd
import os

dosya_adi = "adim1_temizlenmis_veri.csv"

if os.path.exists(dosya_adi):
    df = pd.read_csv(dosya_adi)
    print(f"'{dosya_adi}' başarıyla yüklendi!")
else:
    print(f"HATA: '{dosya_adi}' dosyası klasörde bulunamadı.")
    exit()

# içinde defiiciency ya da pest geçiyosa 1 = hasta değilse 0 = sağlıklı
def durum_belirle(tag):
    if 'deficiency' in str(tag) or 'Pest' in str(tag):
        return 1
    else:
        return 0

# label kolonu oluşturma 
df['Label'] = df['Semantic_Tag'].apply(durum_belirle)

# sağlıklı ve hasya veri ayırma 
saglikli_veri = df[df['Label'] == 0]
hasta_veri = df[df['Label'] == 1]

print(f"İşlem Özeti:")
print(f"- Sağlıklı Satır Sayısı: {len(saglikli_veri)}")
print(f"- Hasta Satır Sayısı: {len(hasta_veri)}")


kolonlari_sil = ['Semantic_Tag', 'Action_Suggested', 'Label']
saglikli_egitim_verisi = saglikli_veri.drop(columns=kolonlari_sil)


saglikli_egitim_verisi.to_csv("adim2_saglikli_egitim_verisi.csv", index=False)

 
hasta_veri.to_csv("gizli_kasa_test_verisi.csv", index=False)

print("\nGümrük işlemi kusursuz tamamlandı! Dosyalar oluşturuldu.")