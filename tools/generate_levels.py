#!/usr/bin/env python3
"""Membuat app/src/main/assets/levels.json dari daftar kata dan petunjuk.

Setiap level disusun dengan pencarian acak yang bisa diulang (seed tetap),
lalu dicek dengan aturan di validate_levels.py. Hasil selalu bisa dibaca ulang
oleh game dan lolos validasi.

Pemakaian: python3 tools/generate_levels.py
"""
import json
import os
import random
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from validate_levels import build, find_problem  # noqa: E402

# Setiap level: paket (indonesia / internasional), tingkat kesulitan, batas ukuran papan, dan kata.
# Kesulitan naik bertahap: lebih banyak kata, kata lebih panjang, petunjuk lebih sulit.
LEVELS = [
    {"pack": "indonesia", "difficulty": "Mudah", "title": "Ruang Tamu", "side": 12, "words": [
        ("BUKU", "Kumpulan lembar kertas yang dijilid"),
        ("MEJA", "Tempat meletakkan barang di ruangan"),
        ("KURSI", "Tempat duduk dengan sandaran"),
        ("PINTU", "Jalan masuk dan keluar ruangan"),
        ("ATAP", "Penutup bagian atas rumah"),
        ("LANTAI", "Dasar tempat kita berpijak di rumah"),
        ("JENDELA", "Bukaan di dinding untuk cahaya dan udara"),
    ]},
    {"pack": "indonesia", "difficulty": "Mudah", "title": "Pasar Pagi", "side": 12, "words": [
        ("PASAR", "Tempat jual beli barang"),
        ("BAYAR", "Memberi uang untuk membeli"),
        ("TIMBANGAN", "Alat untuk menimbang barang"),
        ("SAYUR", "Bahan masakan hijau dari kebun"),
        ("IKAN", "Hewan air yang sering digoreng"),
        ("BELI", "Mendapat barang dengan membayar"),
        ("KEMBALIAN", "Uang sisa setelah membayar"),
    ]},
    {"pack": "indonesia", "difficulty": "Mudah", "title": "Jalan Raya", "side": 12, "words": [
        ("MOBIL", "Kendaraan roda empat untuk keluarga"),
        ("MOTOR", "Kendaraan roda dua bermesin"),
        ("BUS", "Kendaraan besar untuk banyak penumpang"),
        ("LAMPU", "Penerang lalu lintas di persimpangan"),
        ("PARKIR", "Menghentikan kendaraan untuk sementara"),
        ("SUPIR", "Orang yang mengemudikan kendaraan"),
        ("HALTE", "Tempat menunggu bus"),
    ]},
    {"pack": "indonesia", "difficulty": "Sedang", "title": "Dapur Ibu", "side": 13, "words": [
        ("SAPU", "Alat untuk membersihkan lantai"),
        ("SETRIKA", "Alat pelicin pakaian yang panas"),
        ("KIPAS", "Alat peniup angin saat gerah"),
        ("KULKAS", "Lemari dingin untuk menyimpan makanan"),
        ("PANCI", "Wadah memasak berbentuk bulat"),
        ("PIRING", "Tempat menaruh lauk saat makan"),
        ("GELAS", "Wadah untuk minum"),
        ("SENDOK", "Alat makan berbentuk cekung"),
    ]},
    {"pack": "indonesia", "difficulty": "Sedang", "title": "Langit Nusantara", "side": 13, "words": [
        ("HUJAN", "Air yang turun dari langit"),
        ("PELANGI", "Lengkung warna setelah hujan dan cahaya"),
        ("KABUT", "Uap tipis yang menutup pandangan"),
        ("ANGIN", "Udara yang bergerak"),
        ("PETIR", "Kilatan cahaya saat badai"),
        ("BADAI", "Cuaca buruk dengan angin kencang"),
        ("KEMARAU", "Musim tanpa hujan"),
        ("MENDUNG", "Langit tertutup awan gelap"),
        ("EMBUN", "Titik air pagi di dedaunan"),
    ]},
    {"pack": "indonesia", "difficulty": "Sedang", "title": "Profesi", "side": 13, "words": [
        ("DOKTER", "Tenaga medis yang mengobati pasien"),
        ("PERAWAT", "Membantu dokter merawat pasien"),
        ("PETANI", "Bekerja mengolah sawah atau ladang"),
        ("NELAYAN", "Mencari ikan di laut atau sungai"),
        ("PILOT", "Menerbangkan pesawat"),
        ("POLISI", "Menjaga keamanan masyarakat"),
        ("DOSEN", "Pengajar di perguruan tinggi"),
        ("SOPIR", "Mengemudikan angkutan"),
        ("PENULIS", "Menyusun buku atau artikel"),
    ]},
    {"pack": "indonesia", "difficulty": "Sulit", "title": "Tubuh Kita", "side": 14, "words": [
        ("JANTUNG", "Organ pemompa darah"),
        ("PARU", "Organ tempat pertukaran oksigen"),
        ("GINJAL", "Organ penyaring darah dan urine"),
        ("HATI", "Organ terbesar kedua di dalam tubuh"),
        ("OTAK", "Pusat kendali seluruh tubuh"),
        ("LAMBUNG", "Kantong pencernaan di perut"),
        ("TULANG", "Rangka keras pembentuk tubuh"),
        ("OTOT", "Jaringan yang membuat tubuh bergerak"),
        ("KULIT", "Lapisan terluar pelindung tubuh"),
        ("DARAH", "Cairan merah yang mengalir di nadi"),
    ]},
    {"pack": "indonesia", "difficulty": "Sulit", "title": "Kerajaan Nusantara", "side": 14, "words": [
        ("SRIWIJAYA", "Kerajaan maritim berpusat di Palembang"),
        ("MAJAPAHIT", "Kerajaan Hindu-Buddha terbesar di Jawa Timur"),
        ("DEMAK", "Kerajaan Islam pertama di Pulau Jawa"),
        ("MATARAM", "Kerajaan agraris pembangun Borobudur"),
        ("PAJAJARAN", "Kerajaan Sunda di Jawa Barat"),
        ("TARUMANEGARA", "Kerajaan tertua di Jawa Barat"),
        ("TERNATE", "Kesultanan di Maluku Utara"),
        ("KUTAI", "Kerajaan tertua di Indonesia dari Kalimantan"),
        ("BANTEN", "Kesultanan di ujung barat Jawa"),
    ]},
    {"pack": "indonesia", "difficulty": "Ahli", "title": "Ilmu Alam", "side": 15, "words": [
        ("FOTOSINTESIS", "Proses tumbuhan membuat makanan dari cahaya"),
        ("GRAVITASI", "Gaya tarik antara benda bermassa"),
        ("ATOM", "Satuan terkecil unsur kimia"),
        ("MOLEKUL", "Gabungan beberapa atom"),
        ("EVOLUSI", "Perubahan makhluk hidup dalam waktu lama"),
        ("KLOROFIL", "Pigmen hijau pada daun"),
        ("ENERGI", "Kemampuan untuk melakukan kerja"),
        ("MAGNET", "Benda yang menarik besi"),
        ("ELEKTRON", "Partikel bermuatan negatif"),
        ("OKSIGEN", "Gas yang dibutuhkan untuk bernapas"),
    ]},
    {"pack": "indonesia", "difficulty": "Ahli", "title": "Budaya & Alam", "side": 15, "words": [
        ("KOMODO", "Kadal raksasa dari Nusa Tenggara Timur"),
        ("BOROBUDUR", "Candi Buddha terbesar di dunia"),
        ("PRAMBANAN", "Candi Hindu berbentuk menjulang di Yogyakarta"),
        ("MERAPI", "Gunung api paling aktif di Jawa Tengah"),
        ("TOBA", "Danau vulkanik terbesar di Indonesia"),
        ("RAJAAMPAT", "Gugusan pulau dengan terumbu karang terkaya"),
        ("WAYANG", "Pertunjukan boneka kulit khas Jawa"),
        ("ANGKLUNG", "Alat musik bambu dari Jawa Barat"),
        ("KERIS", "Senjata tradisional berbilah berkelok"),
        ("BATIK", "Kain bermotif hasil lilin dan canting"),
    ]},
    {"pack": "internasional", "difficulty": "Mudah", "title": "Animals", "side": 12, "words": [
        ("HORSE", "Animal you can ride"),
        ("TIGER", "Striped big cat"),
        ("ZEBRA", "Striped horse-like animal"),
        ("EAGLE", "Bird of prey"),
        ("RABBIT", "Animal with long ears"),
        ("SNAKE", "Animal that slithers"),
        ("TURTLE", "Animal with a shell"),
    ]},
    {"pack": "internasional", "difficulty": "Mudah", "title": "Fruits", "side": 12, "words": [
        ("APPLE", "Fruit that keeps the doctor away"),
        ("MANGO", "Tropical fruit of the sunny days"),
        ("GRAPE", "Small fruit that makes wine"),
        ("LEMON", "Sour yellow citrus"),
        ("PEACH", "Fuzzy stone fruit"),
        ("BERRY", "Small juicy fruit like a strawberry"),
        ("MELON", "Big fruit with a green rind"),
    ]},
    {"pack": "internasional", "difficulty": "Sedang", "title": "Space", "side": 13, "words": [
        ("PLANET", "Earth is one"),
        ("COMET", "Icy body with a glowing tail"),
        ("ORBIT", "Path around a star or planet"),
        ("MOON", "Earth's natural satellite"),
        ("STAR", "Sun is one of these"),
        ("ROCKET", "Vehicle that launches into orbit"),
        ("GALAXY", "Huge group of stars"),
        ("COSMOS", "The universe as a whole"),
    ]},
    {"pack": "internasional", "difficulty": "Sedang", "title": "Kitchen", "side": 13, "words": [
        ("OVEN", "Appliance for baking"),
        ("SPOON", "Utensil with a round bowl"),
        ("KNIFE", "Sharp tool for cutting"),
        ("PLATE", "Flat dish for serving food"),
        ("BOWL", "Deep dish for soup"),
        ("TOASTER", "Machine that browns bread"),
        ("FRIDGE", "Cold cabinet for food"),
        ("SINK", "Basin with a faucet"),
    ]},
    {"pack": "internasional", "difficulty": "Sulit", "title": "Oceans", "side": 14, "words": [
        ("ATLANTIC", "Ocean between America and Europe"),
        ("PACIFIC", "Largest ocean on Earth"),
        ("ARCTIC", "Northernmost ocean"),
        ("INDIAN", "Ocean south of Asia"),
        ("CORAL", "Colorful reef builder"),
        ("REEF", "Rocky ridge near the surface"),
        ("TIDE", "Regular rise and fall of the sea"),
        ("WAVE", "Ridge of water on the sea"),
        ("CURRENT", "Steady flow of water in the sea"),
    ]},
    {"pack": "internasional", "difficulty": "Sulit", "title": "Science", "side": 14, "words": [
        ("ENZYME", "Protein that speeds up reactions"),
        ("NEUTRON", "Particle with no charge in the nucleus"),
        ("PROTON", "Positively charged particle"),
        ("GENE", "Unit of heredity"),
        ("CELL", "Basic unit of life"),
        ("MOLECULE", "Group of bonded atoms"),
        ("VACCINE", "Preparation that builds immunity"),
        ("MICROBE", "Tiny living organism"),
        ("PHOTON", "Particle of light"),
    ]},
    {"pack": "internasional", "difficulty": "Ahli", "title": "World Cities", "side": 15, "words": [
        ("TOKYO", "Capital of Japan"),
        ("PARIS", "City of the Eiffel Tower"),
        ("LONDON", "Capital on the Thames"),
        ("BERLIN", "German capital with a famous wall history"),
        ("CAIRO", "Capital on the Nile"),
        ("SYDNEY", "Largest city in Australia"),
        ("LAGOS", "Largest city in Nigeria"),
        ("MADRID", "Spanish capital"),
        ("DUBAI", "City with a famous tall tower"),
        ("LIMA", "Capital of Peru"),
    ]},
    {"pack": "internasional", "difficulty": "Ahli", "title": "Technology", "side": 15, "words": [
        ("BINARY", "Number system with only two digits"),
        ("COMPILER", "Translates source code into machine code"),
        ("KERNEL", "Core of an operating system"),
        ("ROUTER", "Device that directs network traffic"),
        ("SERVER", "Machine that serves requests"),
        ("BROWSER", "Program for surfing the web"),
        ("PIXEL", "Smallest dot on a screen"),
        ("CACHE", "Fast temporary storage"),
        ("ALGORITHM", "Step-by-step procedure for a task"),
        ("THREAD", "Smallest unit of execution in a program"),
    ]},
]



def try_place(words, rng, max_side):
    """Mencoba menempatkan semua kata. Mengembalikan daftar kata dengan posisi, atau None."""
    order = list(words)
    rng.shuffle(order)
    order.sort(key=lambda w: -len(w[0]))

    first_answer, first_clue = order[0]
    placed = [{"answer": first_answer, "clue": first_clue, "row": 0, "col": 0, "across": True}]

    for answer, clue in order[1:]:
        candidates = []
        for p in placed:
            for i, pch in enumerate(p["answer"]):
                for j, wch in enumerate(answer):
                    if pch != wch:
                        continue
                    if p["across"]:
                        candidates.append((p["row"] - j, p["col"] + i, False))
                    else:
                        candidates.append((p["row"] + i, p["col"] - j, True))
        rng.shuffle(candidates)

        done = False
        for row, col, across in candidates:
            trial = placed + [{"answer": answer, "clue": clue, "row": row, "col": col, "across": across}]
            if build(trial)[1]:
                continue
            shifted = [dict(w) for w in trial]
            rows, cols = normalize(shifted)
            if rows > max_side or cols > max_side:
                continue
            if find_problem(shifted, rows, cols) is None:
                placed = trial
                done = True
                break
        if not done:
            return None
    return placed


def normalize(placed):
    """Menggeser kata agar pojok kiri atas kotak menjadi (0, 0), lalu menghitung ukuran."""
    min_r = min(w["row"] for w in placed)
    min_c = min(w["col"] for w in placed)
    for w in placed:
        w["row"] -= min_r
        w["col"] -= min_c
    cells, _ = build(placed)
    rows = max(r for r, _ in cells) + 1
    cols = max(c for _, c in cells) + 1
    return rows, cols


def make_level(index, spec):
    rng = random.Random(1000 + index)
    for _ in range(40000):
        placed = try_place(spec["words"], rng, spec["side"])
        if placed:
            rows, cols = normalize(placed)
            return {
                "id": index + 1,
                "pack": spec["pack"],
                "difficulty": spec["difficulty"],
                "title": spec["title"],
                "rows": rows,
                "cols": cols,
                "words": [
                    {"answer": w["answer"], "clue": w["clue"], "row": w["row"],
                     "col": w["col"], "across": w["across"]}
                    for w in sorted(placed, key=lambda w: (w["row"], w["col"], not w["across"]))
                ],
            }
    raise SystemExit(f"Gagal menyusun level '{spec['title']}'. Coba kurangi kata.")


def main():
    levels = [make_level(i, spec) for i, spec in enumerate(LEVELS)]
    out = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..",
                       "app", "src", "main", "assets", "levels.json")
    with open(out, "w", encoding="utf-8") as f:
        json.dump({"levels": levels}, f, ensure_ascii=False, indent=2)
        f.write("\n")
    for lv in levels:
        print(f"Level {lv['id']} [{lv['pack']}/{lv['difficulty']}] {lv['title']}: {lv['rows']}x{lv['cols']}, {len(lv['words'])} kata")
    print("Tersimpan:", os.path.normpath(out))


if __name__ == "__main__":
    main()
