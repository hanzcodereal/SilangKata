# Silang Kata

Teka-teki silang (TTS) Bahasa Indonesia untuk Android. Dibuat dengan Kotlin tanpa engine game.

- **Versi:** 1.0.0
- **Developer:** Hanz Dev Official
- **Kontak:** hanzzcodee@gmail.com
- **Dukung:** https://saweria.co/hanzreally

## Fitur

- 18 level dalam dua paket:
  - **Paket Indonesia** (10 level): petunjuk Bahasa Indonesia, dari Mudah sampai Ahli.
  - **Paket Internasional** (8 level): petunjuk Bahasa Inggris, dari Mudah sampai Ahli.
- Kesulitan naik bertahap: jumlah kata bertambah, kata makin panjang, dan petunjuk makin sulit.
- Level terbuka berurutan dalam satu paket. Selesaikan level sebelumnya untuk membuka berikutnya.
- Keyboard huruf di dalam game, tidak butuh keyboard HP.
- Bantuan: membuka satu huruf. Bantuan awal 5 dan bertambah 2 setiap level selesai.
- Periksa: menandai huruf yang belum tepat dengan warna merah.
- Progres tersimpan otomatis di perangkat.

## Struktur

```
app/src/main/java/com/hanzdev/silangkata/   kode Kotlin (menu, level, papan, progres)
app/src/main/assets/levels.json             data level (dibuat oleh tools/generate_levels.py)
app/src/main/res/                           ikon, tema, string
tools/generate_levels.py                    membuat ulang levels.json dari daftar kata
tools/validate_levels.py                    memeriksa semua level (dijalankan juga di CI)
.github/workflows/build-apk.yml             build APK rilis, hanya manual
```

## Mengubah atau menambah level

1. Edit daftar `LEVELS` di `tools/generate_levels.py`. Setiap level punya `pack` (`indonesia` atau `internasional`), `difficulty` (Mudah, Sedang, Sulit, Ahli), `side` (batas ukuran papan), dan daftar kata. Kata harus huruf besar, tanpa spasi, minimal 2 huruf.
2. Jalankan:
   ```
   python3 tools/generate_levels.py
   python3 tools/validate_levels.py app/src/main/assets/levels.json
   ```

## Build APK di GitHub (manual)

1. Buka tab **Actions** > **Build APK Rilis** > **Run workflow**.
2. Tunggu sampai selesai. APK tersedia di **Artifacts** dan di **Releases** (tag `v1.0.0`).

Workflow tidak berjalan otomatis saat ada push. Hanya jalan ketika kamu tekan Run workflow.

### Kunci tanda tangan (disarankan)

Tanpa secret, workflow memakai kunci sementara yang berubah setiap build. APK hasilnya bisa diinstal, tetapi tidak bisa dipasang sebagai pembaruan di atas versi sebelumnya. Untuk rilis berikutnya, buat kunci sekali lalu simpan di Settings > Secrets and variables > Actions:

| Secret | Isi |
|---|---|
| `KEYSTORE_BASE64` | isi file `.jks` dalam base64 (`base64 -w0 release.jks`) |
| `KEYSTORE_PASSWORD` | password keystore |
| `KEY_ALIAS` | alias kunci |
| `KEY_PASSWORD` | password kunci |

Buat kunci dengan:
```
keytool -genkeypair -keystore release.jks -storetype PKCS12 -alias silangkata \
  -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Hanz Dev Official, O=Hanz Dev Official, C=ID"
```
Simpan `release.jks` di tempat aman dan jangan di-commit.

## Membuka di Android Studio

Buka folder ini di Android Studio (Koala atau lebih baru). Android Studio akan membuat Gradle wrapper dan mengunduh SDK yang dibutuhkan. Lalu jalankan di emulator atau HP dengan Android 8.0 ke atas.
