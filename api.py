from fastapi import FastAPI
from pydantic import BaseModel
from Tahmin_Motoru import sera_durumunu_sorgula

app = FastAPI(title="Sera Yapay Zeka API", version="1.0")


son_gelen_veri = {
    "sensor_id": "Bağlantı Bekleniyor...",
    "temperature": 0.0,
    "humidity": 0.0,
    "moisture": 0.0,
    "pH": 0.0,
    "nitrogen": 0,
    "phosphorus": 0,
    "potassium": 0
}

class SensorVerisi(BaseModel):
    sensor_id: str = "Manuel"
    degerler: list[float]  

@app.post("/tahmin-yap")
def tahmin_istegi_karsila(veri: SensorVerisi):
    global son_gelen_veri 
    
    yapay_zeka_sonucu = sera_durumunu_sorgula(veri.degerler)
    
    if len(veri.degerler) >= 7:
        son_gelen_veri = {
            "sensor_id": veri.sensor_id,
            "temperature": round(veri.degerler[0], 2),
            "humidity": round(veri.degerler[1], 2),
            "moisture": round(veri.degerler[2], 2),
            "pH": round(veri.degerler[3], 2),
            "nitrogen": int(veri.degerler[4]),
            "phosphorus": int(veri.degerler[5]),
            "potassium": int(veri.degerler[6])
        }
        
    return yapay_zeka_sonucu

@app.get("/sensor-oku")
def son_sensor_verisini_getir():
    return son_gelen_veri

@app.get("/")
def ana_sayfa():
    return {"mesaj": "Sera Yapay Zeka Motoru Aktif Olarak Çalışıyor!"}