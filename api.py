from fastapi import FastAPI
from pydantic import BaseModel
from Tahmin_Motoru import sera_durumunu_sorgula

from veritabani_olustur import canli_veriyi_veritabanina_yaz

app = FastAPI(title="Sera Yapay Zeka API", version="1.0")

class SensorVerisi(BaseModel):
    degerler: list[float]  

@app.post("/tahmin-yap")
def tahmin_istegi_karsila(veri: SensorVerisi):
    yapay_zeka_sonucu = sera_durumunu_sorgula(veri.degerler)
    
    if "hata" not in yapay_zeka_sonucu:
        canli_veriyi_veritabanina_yaz(veri.degerler, yapay_zeka_sonucu)
            
    return yapay_zeka_sonucu

@app.get("/")
def ana_sayfa():
    return {"mesaj": "Sera Yapay Zeka Motoru Aktif Olarak Çalışıyor!"}